package org.example.collegemanagement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;


public class DatabaseConnector {

    //database fields

    private static final String JDBC_URL = "jdbc:mysql://localhost:3306/collegeconsole";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "root";

    // Method to establish a connection to the MySQL database
    public static Connection getConnection() throws SQLException {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(JDBC_URL, USERNAME, PASSWORD);
            System.out.println("Database connected successfully.");
        } catch (SQLException e) {
            System.err.println("Error connecting to the database: " + e.getMessage());
            throw e;
        }
        return connection;
    }
}


