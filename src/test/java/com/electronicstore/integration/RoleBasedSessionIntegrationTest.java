package com.electronicstore.integration;

import com.electronicstore.controller.BillingController;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.utils.SessionState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class RoleBasedSessionIntegrationTest {

    private SessionState sessionState;
    private BillingController billingController;

    @BeforeEach
    void setup() {
        sessionState = SessionState.getInstance();
        sessionState.endSession();
        billingController = new BillingController();
    }

    /**
     * TEST CASE 1
     * System Behaviour without login
     */
    @Test
    void whenNoUserLoggedIn_actionsAreBlocked() {

        
        assertFalse(sessionState.isLoggedIn());

        
        assertThrows(IllegalStateException.class,
                () -> billingController.createNewBill(),
                "Billing should not be allowed without login");
    }

    /**
     * TEST CASE 2
     * 
     */
    @Test
    void systemBehaviorChangesAfterLoginAndLogout() {

        
        Cashier cashier = new Cashier(
                "U1",
                "cashier",
                "password",
                "Test Cashier",
                "cashier@test.com",
                "123456",
                "electronics"
        );

        // ===== LOGIN =====
        sessionState.startSession(cashier);

        assertTrue(sessionState.isLoggedIn());
        assertTrue(sessionState.isCashier());

        
        assertNotNull(
                billingController.createNewBill(),
                "Cashier should be able to create bill"
        );

        
        sessionState.endSession();
        assertFalse(sessionState.isLoggedIn());

        
        assertThrows(IllegalStateException.class,
                () -> billingController.createNewBill(),
                "Action should be blocked after logout");
    }
}

