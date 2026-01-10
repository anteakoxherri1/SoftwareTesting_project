package com.electronicstore.controller;

import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import java.io.IOException;
import java.util.List;

public class LoginController {
    private static final String USERS_FILE = "users.dat";
    private SessionState sessionState;

    public LoginController() {
        this.sessionState = SessionState.getInstance();
    }

    public boolean login(String username, String password) {
        try {
            List<User> users = FileHandler.readListFromFile(USERS_FILE);

            for (User user : users) {
                if (user.getUsername().equals(username)) {
                    if (user.login(username, password)) {
                        sessionState.startSession(user);
                        return true;
                    }
                    return false; // Password incorrect
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return false; // User not found
    }

    public void logout() {
        if (sessionState.isLoggedIn()) {
            sessionState.endSession();
        }
    }

    public boolean changePassword(String oldPassword, String newPassword) {
        if (!sessionState.isLoggedIn()) {
            return false;
        }

        User currentUser = sessionState.getCurrentUser();
        return currentUser.changePassword(oldPassword, newPassword);
    }

    public User getCurrentUser() {
        return sessionState.getCurrentUser();
    }
}