package com.electronicstore.integration;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryFileOverwriteIntegrationTest {

    // ✅ TEST CASE 1
    @Test
    void saveItems_overwritesPreviousData() {
        
        String testFile = "overwrite_items.dat";

        Category category = new Category("C1", "Electronics", 5, "Tech");
        Supplier supplier = new Supplier("S1", "SupplierOne", "mail@test.com");

        Item firstItem = new Item(
                "I1", "Laptop", category, supplier,
                LocalDate.now(), 400, 700, 10
        );

        List<Item> firstList = new ArrayList<>();
        firstList.add(firstItem);

        FileHandler.saveListToFile(testFile, firstList);

        // WHEN – overwrite with new data
        Item secondItem = new Item(
                "I2", "Tablet", category, supplier,
                LocalDate.now(), 200, 350, 5
        );

        List<Item> secondList = new ArrayList<>();
        secondList.add(secondItem);

        FileHandler.saveListToFile(testFile, secondList);
        List<Item> loadedItems = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedItems, "List should not be null");
        assertEquals(1, loadedItems.size(), "File should contain only the latest data");
        assertEquals("Tablet", loadedItems.get(0).getName());
    }

    // ✅ TEST CASE 2
    @Test
    void saveItems_afterEmptyFile_storesDataCorrectly() {
        // GIVEN
        String testFile = "overwrite_from_empty.dat";

        List<Item> emptyList = new ArrayList<>();
        FileHandler.saveListToFile(testFile, emptyList);

        Category category = new Category("C2", "Accessories", 3, "Electronics");
        Supplier supplier = new Supplier("S2", "AccessorySupplier", "acc@test.com");

        Item item = new Item(
                "I3", "Mouse", category, supplier,
                LocalDate.now(), 10, 25, 50
        );

        List<Item> items = new ArrayList<>();
        items.add(item);

        // WHEN
        FileHandler.saveListToFile(testFile, items);
        List<Item> loadedItems = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedItems, "List should not be null");
        assertEquals(1, loadedItems.size());
        assertEquals("Mouse", loadedItems.get(0).getName());
    }
}
