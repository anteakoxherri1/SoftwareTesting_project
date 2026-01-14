package com.electronicstore.integration;

import com.electronicstore.controller.BillingController;
import com.electronicstore.model.utils.SessionState;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.Manager;
import com.electronicstore.model.users.Administrator;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BillingRoleIntegrationTest {

    private SessionState sessionState;
    private BillingController billingController;

    @BeforeEach
    void setUp() {
        sessionState = SessionState.getInstance();
        billingController = new BillingController();
    }

    @AfterEach
    void resetSessionState() {
        sessionState.endSession();
    }

    // CASHIER
    @Test
    void createBill_asCashier_isAllowed() {
        Cashier cashier = new Cashier(
                "U100",
                "cashierUser",
                "1234",
                "Cashier Test",
                "cashier@test.com",
                "111111",
                "Electronics"
        );

        sessionState.startSession(cashier);

        assertDoesNotThrow(() -> billingController.createNewBill());
        assertNotNull(billingController.getCurrentBill());
    }

    // Manager
    @Test
    void createBill_asManager_isRejected() {
        Manager manager = new Manager(
                "U200",
                "managerUser",
                "1234",
                "Manager Test",
                "manager@test.com",
                "222222"
        );

        sessionState.startSession(manager);

        assertThrows(IllegalStateException.class,
                () -> billingController.createNewBill());

        assertNull(billingController.getCurrentBill());
    }

    // ADMINISTRATOR
    @Test
    void createBill_asAdministrator_isRejected() {
        Administrator admin = new Administrator(
                "U300",
                "adminUser",
                "1234",
                "Admin Test",
                "admin@test.com",
                "333333"
        );

        sessionState.startSession(admin);

        assertThrows(IllegalStateException.class,
                () -> billingController.createNewBill());

        assertNull(billingController.getCurrentBill());
    }
}
