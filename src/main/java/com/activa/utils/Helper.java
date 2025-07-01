package com.activa.utils;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;

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
}
