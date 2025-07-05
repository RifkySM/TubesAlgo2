package com.activa.middlewares;

import com.activa.utils.Helper;
import com.activa.utils.SessionManager;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * A utility class to handle authentication-related navigation.
 * This class provides methods to redirect the user to the login screen.
 */
public class AuthMiddleware {
    /**
     * Holds a static reference to the primary stage of the application.
     * This allows the middleware to change scenes without needing a reference
     * from a specific UI Node.
     */
    private static Stage primaryStage;

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private AuthMiddleware() {
    }

    /**
     * Initializes the AuthMiddleware with the primary stage of the application.
     * THIS METHOD MUST BE CALLED ONCE when the application starts.
     * The best place to call it is in the start() method of your main Application class.
     *
     * Example usage in Main.java:
     *
     * @Override
     * public void start (Stage primaryStage) {
     * AuthMiddleware.setPrimaryStage(primaryStage);
     * // ... your other startup code
     * }
     *
     * @param stage The primary stage of the JavaFX application.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Checks if the user is logged in using the SessionManager.
     * If the user is not logged in, it redirects them to the login screen.
     * This method no longer requires a Node argument.
     */
    public static void checkAuth() {
        if (primaryStage == null) {
            System.err.println("Error: Primary stage is not set in AuthMiddleware. Please call AuthMiddleware.setPrimaryStage() on startup.");
            return;
        }
        if (!SessionManager.getInstance().isLoggedIn()) {
            redirectToLogin();
        }
    }

    /**
     * Closes the current scene and opens the login scene on the same stage.
     * This method no longer requires a Node argument.
     */
    public static void redirectToLogin() {
        if (primaryStage == null) {
            System.err.println("Error: Primary stage is not set in AuthMiddleware. Please call AuthMiddleware.setPrimaryStage() on startup.");
            return;
        }

        Helper.openWindow(primaryStage, "/views/auth/login.fxml", "Login");
    }

}
