package com.trevisan.CalculadoraMicroServicesDB.Infra;

import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.sql.DataSource;
import java.lang.reflect.InvocationTargetException;
import java.sql.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
@DisplayName("ConfigDB Tests")
public class ConfigDBTest {

    @InjectMocks
    private ConfigDB configDB;

    @Mock
    private DataSource dataSource;

    @Mock
    private Connection connection;

    @Mock
    private Statement statement;

    @Mock
    private PreparedStatement preparedStatement;

    @Mock
    private ResultSet resultSet;

    @Mock
    private DatabaseMetaData metadata;

    @BeforeEach
    void setUp() throws SQLException {
        configDB = new ConfigDB(dataSource);
        lenient().when(dataSource.getConnection()).thenReturn(connection);
    }

    @Test
    @DisplayName("Should check connection successfully when connection is open")
    void testCheckConnection_Success() throws SQLException {
        when(connection.isClosed()).thenReturn(false);

        assertDoesNotThrow(() -> configDB.checkConnection());

        verify(connection).isClosed();
        verify(connection).close();
    }

    @Test
    @DisplayName("Should throw RuntimeException when connection is already closed")
    void testCheckConnection_ConnectionClosed_ThrowsRuntimeException() throws SQLException {
        when(connection.isClosed()).thenReturn(true);

        assertThrows(RuntimeException.class, () -> configDB.checkConnection());

        verify(connection).isClosed();
        verify(connection, never()).close();
    }

    @Test
    @DisplayName("Should throw RuntimeException when DataSource fails to provide connection")
    void testCheckConnection_SQLException_ThrowsRuntimeException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Connection Error"));

