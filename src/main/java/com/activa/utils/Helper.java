package com.activa.utils;
import com.activa.App;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;
import java.util.Optional;

public class Helper {
    /**
     * Hashes a plain-text password using the BCrypt algorithm.
     * <p>
     * A new, random salt is generated for each password with a work factor of 12.
     * The resulting hash string contains the algorithm version, work factor, salt, and hash,
     * making it ideal for storage in a database.
     *
     * @param plainTextPassword The user's original, unhashed password to secure.
     * @return A String containing the BCrypt hash of the password.
     */
    public static String hashPassword(String plainTextPassword) {
        return BCrypt.hashpw(plainTextPassword, BCrypt.gensalt(12));
    }

    /**
     * Verifies a plain-text password against a stored BCrypt hash.
     * <p>
     * This method securely compares the candidate password by hashing it with the
     * salt extracted from the stored hash and checking if the results match.
     *
     * @param candidatePassword    The plain-text password submitted during a login attempt.
     * @param hashedPasswordFromDB The complete BCrypt hash retrieved from the database for the user.
     * @return {@code true} if the candidate password matches the stored hash, {@code false} otherwise.
     */
    public static boolean checkPassword(String candidatePassword, String hashedPasswordFromDB) {
        return BCrypt.checkpw(candidatePassword, hashedPasswordFromDB);
    }

    /**
     * Displays a modal alert dialog with a specified title, message, and alert type.
     * <p>
     * This method waits for the user to close the alert before returning.
     * The header text of the alert is set to null for a cleaner look.
     *
     * @param title     The text to display in the title bar of the dialog.
     * @param message   The main content message to display to the user.
     * @param alertType The type of alert to display (e.g., INFORMATION, WARNING, ERROR).
     */
    public static void showAlert(String title, String message, AlertType alertType) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null); // Recommended for a cleaner dialog
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Opens a new JavaFX window using the given stage, FXML file path, and window title.
     *
     * @param stage     the {@link Stage} to set the scene on
     * @param fxmlPath  the path to the FXML file (must start with a slash, e.g. "/views/auth/login.fxml")
     * @param title     the title to set for the window
     *
     * This method attempts to load the specified FXML file and apply it to the given stage.
     * If the file cannot be loaded or found, it prints an error message to the console.
     */
    public static void openWindow(Stage stage, String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(App.class.getResource(fxmlPath)));
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.setTitle(title);
            stage.centerOnScreen();
            stage.show();
        } catch (IOException e) {
            System.err.println("Failed to load FXML file: " + fxmlPath);
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.err.println("Error: FXML file not found at path: " + fxmlPath);
            e.printStackTrace();
        }
    }
    /**
     * Displays a confirmation dialog with a specified title and message.
     * <p>
     * This dialog shows "OK" and "Cancel" buttons and waits for the user's response.
     *
     * @param title   The text for the dialog's title bar.
     * @param message The main content message for the user.
     * @return {@code true} if the user clicks the "OK" button, {@code false} otherwise.
     */
    public static boolean showConfirmation(String title, String message) {
        Alert alert = new Alert(AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);

        Optional<ButtonType> result = alert.showAndWait();
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
