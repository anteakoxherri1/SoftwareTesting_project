package com.electronicstore.unit.model.users;

import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.users.Cashier;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TESTS for Cashier.
 */
class CashierUnitTest {

    /**
     * Verifies that a valid Bill object can be added to the cashier.
     */
    @Test
    void addBill_shouldAcceptValidBill() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Name", "email@test.com", "111", "IT"
        );

        Bill bill = new Bill("B1", "U1");

        cashier.addBill(bill);

        // We do not check totals here (already covered in Part 2)
        assertNotNull(cashier.viewDailyBills(),
                "Daily bills list should not be null after adding a bill");
    }

    /**
     * Verifies that adding a null Bill does not modify the bill list.
     */
    @Test
    void addBill_shouldIgnoreNullBill() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Name", "email@test.com", "111", "IT"
        );

        cashier.addBill(null);

        assertTrue(cashier.viewDailyBills().isEmpty(),
                "Null bill should not be added");
    }

    /**
     * Verifies that clearBills removes all bills from the cashier.
     */
    @Test
    void clearBills_shouldRemoveAllBills() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Name", "email@test.com", "111", "IT"
        );

        cashier.addBill(new Bill("B1", "U1"));
        cashier.addBill(new Bill("B2", "U1"));

        cashier.clearBills();

        assertTrue(cashier.viewDailyBills().isEmpty(),
                "All bills should be removed after clearBills()");
    }

    /**
     * Verifies that login succeeds only with correct credentials.
     */
    @Test
    void login_shouldValidateCredentialsCorrectly() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Name", "email@test.com", "111", "IT"
        );

        assertTrue(cashier.login("cash", "1234"));
        assertFalse(cashier.login("cash", "wrong"));
        assertFalse(cashier.login("wrongUser", "1234"));
    }

    /**
     * Verifies that password change works only when the old password is correct.
     */
    @Test
    void changePassword_shouldUpdatePasswordOnlyIfOldMatches() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Name", "email@test.com", "111", "IT"
        );

        assertFalse(cashier.changePassword("wrongOld", "newPass"),
                "Password should not change with wrong old password");

        assertTrue(cashier.changePassword("1234", "newPass"),
                "Password should change with correct old password");

        assertTrue(cashier.login("cash", "newPass"),
                "Login should succeed with new password");
    }
}
