package com.electronicstore.controller;

import com.electronicstore.model.users.*;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class UserManagementController {
	private static final String USERS_FILE = "users.dat";
    private final SessionState sessionState;

    public UserManagementController() {
        this.sessionState = SessionState.getInstance();
    }

    public boolean addUser(String username, String password, String name,
                           String email, String phone, String userType, String sector) {
 

        try {
            List<User> users = loadUsers();

            // If this is the first user, allow creating an administrator
            boolean isFirstUser = users.isEmpty();

            // Only allow admin creation if it's the first user or if current user is admin
            if (userType.equalsIgnoreCase("administrator")) {
                if (!isFirstUser && !sessionState.isLoggedIn()) {
                    return false;
                }
                if (!isFirstUser && !sessionState.isAdministrator()) {
                    return false;
                }
            }


            // Check if username already exists
            if (users.stream().anyMatch(u -> u.getUsername().equals(username))) {
                return false;
            }

            String userId = "U" + UUID.randomUUID().toString().substring(0, 8);
            User newUser;

            switch (userType.toLowerCase()) {
                case "cashier" ->
                        newUser = new Cashier(userId, username, password, name,
                                email, phone, sector);
                case "manager" ->
                        newUser = new Manager(userId, username, password, name,
                                email, phone);
                case "administrator" ->
                        newUser = new Administrator(userId, username, password, name,
                                email, phone);
                default ->
                        throw new IllegalArgumentException("Invalid user type");
            }

            users.add(newUser);

            // Ensure the data directory exists
          Files.createDirectories(Paths.get(FileHandler.DATA_DIRECTORY));

            // Save the updated user list
            FileHandler.saveListToFile(users, USERS_FILE);
            return true;

        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Error saving user: " + e.getMessage());
            return false;
        }
    }

    public boolean updateUser(String userId, Map<String, String> updates) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        try {
            List<User> users = loadUsers();
            Optional<User> userOpt = users.stream()
                    .filter(u -> u.getId().equals(userId))
                    .findFirst();

            if (userOpt.isPresent()) {
                User user = userOpt.get();

                updates.forEach((key, value) -> {
                    switch (key) {
                        case "name" -> user.setName(value);
                        case "email" -> user.setEmail(value);
                        case "phone" -> user.setPhone(value);
                        case "password" -> user.setPassword(value);
                    }
                });

                FileHandler.saveListToFile(users, USERS_FILE);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteUser(String userId) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        try {
            List<User> users = loadUsers();
            boolean removed = users.removeIf(u -> u.getId().equals(userId));

            if (removed) {
                FileHandler.saveListToFile(users, USERS_FILE);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
    public List<User> getAllUsers() {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }

        return loadUsers();
    }

   /* public List<User> getAllUsers() {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }

        try {
            return loadUsers();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    */
    public boolean toggleUserActive(String userId) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        try {
            List<User> users = loadUsers();

            for (User user : users) {
                if (user.getId().equals(userId)) {
                    user.setActive(!user.isActive()); // toggle
                    FileHandler.saveListToFile(users, USERS_FILE);
                    return true;
                }
            }
            return false;

        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }


    public List<User> getUsersByType(String userType) {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }

        return loadUsers().stream()
                .filter(u -> u.getClass().getSimpleName()
                        .equalsIgnoreCase(userType))
                .toList();
    }

  /*  public List<User> getUsersByType(String userType) {
        if (!sessionState.isAdministrator()) {
            return new ArrayList<>();
        }

        try {
            return loadUsers().stream()
                    .filter(u -> u.getClass().getSimpleName()
                            .equalsIgnoreCase(userType))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
    */
    private List<User> loadUsers() {
        try {
            return FileHandler.readListFromFile(USERS_FILE);
        } catch (Exception e) {
            System.err.println("Users file missing or corrupted. Starting fresh.");
            return new ArrayList<>();
        }
    }


  /*  private List<User> loadUsers() throws IOException {
        try {
            return FileHandler.readListFromFile(USERS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }
    */

    public boolean resetPassword(User user) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        try {
            List<User> users = loadUsers();
            Optional<User> userOpt = users.stream()
                    .filter(u -> u.getId().equals(user.getId()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User foundUser = userOpt.get();
                foundUser.setPassword("password");
                FileHandler.saveListToFile(users, USERS_FILE);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean editUser(User user, String username, String name, String email, String phone, String role, String sector) {
        if (!sessionState.isAdministrator()) {
            return false;
        }

        try {
            List<User> users = loadUsers();
            Optional<User> userOpt = users.stream()
                    .filter(u -> u.getId().equals(user.getId()))
                    .findFirst();

            if (userOpt.isPresent()) {
                User foundUser = userOpt.get();
                foundUser.setUsername(username);
                foundUser.setName(name);
                foundUser.setEmail(email);
                foundUser.setPhone(phone);

                if (foundUser instanceof Cashier) {
                    ((Cashier) foundUser).setSector(sector);
                }

                //if (foundUser instanceof Manager) {
                    //((Manager) foundUser).setRole(role);
                //}

                FileHandler.saveListToFile(users, USERS_FILE);
                return true;
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }
}