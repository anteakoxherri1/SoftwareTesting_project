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

class InventoryFileOverwriteIntegrationTest {

    // ✅ TEST CASE 1
    @Test
    void saveItems_overwritesPreviousData() {

        // GIVEN
        String testFile = "overwrite_items.dat";

        Category category = new Category("C1", "Electronics", 5, "Tech");
        Supplier supplier = new Supplier("S1", "SupplierOne", "mail@test.com");

        Item firstItem = new Item(
                "I1", "Laptop", category, supplier,
                LocalDate.now(), 400, 700, 10
        );

        List<Item> firstList = new ArrayList<>();
        firstList.add(firstItem);

        assertDoesNotThrow(() ->
                FileHandler.saveListToFile(firstList, testFile)
        );

        // WHEN – overwrite with new data
        Item secondItem = new Item(
                "I2", "Tablet", category, supplier,
                LocalDate.now(), 200, 350, 5
        );

        List<Item> secondList = new ArrayList<>();
        secondList.add(secondItem);

        assertDoesNotThrow(() ->
                FileHandler.saveListToFile(secondList, testFile)
        );

        List<Item> loadedItems = assertDoesNotThrow(() ->
                FileHandler.readListFromFile(testFile)
        );

        // THEN
        assertNotNull(loadedItems);
        assertEquals(1, loadedItems.size(),
                "File should contain only the latest data");
        assertEquals("Tablet", loadedItems.get(0).getName());
    }

    // ✅ TEST CASE 2
    @Test
    void saveItems_afterEmptyFile_storesDataCorrectly() {

        // GIVEN
        String testFile = "overwrite_from_empty.dat";

        List<Item> emptyList = new ArrayList<>();

        assertDoesNotThrow(() ->
                FileHandler.saveListToFile(emptyList, testFile)
        );

        Category category = new Category("C2", "Accessories", 3, "Electronics");
        Supplier supplier = new Supplier("S2", "AccessorySupplier", "acc@test.com");

        Item item = new Item(
                "I3", "Mouse", category, supplier,
                LocalDate.now(), 10, 25, 50
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
        assertEquals("Mouse", loadedItems.get(0).getName());
    }
}
