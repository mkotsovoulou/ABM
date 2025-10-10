package com.eydap.abm;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseConnector {

    /**
     * Attempts to establish a connection to a Microsoft SQL Server database.
     *
     * @param server   The server name or IP address.
     * @param database The name of the database.
     * @param username The username for the database login.
     * @param password The password for the database login.
     * @return A database Connection object.
     * @throws SQLException if a database access error occurs.
     */
    public static Connection getConnection(String server, String database, String username, String password) throws SQLException {
        // Build the connection URL for MS SQL Server
        // We add 'encrypt=true' and 'trustServerCertificate=true' to avoid common SSL issues
        String url = String.format("jdbc:sqlserver://%s;databaseName=%s;encrypt=true;trustServerCertificate=true;",
                server, database);

        // DriverManager will use the MS SQL JDBC driver we added in pom.xml
        // It will throw an SQLException if the connection fails (e.g., bad credentials, server not found)
        return DriverManager.getConnection(url, username, password);
    }
}
