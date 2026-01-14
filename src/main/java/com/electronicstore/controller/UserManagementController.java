/*package com.electronicstore.controller;

import com.electronicstore.model.users.*;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;

import java.nio.file.Files;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserManagementController {

    private static final String USERS_FILE = "users.dat";
    private static final Logger LOGGER =
            Logger.getLogger(UserManagementController.class.getName());

    private final SessionState sessionState;

    public UserManagementController() {
        this.sessionState = SessionState.getInstance();
    }

    // =====================
    // ADD USER
    // =====================
    public boolean addUser(String username, String password, String name,
                           String email, String phone, String userType, String sector) {

        List<User> users = loadUsers();
        boolean isFirstUser = users.isEmpty();

        if (userType.equalsIgnoreCase("administrator")
                && !isFirstUser
                && !sessionState.isAdministrator()) {
            return false;
        }

        if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
            return false;
        }

        String userId = "U" + UUID.randomUUID().toString().substring(0, 8);
        User newUser;

        switch (userType.toLowerCase()) {
            case "cashier" ->
                    newUser = new Cashier(userId, username, password, name, email, phone, sector);
            case "manager" ->
                    newUser = new Manager(userId, username, password, name, email, phone);
            case "administrator" ->
                    newUser = new Administrator(userId, username, password, name, email, phone);
            default ->
                    throw new IllegalArgumentException("Invalid user type");
        }

        users.add(newUser);

        try {
            Files.createDirectories(FileHandler.DATA_DIRECTORY);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Failed creating data directory", e);
            return false;
        }

        // ✅ RENDITJA E SAKTË
        FileHandler.saveListToFile(USERS_FILE, users);
        return true;
    }

    // =====================
    // UPDATE USER
    // =====================
    public boolean updateUser(String userId, Map<String, String> updates) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        List<User> users = loadUsers();

        Optional<User> userOpt = users.stream()
                .filter(u -> u.getId().equals(userId))
                .findFirst();

        if (userOpt.isEmpty()) {
            return false;
        }

        User user = userOpt.get();

        updates.forEach((key, value) -> {
            switch (key) {
                case "name" -> user.setName(value);
                case "email" -> user.setEmail(value);
                case "phone" -> user.setPhone(value);
                case "password" -> user.setPassword(value);
            }
        });

        // ✅ RENDITJA E SAKTË
        FileHandler.saveListToFile(USERS_FILE, users);
        return true;
    }

    // =====================
    // DELETE USER
    // =====================
    public boolean deleteUser(String userId) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        List<User> users = loadUsers();
        boolean removed = users.removeIf(u -> u.getId().equals(userId));

        if (removed) {
            // ✅ RENDITJA E SAKTË
            FileHandler.saveListToFile(USERS_FILE, users);
        }

        return removed;
    }

    // =====================
    // GET USERS
    // =====================
    public List<User> getAllUsers() {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }
        return loadUsers();
    }

    public List<User> getUsersByType(String userType) {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }

        return loadUsers().stream()
                .filter(u -> u.getClass().getSimpleName().equalsIgnoreCase(userType))
                .toList();
    }

    // =====================
    // PASSWORD MANAGEMENT
    // =====================
    public boolean resetPassword(User user) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        List<User> users = loadUsers();

        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                u.setPassword("password");

                // ✅ RENDITJA E SAKTË
                FileHandler.saveListToFile(USERS_FILE, users);
                return true;
            }
        }
        return false;
    }

    public boolean editUser(User user, String username, String name,
                            String email, String phone, String role, String sector) {

        if (!sessionState.isAdministrator()) {
            return false;
        }

        List<User> users = loadUsers();

        for (User u : users) {
            if (u.getId().equals(user.getId())) {
                u.setUsername(username);
                u.setName(name);
                u.setEmail(email);
                u.setPhone(phone);

                if (u instanceof Cashier cashier) {
                    cashier.setSector(sector);
                }

                // ✅ RENDITJA E SAKTË
                FileHandler.saveListToFile(USERS_FILE, users);
                return true;
            }
        }
        return false;
    }

    // =====================
    // LOAD USERS
    // =====================
    private List<User> loadUsers() {
        return FileHandler.readListFromFile(USERS_FILE);
    }
}
*/
