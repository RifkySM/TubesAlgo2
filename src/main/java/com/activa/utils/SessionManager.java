package com.activa.utils;

import com.activa.models.User;
import java.sql.Connection;
import java.sql.SQLException;

/**
 * Manages the application-wide user session using the Singleton pattern.
 * This ensures there is only one instance of the session state (user and db connection)
 * accessible throughout the entire application.
 */
public class SessionManager {
    private static SessionManager instance;
    private User currentUser;
    private Connection databaseConnection;

    /**
     * The constructor is private to prevent instantiation from outside the class.
     * This is a key part of the Singleton pattern.
     */
    private SessionManager() {
    }

    /**
     * Provides a global access point to the single instance of the SessionManager.
     * The 'synchronized' keyword makes it thread-safe.
     *
     * @return The single instance of SessionManager.
     */
    public static synchronized SessionManager getInstance() {
        if (instance == null) {
            instance = new SessionManager();
        }
        return instance;
    }

    /**
     * Checks if a user is currently logged into the session.
     *
     * @return true if a user is logged in, false otherwise.
     */
    public boolean isLoggedIn() {
        return this.currentUser != null;
    }

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    public User getCurrentUser() {
        return this.currentUser;
    }

    public void setDatabaseConnection(Connection connection) {
        this.databaseConnection = connection;
    }

    public Connection getDatabaseConnection() {
        return this.databaseConnection;
    }

    /**
     * Safely closes the database connection if it's open.
     */
    public void closeConnection() {
        try {
            if (databaseConnection != null && !databaseConnection.isClosed()) {
                databaseConnection.close();
                this.databaseConnection = null; // Set to null after closing
                System.out.println("Database connection closed successfully.");
            }
        } catch (SQLException e) {
            System.err.println("Error closing database connection: " + e.getMessage());
        }
    }

    /**
     * Clears all session data and closes the database connection.
     * Effectively logs the user out and resets the session state.
     */
    public void clearSession() {
        this.currentUser = null;
        closeConnection();
    }
}

