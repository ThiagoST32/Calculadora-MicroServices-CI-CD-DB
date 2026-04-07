package com.trevisan.CalculadoraMicroServicesDB.Infra;

import lombok.extern.slf4j.Slf4j;
import org.jspecify.annotations.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.support.EncodedResource;

import javax.sql.DataSource;
import java.sql.*;
import java.util.*;

import static org.springframework.jdbc.datasource.init.ScriptUtils.executeSqlScript;

@Configuration
@Slf4j
public class ConfigDB implements CommandLineRunner{

    @Value("${spring.datasource.url}")
    private String datasourceUrl;
    @Value("${spring.datasource.username}")
    private String datasourceUsername;
    @Value("${spring.datasource.password}")
    private String datasourcePassword;
    @Value("${spring.datasource.driver-class-name}")
    private String datasourceDriverClassName;

    private static final List<String> REQUIRED_PERMISSIONS = Arrays.asList(
            "SELECT", "INSERT", "UPDATE", "DELETE", "EXECUTE"
    );

    private static final String DatabaseTypeMysql = "MySQL";
    private static final String DatabaseTypePostgres = "Postgres";
    private static final String DatabaseTypeSqlServer = "SqlServer";
    private static final String DatabaseTypeH2 = "H2";

    private final DataSource dataSource;

    @Autowired
    public ConfigDB(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    public void checkConnection(){
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
            throw new RuntimeException(ex);
        }
    }

    public void validateConnection(){
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
            throw new RuntimeException(ex);
        }
    }

    public void testConnection(){
        log.info("Testing connection DB");
        try {
            Connection connection = getConnection();

            String sqlQuery = "SELECT * FROM OPERATIONS;";
            Statement statement = connection.createStatement();

            ResultSet resultOfStatementExecuted = statement.executeQuery(sqlQuery);
            
            resultOfStatementExecuted.close();
            connection.close();
            log.info("Statement executed with successfully");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void verifyUserPermissions() throws SQLException {
        String sqlQuery = """
            SELECT * FROM USER_PRIVILEGES WHERE GRANTEE = "'calcUserDB'@'%'";
            """;

        try (Connection newConnection = getConnectionToAnotherDatabase()) {
            PreparedStatement stmt = newConnection.prepareStatement(sqlQuery);
            log.info("Preparing the statement to be executed.");

            ResultSet rs = stmt.executeQuery();

            //Coleta todas as permissões concedidas ao usuário
            Map<String, Boolean> permissionStatus = new HashMap<>();
            Set<String> grantedPermissions = new HashSet<>();
            log.info("Collecting permissions granted to the user.");
            while (rs.next()){
                grantedPermissions.add(rs.getString("PRIVILEGE_TYPE"));
            }

            //Verifica cada permissão requerida pelo usuário
            log.info("Checking permissions granted to the user");
            for (String required : REQUIRED_PERMISSIONS){
                permissionStatus.put(required, grantedPermissions.contains(required));
            }

            if (permissionStatus.containsValue(false)){
                log.info("Attempting to apply missing permissions to the user.");
                applyPermissionsToUser();
            }
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public void applyPermissionsToUser() throws SQLException{
        log.info("Initialize script to apply permissions");

        try (Connection newConnection = getConnectionToAnotherDatabase()) {
            String sqlFile = getDbTypeGrantedPermissions(newConnection);
            EncodedResource sqlQueryPermissionsGranted = new EncodedResource(new ClassPathResource(sqlFile));
            executeSqlScript(newConnection, sqlQueryPermissionsGranted);
            newConnection.close();
            log.info("Permissions applied successfully.");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void checkIfTablesExist(){
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
            throw new RuntimeException(e.getMessage());
        }
    }

    public Connection getConnection() throws SQLException {
        dataSource.setLoginTimeout(10);
        return dataSource.getConnection();
    }

    public void creatingTablesOnDbs(Connection connection) {
        log.info("Creating tables...");
        try {
            String sqlFile = getDbTypeCreateTables(connection);
            EncodedResource sqlQuery = new EncodedResource(new ClassPathResource(sqlFile));
            executeSqlScript(connection, sqlQuery);
            log.info("Tables created with successfully!");
            connection.close();
        } catch (SQLException ex) {
            throw new RuntimeException(ex.getMessage());
        }
    }

    public @NonNull String getDbTypeCreateTables(Connection connection) throws SQLException {
        String dbType = connection.getMetaData().getDatabaseProductName();
        return switch (dbType){
            case DatabaseTypeMysql, DatabaseTypeH2 -> "DBQuerys/MySQL/V1_CREATE_TABLES_DB.sql";

            case DatabaseTypePostgres -> "DBQuerys/Postgres/V1_CREATE_TABLES_DB.sql";

            case DatabaseTypeSqlServer -> "DBQuerys/SqlServer/V1_CREATE_TABLES_DB.sql";

            default -> throw new IllegalStateException("Unexpected value db type: " + dbType);
        };
    }

    public static @NonNull String getDbTypeGrantedPermissions(Connection connection) throws SQLException {
        String dbType = connection.getMetaData().getDatabaseProductName();
        String sqlFile;
        switch (dbType){
            case DatabaseTypeMysql -> sqlFile = "DBQuerys/MySQL/V2_USER_PERMISSIONS_MYSQL.sql";

            case DatabaseTypePostgres -> sqlFile = "DBQuerys/Postgress/V2_USER_PERMISSIONS_POSTGRES.sql";

            case DatabaseTypeSqlServer -> sqlFile = "DBQuerys/SqlServer/V2_USER_PERMISSIONS_SQLSERVER.sql";

            default -> throw new IllegalStateException("Unexpected value: " + dbType);
        }
        return sqlFile;
    }

    @Override
    public void run(String @NonNull ... args) {
        try {
            checkConnection();
            checkIfTablesExist();
            verifyUserPermissions();
            validateConnection();
            testConnection();
        } catch (Exception ex){
            log.error("Error while executing test connection {}", ex.getMessage());
        }
    }

    private Connection getConnectionToAnotherDatabase(){
        try {
            Class.forName(datasourceDriverClassName);
            String urlWithDatabase = datasourceUrl.replaceAll("/[^/]*$", "/" + "information_schema");

            return DriverManager.getConnection(
                    urlWithDatabase,
                    datasourceUsername,
                    datasourcePassword
            );
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException(e);
        }
    }
}
