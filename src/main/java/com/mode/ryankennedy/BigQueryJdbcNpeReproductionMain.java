package com.mode.ryankennedy;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

public class BigQueryJdbcNpeReproductionMain {
    private static final String CREATE_TABLE_DDL = "create table if not exists example.bq_npe_repro (" +
            "id numeric, " +
            "data string, " +
            "session_id string, " +
            "created_at date, " +
            "updated_at date, " +
            "resource_data string)";
    public static final String BIGQUERY_JDBC_URL = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443";

    public static void main(String[] args) throws SQLException {
        // Configure the connection properties. Set ProjectId, OAuthServiceAcctEmail, and
        // OAuthPvtKeyPath accordingly.
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("ProjectId", "YOUR PROJECT ID GOES HERE");
        connectionProperties.setProperty("OAuthServiceAcctEmail", "YOUR SERVICE ACCOUNT EMAIL ADDRESS GOES HERE");
        connectionProperties.setProperty("OAuthPvtKeyPath", "PATH TO YOUR P12 PRIVATE KEY GOES HERE");
        connectionProperties.setProperty("OAuthType", "0");
        connectionProperties.setProperty("QueryDialect", "SQL");

        // Make a connection to the database
        try (Connection connection = DriverManager.getConnection(BIGQUERY_JDBC_URL, connectionProperties)) {
            try (Statement statement = connection.createStatement()) {
                // Create the test table if it doesn't already exist
                statement.execute(CREATE_TABLE_DDL);
            }

            try (Statement statement = connection.createStatement()) {
                // Execute a version of the query without the newline. It will succeed.
                statement.execute("select id,updated_at FROM example.bq_npe_repro");
            }

            try (Statement statement = connection.createStatement()) {
                // Execute a version of the query with a newline immediately before `updated_at`.
                // It will fail because the regular expressions in BQSQLExecutor trick it into
                // thinking this is a DML statement.
                statement.execute("select id,\nupdated_at FROM example.bq_npe_repro");
            }
        }
    }
}
