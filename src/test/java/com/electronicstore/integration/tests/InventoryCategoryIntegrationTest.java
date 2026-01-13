package com.electronicstore.integration.tests;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.Manager;
import com.electronicstore.model.users.Administrator;
import com.electronicstore.model.utils.SessionState;

import org.junit.jupiter.api.*;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryCategoryIntegrationTest {

    private InventoryController inventoryController;
    private SessionState sessionState;

    @BeforeEach
    void setUp() {
        sessionState = SessionState.getInstance();
        sessionState.endSession(); // 
        inventoryController = new InventoryController();
    }

    @AfterEach
    void resetSessionState() {
        sessionState.endSession();
    }

    // ✅ POSITIVE CASE – Manager
    @Test
    void addItemWithCategory_asManager_isSuccessful() {

        // Login as Manager
        Manager manager = new Manager(
                "U400",
                "managerInventory",
                "1234",
                "Inventory Manager",
                "manager@test.com",
                "444444"
        );
        sessionState.startSession(manager);

        // Add Category
        boolean categoryAdded = inventoryController.addCategory(
                "Smartphones",
                5,
                "Electronics"
        );
        assertTrue(categoryAdded);

        // Retrieve category created by the system
        List<Category> categories = inventoryController.getAllCategories();
        assertFalse(categories.isEmpty());
        Category category = categories.get(0);

        // Create Supplier
        Supplier supplier = new Supplier(
                "S400",
                "Apple",
                "apple@test.com"
        );

        // Add Item
        boolean itemAdded = inventoryController.addItem(
                "iPhone 15",
                category,
                supplier,
                800,
                1200,
                10
        );
        assertTrue(itemAdded);

        // Verify item exists in inventory
        List<Item> items = inventoryController.getAllItems();
        assertTrue(
                items.stream().anyMatch(i -> i.getName().equals("iPhone 15")),
                "Item should exist in inventory"
        );
    }

    // ❌ NEGATIVE CASE – Cashier
    @Test
    void addItem_asCashier_isRejected() {

        Cashier cashier = new Cashier(
                "U500",
                "cashierInventory",
                "1234",
                "Cashier User",
                "cashier@test.com",
                "555555",
                "Electronics"
        );
        sessionState.startSession(cashier);

        Category category = new Category(
                "C500",
                "Accessories",
                3,
                "Electronics"
        );

        Supplier supplier = new Supplier(
                "S500",
                "Baseus",
                "baseus@test.com"
        );

        boolean itemAdded = inventoryController.addItem(
                "USB Cable",
                category,
                supplier,
                5,
                10,
                50
        );

        assertFalse(itemAdded);
    }

    // ❌ NEGATIVE CASE – Administrator
    @Test
    void addItem_asAdministrator_isRejected() {

        Administrator admin = new Administrator(
                "U600",
                "adminInventory",
                "1234",
                "Admin User",
                "admin@test.com",
                "666666"
        );
        sessionState.startSession(admin);

        Category category = new Category(
                "C600",
                "Tablets",
                4,
                "Electronics"
        );

        Supplier supplier = new Supplier(
                "S600",
                "Samsung",
                "samsung@test.com"
        );

        boolean itemAdded = inventoryController.addItem(
                "Galaxy Tab",
                category,
                supplier,
                300,
                500,
                15
        );

        assertFalse(itemAdded);
    }
}
