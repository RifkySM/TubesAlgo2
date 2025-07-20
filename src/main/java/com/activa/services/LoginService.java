package com.activa.services;

import com.activa.models.Member;
import com.activa.models.User;
import com.activa.repositories.MemberRepository;
import com.activa.repositories.UserRepository;
import com.activa.utils.Helper;
import com.activa.utils.SessionManager;
import javafx.scene.control.Alert;

import java.util.Optional;

public class LoginService {
    private final UserRepository userRepository;
    private final MemberRepository memberRepository;

    /**
     * Constructs a new LoginService and initializes its repositories.
     */
    public LoginService() {
        this.userRepository = new UserRepository();
        this.memberRepository = new MemberRepository();
    }

    /**
     * Attempts to authenticate a user. If the user is a member, it also checks
     * if the member account is active before granting access.
     *
     * @param username The username to check.
     * @param password The plain-text password to verify.
     * @return {@code true} if credentials are valid and the user is allowed to log in,
     * {@code false} otherwise.
     */
    public Boolean login(String username, String password) {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            Helper.showAlert("Login Failed", "Username and password cannot be empty.", Alert.AlertType.ERROR);
            return false;
        }

        try {
            Optional<User> optionalUser = userRepository.findByUsername(username);

            if (optionalUser.isEmpty()) {
                Helper.showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
                return false;
            }

            User user = optionalUser.get();
            boolean isPasswordCorrect = Helper.checkPassword(password, user.getPassword());

            if (isPasswordCorrect) {
                if ("member".equalsIgnoreCase(user.getRole())) {
                    Optional<Member> optionalMember = memberRepository.findByUserId(user.getId());

                    if (optionalMember.isPresent()) {
                        Member member = optionalMember.get();
                        if (!member.isActive()) {
                            Helper.showAlert("Login Failed", "Your member account is inactive. Please contact an administrator.", Alert.AlertType.WARNING);
                            return false;
                        }
                    } else {
                        Helper.showAlert("Login Failed", "Could not find associated member data. Please contact an administrator.", Alert.AlertType.ERROR);
                        return false;
                    }
                }

                SessionManager.getInstance().setCurrentUser(user);
                return true;

            } else {
                Helper.showAlert("Login Failed", "Invalid username or password.", Alert.AlertType.ERROR);
                return false;
            }

        } catch (Exception e) {
            System.err.println("An error occurred during login for user: " + username);
            e.printStackTrace();
            Helper.showAlert("System Error", "A system error occurred during login. Please try again later.", Alert.AlertType.ERROR);
            return false;
        }
    }
}
