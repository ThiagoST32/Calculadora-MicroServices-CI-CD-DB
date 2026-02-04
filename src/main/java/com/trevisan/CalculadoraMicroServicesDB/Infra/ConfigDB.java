package com.trevisan.CalculadoraMicroServicesDB.Infra;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.support.EncodedResource;

import javax.sql.DataSource;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.*;
import java.util.*;

import static org.springframework.jdbc.datasource.init.ScriptUtils.*;

@Configuration
@Slf4j
public class ConfigDB implements CommandLineRunner{

    private static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
            "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE"
    );

    private static final String DatabaseTypeMysql = "MYSQL";
    private static final String DatabaseTypePostgres = "POSTGRES";
    private static final String DatabaseTypeSqlServer = "SQLSERVER";

    private final DataSource dataSource;

    @Autowired
    public ConfigDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    private void checkConnection(){
        log.info("Checking connection DB");
        try {
            if (getConnection().isClosed()){
                log.error("Cannot connect to Database!");
                throw new RuntimeException();
            }
            log.info("Connect to Database successfully");
            getConnection().close();
        } catch (SQLException ex){
            log.error("Error to connect on Database! - {}", String.valueOf(ex.getNextException()));
            throw new RuntimeException();
        }
    }

    private void validateConnection(){
        log.info("Validating connection DB");
        try{
            Connection connection = getConnection();
            //Valide connection on 10 seconds
            boolean reachableConnection = connection.isValid(10);
            if (!reachableConnection){
                log.error("Connection is not valid!");
                throw new RuntimeException();
            }
            log.info("Connection validate!");
            connection.close();
        } catch (SQLException ex){
            log.error("Error to validate connection! - {}", String.valueOf(ex.getNextException()));
            throw new RuntimeException();
        }
    }

    private void testConnection(){
        log.info("Testing connection DB");
        try {
            Connection connection = getConnection();

            String sqlQuery = "SELECT * FROM OPERATIONS;";
            Statement statement = connection.createStatement();

            ResultSet resultOfStatementExecuted = statement.executeQuery(sqlQuery);

            if (resultOfStatementExecuted.wasNull()){
                log.error("Cannot execute statement to testing DB");
                throw new RuntimeException();
            }
            
            resultOfStatementExecuted.close();
            connection.close();
            log.info("Statement executed with successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void verifyUserPermissions(){
        Map<String, Boolean> permissionStatus = new HashMap<>();
        String userDB = "calcDB";

        String sqlQuery = """
            SELECT
                permission_name,
                state_desc
            FROM sys.database_permissions dp
            JOIN sys.database_principals dp2
                ON dp.grantee_principal_id = dp2.principal_id
            WHERE dp2.name = ?
            AND state_desc = 'GRANT'
            """;

        try (PreparedStatement stmt = getConnection().prepareStatement(sqlQuery)) {
            log.info("Preparing the statement to be executed.");
            stmt.setString(1, userDB);
            ResultSet rs = stmt.getResultSet();

            //Coleta todas as permissões concedidas ao usuário
            Set<String> grantedPermissions = new HashSet<>();
            while (rs.next()){
                log.info("Collecting permissions granted to the user.");
                grantedPermissions.add(rs.getString("permission_name"));
            }

            //Verifica cada permissão requerida pelo usuário
            for (String required : REQUIRED_PERMISSIONS){
                log.info("Checking permissions granted to the user");
                permissionStatus.put(required, grantedPermissions.contains(required));
            }

            permissionStatus.forEach((perm, granted) ->
                    System.err.println(perm + ": " + (granted ? "CONCEDIDA" : "FALTANDO"))
            );

            if (permissionStatus.containsValue(false)){
                log.info("Attempting to apply missing permissions to the user.");
                applyPermissionsToUser();
            }

        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    private void applyPermissionsToUser() {
        log.info("Initialize script to apply permissions");
        try (Statement stmt = getConnection().createStatement()) {
            String sqlQueryPermissionsGranted = String.valueOf(new BufferedReader(new FileReader(getDbTypeGrantedPermissions(getConnection()))));
            stmt.executeQuery(sqlQueryPermissionsGranted);

            ResultSet rs = stmt.getResultSet();

            if (rs.wasNull()) {
                log.error("Error to apply sql query");
                throw new RuntimeException();
            }

            getConnection().commit();
            rs.close();
            getConnection().close();
            log.info("Permissions applied successfully.");
        } catch (SQLException | FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }

    private void checkIfTablesExist(){
        log.info("Checking tables on DB");
        try {
            Connection connection = getConnection();

            String sqlQuery = "SHOW TABLES;";
            Statement statement = connection.createStatement();

            ResultSet resultOfStatementExecuted = statement.executeQuery(sqlQuery);

            if (resultOfStatementExecuted.getRow() == 0){
                log.warn("Not exists tables on DB, trying create tables...");
                creatingTablesOnDbs(connection);
            }

            resultOfStatementExecuted.close();
            connection.close();
            log.info("Statement executed to check tables with successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private Connection getConnection() throws SQLException {
        dataSource.setLoginTimeout(10);
        return dataSource.getConnection();
    }

    private void creatingTablesOnDbs(Connection connection) {
        log.info("Creating tables...");
        try {
            String sqlFile = getDbTypeCreateTables(connection);
            EncodedResource sqlQuery = new EncodedResource(new FileSystemResource(sqlFile));
            executeSqlScript(connection, sqlQuery);
            connection.commit();
            log.info("Tables created with successfully!");
            connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getNextException());
        }
    }

    private static @NonNull String getDbTypeCreateTables(Connection connection) throws SQLException {
        String dbType = connection.getMetaData().getDatabaseProductName();
        String sqlFile;
        switch (dbType){
            case DatabaseTypeMysql -> sqlFile = "resources/DBQuerys/MySQL/V1_CREATE_TABLES_DB.sql";

            case DatabaseTypePostgres -> sqlFile = "resources/DBQuerys/Postgress/V1_CREATE_TABLES_DB.sql";

            case DatabaseTypeSqlServer -> sqlFile = "resources/DBQuerys/SqlServer/V1_CREATE_TABLES_DB.sql";

            default -> throw new IllegalStateException("Unexpected value db type: " + dbType);
        }
        return sqlFile;
    }

    private static @NonNull String getDbTypeGrantedPermissions(Connection connection) throws SQLException {
        String dbType = connection.getMetaData().getDatabaseProductName();
        String sqlFile;
        switch (dbType){
            case DatabaseTypeMysql -> sqlFile = "resources/DBQuerys/MySQL/V2_USER_PERMISSIONS_MYSQL.sql";

            case DatabaseTypePostgres -> sqlFile = "resources/DBQuerys/Postgress/V2_USER_PERMISSIONS_POSTGRES.sql";

            case DatabaseTypeSqlServer -> sqlFile = "resources/DBQuerys/SqlServer/V2_USER_PERMISSIONS_SQLSERVER.sql";

            default -> throw new IllegalStateException("Unexpected value: " + dbType);
        }
        return sqlFile;
    }

    @Override
    public void run(String @NonNull ... args){
        checkConnection();
        validateConnection();
        testConnection();
        checkIfTablesExist();
        verifyUserPermissions();
    }
}
