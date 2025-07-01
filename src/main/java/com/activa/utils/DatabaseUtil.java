package com.activa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String USER = "postgres";
    private static final String DATABASE = "activa";
    private static final String PASSWORD = "password";
    private static final String URL = "jdbc:postgresql://localhost:5432/" + DATABASE;

    public static Connection getConnection() {
        try {
            System.out.println("Database Connected Successfully");
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database.");
            e.printStackTrace();
            return null;
        }
    }
}
