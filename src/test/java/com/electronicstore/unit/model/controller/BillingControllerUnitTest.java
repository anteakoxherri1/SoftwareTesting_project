package com.electronicstore.unit.model.controller;

import com.electronicstore.controller.BillingController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.utils.SessionState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class BillingControllerUnitTest {

    private final SessionState session = SessionState.getInstance();

    @AfterEach
    void cleanupSession() {
        // SessionState is a Singleton, so always reset it after each test
        session.endSession();
    }

    // Helper to create a valid cashier session
    private void loginAsCashier() {
        Cashier cashier = new Cashier(
                "U1", "cash", "1234",
                "Cashier Name", "cash@test.com", "111", "IT"
        );
        session.startSession(cashier);
        assertTrue(session.isCashier(), "Precondition failed: should be logged in as Cashier");
    }

    // Helper to create a real Item with stock
    private Item createItemWithStock(int stockQty) {
        Category category = new Category("C1", "Laptops", 5, "IT");
        Supplier supplier = new Supplier("S1", "Dell", "contact");

        return new Item(
                "I1", "Laptop A",
                category,
                supplier,
                LocalDate.now(),
                500.0,   // purchasePrice
                700.0,   // sellingPrice
                stockQty // stockQuantity
        );
    }

    @Test
    void createNewBill_shouldThrowIfNotCashier() {
        // Not logged in as cashier (no session)
        BillingController controller = new BillingController();

        // createNewBill() should throw because only cashiers can create bills
        assertThrows(IllegalStateException.class, controller::createNewBill);
    }

    @Test
    void createNewBill_shouldCreateBillWhenCashierLoggedIn() {
        loginAsCashier();

        BillingController controller = new BillingController();

        assertNotNull(controller.createNewBill(), "Bill should be created");
        assertNotNull(controller.getCurrentBill(), "Current bill should not be null after creation");
        assertTrue(controller.getCurrentBill().getBillNumber().startsWith("B"),
                "Bill number should start with 'B'");
    }

    @Test
    void addItemToBill_shouldReturnFalseWhenNoCurrentBill() {
        // No bill created yet
        BillingController controller = new BillingController();

        Item item = createItemWithStock(10);

        boolean result = controller.addItemToBill(item, 1);

        assertFalse(result, "Should return false when there is no active bill");
    }

    @Test
    void addItemToBill_shouldReturnFalseWhenInsufficientStock() {
        loginAsCashier();
        BillingController controller = new BillingController();
        controller.createNewBill();

        Item item = createItemWithStock(2);

        // Request more than available stock
        boolean result = controller.addItemToBill(item, 5);

        assertFalse(result, "Should return false if item stock is insufficient");
        assertEquals(2, item.getStockQuantity(), "Stock should not change when addItemToBill fails");
    }

    @Test
    void addItemToBill_shouldAddItemAndDecreaseStockWhenAvailable() {
        loginAsCashier();
        BillingController controller = new BillingController();
        controller.createNewBill();

        Item item = createItemWithStock(10);

        boolean result = controller.addItemToBill(item, 3);

        assertTrue(result, "Should return true when item is successfully added");
        assertEquals(7, item.getStockQuantity(), "Stock should decrease by the sold quantity");
        assertEquals(1, controller.getCurrentBill().getItems().size(), "Bill should contain one sale item");
    }

    @Test
    void removeItemFromBill_shouldReturnFalseWhenNoCurrentBill() {
        BillingController controller = new BillingController();
        Item item = createItemWithStock(10);
        SaleItem saleItem = new SaleItem(item, 2);

        assertFalse(controller.removeItemFromBill(saleItem),
                "Should return false when there is no active bill");
    }

    @Test
    void removeItemFromBill_shouldRemoveAndRestoreStock() {
        loginAsCashier();
        BillingController controller = new BillingController();
        controller.createNewBill();

        Item item = createItemWithStock(10);

        // Add item to bill (stock decreases)
        assertTrue(controller.addItemToBill(item, 4));
        assertEquals(6, item.getStockQuantity());

        // Grab the SaleItem that was added to the bill
        SaleItem saleItem = controller.getCurrentBill().getItems().get(0);

        // Remove it (stock should be restored)
        assertTrue(controller.removeItemFromBill(saleItem));
        assertEquals(10, item.getStockQuantity(), "Stock should be restored after removal");
        assertTrue(controller.getCurrentBill().getItems().isEmpty(), "Bill should be empty after removal");
    }

    @Test
    void getBillTotal_shouldReturnZeroWhenNoBill() {
        BillingController controller = new BillingController();
        assertEquals(0.0, controller.getBillTotal(), 0.0001,
                "Total should be 0.0 when there is no current bill");
    }

    @Test
    void getBillTotal_shouldReturnPositiveWhenBillHasItems() {
        loginAsCashier();
        BillingController controller = new BillingController();
        controller.createNewBill();

        Item item = createItemWithStock(10);
        // sellingPrice = 700.0, quantity = 2 -> expected total depends on Bill implementation,
        // but it should definitely be > 0 if total uses selling price.
        assertTrue(controller.addItemToBill(item, 2));

        assertTrue(controller.getBillTotal() > 0.0,
                "Total should be > 0 when bill contains items");
    }
}
