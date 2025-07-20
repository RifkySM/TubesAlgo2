package com.activa.utils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DatabaseUtil {
    private static final String URL = "jdbc:sqlite:src/main/java/lib/activa.db";

    public static Connection getConnection() {
        try {
            System.out.println("Database Connected Successfully");
            return DriverManager.getConnection(URL);
        } catch (SQLException e) {
            System.err.println("Failed to connect to database.");
            e.printStackTrace();
            return null;
        }
    }
}
