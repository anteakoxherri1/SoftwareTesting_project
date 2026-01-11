package com.electronicstore.model.users;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class AdministratorManageUsersCoverageTest {

    private Administrator admin;
    private User cashier;

    @BeforeEach
    void setUp() {
        admin = new Administrator("A1", "admin", "pass", "Admin", "a@mail.com", "000");
        cashier = new Cashier("U1", "cash1", "p", "Cashier", "c@mail.com", "111", "Phones");
    }

    @Test
    void addNewUser_success() {
        assertTrue(admin.manageUsers(cashier, "add"));
    }

    @Test
    void addExistingUser_fails() {
        admin.manageUsers(cashier, "add");
        assertFalse(admin.manageUsers(cashier, "add"));
    }

    @Test
    void removeExistingUser_success() {
        admin.manageUsers(cashier, "add");
        assertTrue(admin.manageUsers(cashier, "remove"));
    }

    @Test
    void removeMissingUser_fails() {
        assertFalse(admin.manageUsers(cashier, "remove"));
    }

    @Test
    void updateExistingUser_success() {
        admin.manageUsers(cashier, "add");
        assertTrue(admin.manageUsers(cashier, "update"));
    }

    @Test
    void updateMissingUser_fails() {
        assertFalse(admin.manageUsers(cashier, "update"));
    }

    @Test
    void wrongAction_throwsError() {
        assertThrows(IllegalArgumentException.class,
                () -> admin.manageUsers(cashier, "wrong"));
    }
}
