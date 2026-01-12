package com.electronicstore.unit.model.users;

import com.electronicstore.model.users.Administrator;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.Manager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTS for Administrator.
 */
class AdministratorUnitTest {

    @Test
    void login_shouldReturnTrueOnlyForCorrectCredentials() {
        Administrator a = new Administrator("A1", "admin", "pass", "Admin", "a@test.com", "000");

        assertTrue(a.login("admin", "pass"));
        assertFalse(a.login("admin", "wrong"));
        assertFalse(a.login("wrongUser", "pass"));
    }
    /**
     * Verifies that password changes are allowed only when the old password is correct.
     */
    @Test
    void changePassword_shouldUpdatePasswordOnlyIfOldMatches() {
        Administrator a = new Administrator("A1", "admin", "pass", "Admin", "a@test.com", "000");

        assertFalse(a.changePassword("badOld", "new"));
        assertTrue(a.changePassword("pass", "new"));

        // Verify new password works
        assertTrue(a.login("admin", "new"));
    }

    @Test
    void viewSystemStats_shouldContainRoleCounts() {
        Administrator admin = new Administrator("A1", "admin", "pass", "Admin", "a@test.com", "000");

        // Setup: use manageUsers to populate internal allUsers list
        admin.manageUsers(new Cashier("U1", "c1", "p", "Cashier", "c@test.com", "1", "IT"), "add");
        admin.manageUsers(new Manager("U2", "m1", "p", "Manager", "m@test.com", "2"), "add");
        admin.manageUsers(new Administrator("U3", "a2", "p", "Admin2", "a2@test.com", "3"), "add");

        String stats = admin.viewSystemStats().toLowerCase();

        assertTrue(stats.contains("total users"));
        assertTrue(stats.contains("cashiers"));
        assertTrue(stats.contains("managers"));
        assertTrue(stats.contains("administrators"));
    }
}
