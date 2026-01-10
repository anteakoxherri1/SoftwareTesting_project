package com.electronicstore.model.utils;

import com.electronicstore.model.users.User;
import java.time.LocalDateTime;

public class SessionState {
    private static SessionState instance;
    private User currentUser;
    private LocalDateTime loginTime;
    private String currentSection;

    // Private constructor for Singleton pattern
    private SessionState() {}

    // Get singleton instance
    public static SessionState getInstance() {
        if (instance == null) {
            instance = new SessionState();
        }
        return instance;
    }

    // Session management methods
    public void startSession(User user) {
        this.currentUser = user;
        this.loginTime = LocalDateTime.now();
        this.currentSection = "main";
    }

    public void endSession() {
        if (this.currentUser != null) {
            this.currentUser.logout();
        }
        this.currentUser = null;
        this.loginTime = null;
        this.currentSection = null;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }

    // Getters and setters
    public User getCurrentUser() {
        return currentUser;
    }

    public LocalDateTime getLoginTime() {
        return loginTime;
    }

    public String getCurrentSection() {
        return currentSection;
    }

    public void setCurrentSection(String section) {
        this.currentSection = section;
    }

    // User type checking methods
    public boolean isCashier() {
        return currentUser != null &&
                currentUser.getClass().getSimpleName().equals("Cashier");
    }

    public boolean isManager() {
        return currentUser != null &&
                currentUser.getClass().getSimpleName().equals("Manager");
    }

    public boolean isAdministrator() {
        return currentUser != null &&
                currentUser.getClass().getSimpleName().equals("Administrator");
    }

    // Session information
    public String getSessionInfo() {
        if (!isLoggedIn()) {
            return "No active session";
        }

        return String.format("""
            Current Session:
            User: %s (%s)
            Login Time: %s
            Current Section: %s
            """,
                currentUser.getName(),
                currentUser.getClass().getSimpleName(),
                loginTime,
                currentSection);
    }

    @Override
    public String toString() {
        return "SessionState[user=" +
                (currentUser != null ? currentUser.getUsername() : "none") +
                ", loginTime=" + loginTime +
                ", section=" + currentSection + "]";
    }
}