        assertThrows(RuntimeException.class, () -> configDB.checkConnection());
    }

    @Test
    @DisplayName("Should return valid connection from DataSource")
    void testGetConnection_Success() throws SQLException {
        Connection result = configDB.getConnection();

        assertNotNull(result);
        assertEquals(result, connection);
        verify(dataSource).getConnection();
    }

    @Test
    @DisplayName("Should propagate SQLException when getting connection fails")
    void testValidateConnection_SuccessfulValidation() throws SQLException {
        when(connection.isValid(10)).thenReturn(true);

        assertDoesNotThrow(() -> configDB.validateConnection());

        verify(connection).isValid(10);
        verify(connection).close();
    }

    @Test
    @DisplayName("Should propagate SQLException when getting connection fails")
    void testGetConnection_SQLException_PropagatesException() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("DB unreachable"));

        assertThrows(SQLException.class, () -> configDB.getConnection());
    }

    @Test
    @DisplayName("Should validate connection successfully when connection is valid")
    void testValidateConnection_Success() throws SQLException, NoSuchMethodException {
        when(connection.isValid(10)).thenReturn(true);
        var method = ConfigDB.class.getDeclaredMethod("validateConnection");

        assertDoesNotThrow(() -> method.invoke(configDB));

        verify(connection).isValid(10);
        verify(connection).close();
    }

    @Test
    @DisplayName("Should throw RuntimeException when connection is invalid")
    void testValidateConnection_InvalidConnection_ThrowsRuntimeException() throws SQLException, NoSuchMethodException {
        when(connection.isValid(10)).thenReturn(false);

        var method = ConfigDB.class.getDeclaredMethod("validateConnection");
        method.setAccessible(true);

        assertThrows(InvocationTargetException.class, () -> method.invoke(configDB));
    }

    @Test
    void testTestConnections_Success() throws SQLException, NoSuchMethodException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM OPERATIONS;")).thenReturn(resultSet);
        when(resultSet.wasNull()).thenReturn(false);

        var method = ConfigDB.class.getDeclaredMethod("testConnection");
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(configDB));
        verify(statement).executeQuery("SELECT * FROM OPERATIONS;");
        verify(resultSet).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("Should return if user DB contains all permissions granted")
    void testVerifyUserPermissions_AllPermissionsGranted() throws SQLException, NoSuchMethodException {
        when(connection.prepareStatement(anyString())).thenReturn(preparedStatement);
        when(preparedStatement.getResultSet()).thenReturn(resultSet);

        assertNotNull(resultSet);

        when(resultSet.next()).thenReturn(true, true, true, true, true, false);
        when(resultSet.getString("permission_name")).thenReturn(
                "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE"
        );

        var method = ConfigDB.class.getDeclaredMethod("verifyUserPermissions");
        assertNotNull(method);

        assertDoesNotThrow(() -> method.invoke(configDB));

        verify(preparedStatement).setString(1, "calcDB");
        verify(preparedStatement).getResultSet();
    }

    @Test
    @DisplayName("Should return if user all tables on DB exist")
    void testCheckIfTablesExist_TableExist() throws SQLException, NoSuchMethodException {
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SHOW TABLES;")).thenReturn(resultSet);
        when(resultSet.getRow()).thenReturn(1);

        var method = ConfigDB.class.getDeclaredMethod("checkIfTablesExist");
        assertNotNull(method);
        method.setAccessible(true);

        assertDoesNotThrow(() -> method.invoke(configDB));
        verify(statement).executeQuery("SHOW TABLES;");
        verify(resultSet).close();
        verify(connection).close();
    }

    @Test
    @DisplayName("Should return if connection is close after check connection")
    void testIfConnectionIsClosedAfterCheckConnection() throws SQLException {
        when(dataSource.getConnection()).thenReturn(connection);
        when(connection.isClosed()).thenReturn(false);

        doNothing().when(connection).close();
        assertDoesNotThrow(() -> configDB.checkConnection());
        assertNotEquals(true, connection.isClosed());

        verify(connection).close();
    }

    @Test
    @DisplayName("Should throw RuntimeException when connection is invalid")
    void testValidateConnection_WhenInvalidConnection() throws SQLException {
        when(connection.isValid(10)).thenReturn(false);

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> configDB.validateConnection()
        );

        assertNull(exception.getCause());
        verify(connection).isValid(10);
        verify(connection, never()).close();
    }

    @Test
    @DisplayName("Should throw RuntimeException when SQL exception occurs during connection validation")
    void testValidateConnection_WhenSQLExceptionOnGetConnection() throws SQLException {
        when(dataSource.getConnection()).thenThrow(new SQLException("Get connection failed"));

        RuntimeException exception = assertThrows(
                RuntimeException.class,
                () -> configDB.validateConnection()
        );

        assertNotNull(exception.getCause());
        verify(connection, never()).isValid(anyInt());
        verify(connection, never()).close();
    }

    @Test
    @DisplayName("Should verify if db type is correct in path file")
    void testCheckIfTablesExist_NoTables_CreatesTables() throws SQLException {
        when(connection.getMetaData()).thenReturn(metadata);
        when(metadata.getDatabaseProductName()).thenReturn("MySQL");

        String shouldReturnMysqlType = metadata.getDatabaseProductName();
        var createOnMysql = configDB.getDbTypeCreateTables(connection);
        assertNotNull(createOnMysql);
        assertTrue(createOnMysql.contains(shouldReturnMysqlType));

        when(metadata.getDatabaseProductName()).thenReturn("Postgress");
        String shouldReturnPostgressType = metadata.getDatabaseProductName();
        var createOnPostgress = configDB.getDbTypeCreateTables(connection);
        assertNotNull(createOnPostgress);
        assertTrue(createOnPostgress.contains(shouldReturnPostgressType));

        when(metadata.getDatabaseProductName()).thenReturn("SqlServer");
        String shouldReturnSqlServer = metadata.getDatabaseProductName();
        var createOnSqlServer = configDB.getDbTypeCreateTables(connection);
        assertNotNull(createOnSqlServer);
        assertTrue(createOnSqlServer.contains(shouldReturnSqlServer));
    }

    @Test
    @DisplayName("Should verify if connection is called only 4 times")
    void testRun_Success() throws SQLException {
        when(connection.isClosed()).thenReturn(false);
        when(connection.isValid(10)).thenReturn(true);
        when(connection.createStatement()).thenReturn(statement);
        when(statement.executeQuery("SELECT * FROM OPERATIONS;")).thenReturn(resultSet);
        when(resultSet.wasNull()).thenReturn(false);
        when(statement.executeQuery("SHOW TABLES;")).thenReturn(resultSet);
        when(resultSet.getRow()).thenReturn(5);

//        configDB.run();
        verify(connection, times(4)).close();
    }
}