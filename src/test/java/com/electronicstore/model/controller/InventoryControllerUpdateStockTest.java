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

public class InventoryControllerUpdateStockTest {

    private InventoryController controller;
    private static final Path ITEMS =
            Path.of(FileHandler.DATA_DIRECTORY).resolve("items.dat");

    @BeforeEach
    void setUp() throws Exception {
        controller = new InventoryController();
        Files.createDirectories(Path.of(FileHandler.DATA_DIRECTORY));
        Files.deleteIfExists(ITEMS);
    }

    @AfterEach
    void cleanUp() throws Exception {
        Files.deleteIfExists(ITEMS);
    }

    private void saveItem() throws Exception {
        Category cat = new Category("C1", "Laptops", 5, "Electronics");
        Supplier sup = new Supplier("S1", "HP", "hp@mail.com");

        Item item = new Item("I1", "Laptop", cat, sup, LocalDate.now(), 500, 800, 10);
        FileHandler.saveListToFile(new ArrayList<>(List.of(item)), "items.dat");
    }

    // Boundary: stock becomes exactly 0
    @Test
    void stockAtZero_isValid() throws Exception {
        saveItem();
        assertTrue(controller.updateItemStock("I1", -10));
    }

    // Just below boundary: stock becomes -1 
    @Test
    void stockBelowZero_isInvalid() throws Exception {
        saveItem();
        assertFalse(controller.updateItemStock("I1", -11));
    }

    // Just above boundary: stock becomes 1 
    @Test
    void stockAboveZero_isValid() throws Exception {
        saveItem();
        assertTrue(controller.updateItemStock("I1", -9));
    }
}
