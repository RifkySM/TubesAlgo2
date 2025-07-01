package com.activa.services;

import com.activa.models.User;
import com.activa.repositories.UserRepository;
import com.activa.utils.Helper;
import com.activa.utils.SessionManager;

import java.util.Optional;

public class LoginService {
    private final UserRepository userRepository;

    /**
     * Constructs a new LoginService and initializes its UserRepository.
     */
    public LoginService() {
        this.userRepository = new UserRepository();
    }

    /**
     * Attempts to authenticate a user with the given credentials. If successful,
     * the user is stored in the SessionManager.
     *
     * @param username The username to check.
     * @param password The plain-text password to verify.
     * @return {@code true} if the credentials are valid and the user is authenticated,
     * {@code false} otherwise.
     */
    public Boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return false;
        }

        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                return false;
            }
            User user = optionalUser.get();
            boolean isPasswordCorrect = Helper.checkPassword(password, user.getPassword());

            if (isPasswordCorrect) {
                SessionManager.getInstance().setCurrentUser(user);
                return true;
            } else {
                return false;
            }

        } catch (Exception e) {
            System.err.println("An error occurred during login for user: " + username);
            e.printStackTrace();
            return false;
        }
    }
}