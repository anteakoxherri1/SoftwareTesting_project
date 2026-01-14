package com.electronicstore.controller;

import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;

import java.util.List;

public class LoginController {

    private static final String USERS_FILE = "users.dat";
    private final SessionState sessionState;

    public LoginController() {
        this.sessionState = SessionState.getInstance();
    }

    public boolean login(String username, String password) {
        List<User> users = FileHandler.readListFromFile(USERS_FILE);

        for (User user : users) {
            if (user.getUsername().equals(username)) {
                if (user.login(username, password)) {
                    sessionState.startSession(user);
                    return true;
                }
                return false; // password incorrect
            }
        }
        return false; // user not found
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
