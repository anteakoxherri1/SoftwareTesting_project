package com.electronicstore.model.controller;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.*;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.*;
import com.electronicstore.model.utils.FileHandler;

public class InventoryControllerCheckLowStockTest {

    private InventoryController controller;

    private static final Path ITEMS =
            Path.of(FileHandler.DATA_DIRECTORY).resolve("items.dat");
    private static final Path CATEGORIES =
            Path.of(FileHandler.DATA_DIRECTORY).resolve("categories.dat");

    @BeforeEach
    void setUp() throws Exception {
        controller = new InventoryController();
        Files.deleteIfExists(ITEMS);
        Files.deleteIfExists(CATEGORIES);
    }

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(ITEMS);
        Files.deleteIfExists(CATEGORIES);
    }

    private Item createItem(String id, int stock, int min) {
        Category cat = new Category("C1", "Accessories", min, "Electronics");
        Supplier sup = new Supplier("S1", "Logitech", "mail@test.com");

        return new Item(id, "Mouse", cat, sup, LocalDate.now(), 10, 15, stock);
    }

    @Test
    void returnsOnlyLowStockItems() throws Exception {
        Item low = createItem("I1", 3, 5);
        Item ok  = createItem("I2", 8, 5);

        FileHandler.saveListToFile(new ArrayList<>(List.of(low, ok)), "items.dat");
        FileHandler.saveListToFile(new ArrayList<>(), "categories.dat");

        List<Item> result = controller.checkLowStock();

        assertEquals(1, result.size());
        assertEquals("I1", result.get(0).getId());
    }

    @Test
    void returnsEmptyWhenNoLowStock() throws Exception {
        Item a = createItem("I1", 10, 5);
        Item b = createItem("I2", 20, 5);

        FileHandler.saveListToFile(new ArrayList<>(List.of(a, b)), "items.dat");
        FileHandler.saveListToFile(new ArrayList<>(), "categories.dat");

        assertTrue(controller.checkLowStock().isEmpty());
    }

    @Test
    void returnsEmptyWhenNoItems() throws Exception {
        FileHandler.saveListToFile(new ArrayList<>(), "items.dat");
        FileHandler.saveListToFile(new ArrayList<>(), "categories.dat");

        assertTrue(controller.checkLowStock().isEmpty());
    }
}
