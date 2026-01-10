package com.electronicstore.model.users;
import java.util.logging.Logger;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Administrator extends User implements Serializable {
	private static final Logger LOGGER = Logger.getLogger(Administrator.class.getName());

    private static final long serialVersionUID = 1L;
    private List<User> allUsers;

    public Administrator(String id, String username, String password, String name,
                         String email, String phone) {
        super(id, username, password, name, email, phone);
        this.allUsers = new ArrayList<>();
    }

    @Override
    public boolean login(String username, String password) {
        return this.getUsername().equals(username) && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        LOGGER.info("Administrator logged out: " + this.getUsername());
    }


    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.getPassword().equals(oldPassword)) {
            this.setPassword(newPassword);
            return true;
        }
        return false;
    }

    // Administrator specific methods
    public boolean manageUsers(User user, String action) {
        switch (action.toLowerCase()) {
            case "add" -> {
                if (!allUsers.contains(user)) {
                    allUsers.add(user);
                    return true;
                }
            }
            case "remove" -> {
                return allUsers.remove(user);
            }
            case "update" -> {
                if (allUsers.contains(user)) {
                    int index = allUsers.indexOf(user);
                    allUsers.set(index, user);
                    return true;
                }
            }
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
        return false;
    }

    public List<User> getAllUsers() {
        return new ArrayList<>(allUsers);
    }

    public String viewSystemStats() {
        int totalUsers = allUsers.size();
        int cashiers = (int) allUsers.stream().filter(Cashier.class::isInstance).count();
        int managers = (int) allUsers.stream().filter(Manager.class::isInstance).count();


        return String.format("""
            System Statistics:
            Total Users: %d
            Cashiers: %d
            Managers: %d
            Administrators: %d
            """,
                totalUsers, cashiers, managers,
                totalUsers - cashiers - managers);
    }
}