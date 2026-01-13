package com.electronicstore.integration.tests;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class InventoryItemFileIntegrationTest {

    // ✅ TEST CASE 1
    @Test
    void saveAndReadItems_successfully() {

        // GIVEN
        String testFile = "test_items.dat";

        Category category = new Category("C1", "Laptops", 5, "Electronics");
        Supplier supplier = new Supplier("S1", "TechSupplier", "contact@test.com");

        Item item = new Item(
                "I1",
                "Laptop Dell",
                category,
                supplier,
                LocalDate.now(),
                500.0,
                750.0,
                10
        );

        List<Item> items = new ArrayList<>();
        items.add(item);

        // WHEN
        assertDoesNotThrow(() ->
                FileHandler.saveListToFile(items, testFile)
        );

        List<Item> loadedItems = assertDoesNotThrow(() ->
                FileHandler.readListFromFile(testFile)
        );

        // THEN
        assertNotNull(loadedItems);
        assertEquals(1, loadedItems.size());

        Item loadedItem = loadedItems.get(0);
        assertEquals("Laptop Dell", loadedItem.getName());
        assertEquals(10, loadedItem.getStockQuantity());
        assertEquals("Laptops", loadedItem.getCategory().getName());
    }

    // ✅ TEST CASE 2 – Multiple Items
    @Test
    void saveAndReadMultipleItems_successfully() {

        // GIVEN
        String testFile = "test_items_multiple.dat";

        Category category = new Category("C2", "Accessories", 3, "Electronics");
        Supplier supplier = new Supplier("S2", "AccessorySupplier", "acc@test.com");

        Item item1 = new Item(
                "I2",
                "Mouse",
                category,
                supplier,
                LocalDate.now(),
                10.0,
                20.0,
                50
        );

        Item item2 = new Item(
                "I3",
                "Keyboard",
                category,
                supplier,
                LocalDate.now(),
                20.0,
                35.0,
                30
        );

        List<Item> items = new ArrayList<>();
        items.add(item1);
        items.add(item2);

        // WHEN
        assertDoesNotThrow(() ->
                FileHandler.saveListToFile(items, testFile)
        );

        List<Item> loadedItems = assertDoesNotThrow(() ->
                FileHandler.readListFromFile(testFile)
        );

        // THEN
        assertNotNull(loadedItems);
        assertEquals(2, loadedItems.size());

        assertEquals("Mouse", loadedItems.get(0).getName());
        assertEquals("Keyboard", loadedItems.get(1).getName());
    }
}
