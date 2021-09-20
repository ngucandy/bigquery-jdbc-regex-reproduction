package com.mode.andynguyen;

import java.lang.management.ManagementFactory;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BigQueryJdbcRegexReproductionMain {

    private static final Logger LOGGER = Logger.getLogger("main");

    public static final String BIGQUERY_JDBC_URL = "jdbc:bigquery://https://www.googleapis.com/bigquery/v2:443";

    public static void main(String[] args) throws Exception {
        // Configure the connection properties. Set ProjectId, OAuthServiceAcctEmail, and
        // OAuthPvtKeyPath accordingly.
        final Properties connectionProperties = new Properties();
        connectionProperties.setProperty("ProjectId", "YOUR PROJECT ID GOES HERE");
        connectionProperties.setProperty("OAuthServiceAcctEmail", "YOUR SERVICE ACCOUNT EMAIL ADDRESS GOES HERE");
        connectionProperties.setProperty("OAuthPvtKeyPath", "PATH TO YOUR P12 PRIVATE KEY GOES HERE");
        connectionProperties.setProperty("OAuthType", "0");
        connectionProperties.setProperty("QueryDialect", "SQL");
        connectionProperties.setProperty("LogLevel", "6");
        connectionProperties.setProperty("LogPath", ".");

        ExecutorService executor = Executors.newSingleThreadExecutor();

        // Attempts to close the statement will block when the driver is stuck in regex, so just let the
        // JVM die.
        try {
            // Make a connection to the database
            Connection connection = DriverManager.getConnection(BIGQUERY_JDBC_URL, connectionProperties);

            // SQL comments with lots of hyphens take a long time to get through regex processing. The time to process
            // is directly related to the number of hyphens and appears superlinear (maybe exponential).
            final String query = "-- ----------------------------------------------------------------------\nSELECT 1";
            LOGGER.info("Executing Query: " + query);
            Statement statement = connection.createStatement();
            Future f = executor.submit(() -> statement.execute(query));
            f.get(5, TimeUnit.SECONDS);
            LOGGER.info("Query finished");
        } catch (TimeoutException e) {
            LOGGER.log(Level.SEVERE, "Query took longer than 5 secs", e);
            // dump the threads
            ThreadMXBean mb = ManagementFactory.getThreadMXBean();
            for (ThreadInfo ti : mb.dumpAllThreads(true, true)) {
                System.out.print(ti.toString());
            }
            // trying to close the statement or connection will block, so just exit
            System.exit(1);
        }
    }
}
