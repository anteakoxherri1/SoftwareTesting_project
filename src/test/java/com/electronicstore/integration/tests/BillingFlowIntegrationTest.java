package com.electronicstore.integration.tests;

import com.electronicstore.controller.BillingController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.utils.SessionState;
import com.electronicstore.model.users.Cashier;

import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.*;

class BillingFlowIntegrationTest {

    private BillingController billingController;
    private SessionState sessionState;
    private Item item;

    @BeforeEach
    void setUp() {
        sessionState = SessionState.getInstance();
        sessionState.endSession(); // 

        // Fake Cashier login
        Cashier cashier = new Cashier(
                "U1",
                "cashier",
                "1234",
                "Test Cashier",
                "cashier@test.com",
                "123456",
                "Electronics"
        );
        sessionState.startSession(cashier);

        billingController = new BillingController();

        // Create test product
        Category category = new Category("C1", "Phones", 5, "Electronics");
        Supplier supplier = new Supplier("S1", "Samsung", "contact@samsung.com");

        item = new Item(
                "I1",
                "Galaxy S",
                category,
                supplier,
                java.time.LocalDate.now(),
                500,
                700,
                10 // initial stock
        );
    }

    @AfterEach
    void cleanUp() {
        sessionState.endSession();
    }

    @Test
    void addItemToBill_updatesBillStockAndTotal() {

        // WHEN – create bill
        Bill bill = assertDoesNotThrow(() ->
                billingController.createNewBill()
        );

        // WHEN – add item
        boolean added = billingController.addItemToBill(item, 2);
        assertTrue(added);

        // THEN – bill updated
        assertEquals(1, bill.getItems().size());

        // THEN – stock updated
        assertEquals(8, item.getStockQuantity());

        // THEN – total correct
        double expectedTotal = 2 * item.getSellingPrice();
        assertEquals(expectedTotal, bill.getTotalAmount());
    }

    @Test
    void addItemWithInsufficientStock_failsAndDoesNotUpdateBill() {

        billingController.createNewBill();

        boolean added = billingController.addItemToBill(item, 50); // more than stock

        assertFalse(added);
        assertEquals(10, item.getStockQuantity()); // unchanged
        assertEquals(0, billingController.getBillTotal());
    }
}
