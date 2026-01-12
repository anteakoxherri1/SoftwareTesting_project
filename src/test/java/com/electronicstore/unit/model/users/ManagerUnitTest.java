package com.electronicstore.unit.model.users;

import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.users.Manager;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ManagerUnitTest {

    /**
     * Verifies that a manager can add and remove suppliers correctly.
     */
    @Test
    void manageSuppliers_addAndRemove_shouldWork() {
        Manager manager = new Manager("U2", "mgr", "1111",
                "Manager", "m@test.com", "222");
        Supplier supplier = new Supplier("S1", "Dell", "contact");

        manager.manageSuppliers(supplier, "add");
        assertEquals(1, manager.getSuppliers().size());

        manager.manageSuppliers(supplier, "remove");
        assertEquals(0, manager.getSuppliers().size());
    }

    /**
     * Verifies that an invalid supplier action
     * results in an IllegalArgumentException.
     */
    @Test
    void manageSuppliers_invalidAction_shouldThrowException() {
        Manager manager = new Manager("U2", "mgr", "1111",
                "Manager", "m@test.com", "222");
        Supplier supplier = new Supplier("S1", "Dell", "contact");

        assertThrows(IllegalArgumentException.class,
                () -> manager.manageSuppliers(supplier, "invalid"));
    }

    /**
     * Verifies that a manager receives a "Not authorized" message  when accessing statistics for an unmanaged sector.
     */
    @Test
    void viewSectorStats_whenNotAuthorized_shouldReturnMessage() {
        Manager manager = new Manager("U2", "mgr", "1111",
                "Manager", "m@test.com", "222");

        String result = manager.viewSectorStats("IT");

        assertTrue(result.toLowerCase().contains("not authorized"));
    }

    /**
     * Verifies that a manager can change their password only when the old password is correct.
     */
    @Test
    void changePassword_shouldWorkOnlyWithCorrectOldPassword() {
        Manager manager = new Manager("U2", "mgr", "1111",
                "Manager", "m@test.com", "222");

        assertFalse(manager.changePassword("wrong", "newpass"));
        assertTrue(manager.changePassword("1111", "newpass"));
        assertTrue(manager.login("mgr", "newpass"));
    }
}
