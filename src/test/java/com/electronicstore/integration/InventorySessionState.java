package com.electronicstore.integration;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.users.Manager;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.Administrator;
import com.electronicstore.model.utils.SessionState;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class InventorySessionState {

    private InventoryController inventoryController;
    private SessionState sessionState;

    @BeforeEach
    void setUp() {
        sessionState = SessionState.getInstance();
        inventoryController = new InventoryController();
    }

    @AfterEach
    void resetSessionState() {
        sessionState.endSession();
    }

    //  POSITIVE CASE – Manager
    @Test
    void inventoryAction_asManager_isAllowed() {
        Manager manager = new Manager(
                "U1100",
                "managerRoleInv",
                "1234",
                "Manager User",
                "manager@test.com",
                "111000"
        );
        sessionState.startSession(manager);

        Category category = new Category(
                "C1100",
                "Networking",
                2,
                "Electronics"
        );

        Supplier supplier = new Supplier(
                "S1100",
                "TP-Link",
                "tplink@test.com"
        );

        boolean result = inventoryController.addItem(
                "Router",
                category,
                supplier,
                40,
                70,
                15
        );

        assertTrue(result);
    }

    // NEGATIVE CASE – Cashier
    @Test
    void inventoryAction_asCashier_isRejected() {
        Cashier cashier = new Cashier(
                "U1200",
                "cashierRoleInv",
                "1234",
                "Cashier User",
                "cashier@test.com",
                "122000",
                "Electronics"
        );
        sessionState.startSession(cashier);

        Category category = new Category(
                "C1200",
                "Cables",
                3,
                "Electronics"
        );

        Supplier supplier = new Supplier(
                "S1200",
                "Ugreen",
                "ugreen@test.com"
        );

        boolean result = inventoryController.addItem(
                "HDMI Cable",
                category,
                supplier,
                5,
                10,
                50
        );

        assertFalse(result);
    }

    //  NEGATIVE CASE – Administrator
    @Test
    void inventoryAction_asAdministrator_isRejected() {
        Administrator admin = new Administrator(
                "U1300",
                "adminRoleInv",
                "1234",
                "Admin User",
                "admin@test.com",
                "133000"
        );
        sessionState.startSession(admin);

        Category category = new Category(
                "C1300",
                "Power",
                1,
                "Electronics"
        );

        Supplier supplier = new Supplier(
                "S1300",
                "Anker",
                "anker@test.com"
        );

        boolean result = inventoryController.addItem(
                "Power Bank",
                category,
                supplier,
                25,
                45,
                20
        );

        assertFalse(result);
    }
}
