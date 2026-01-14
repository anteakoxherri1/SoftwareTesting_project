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

public class DifferentFileNameIntegrationTest {

    @Test
    void savingToDifferentFiles_keepsDataSeparated() {
        // GIVEN
        String fileOne = "items_file_one.dat";
        String fileTwo = "items_file_two.dat";

        Category category = new Category("C1", "Electronics", 5, "Tech");
        Supplier supplier = new Supplier("S1", "SupplierOne", "one@test.com");

        Item itemOne = new Item(
                "I1", "Laptop",
                category, supplier,
                LocalDate.now(),
                400.0, 700.0, 10
        );

        Item itemTwo = new Item(
                "I2", "Tablet",
                category, supplier,
                LocalDate.now(),
                200.0, 350.0, 5
        );

        List<Item> listOne = new ArrayList<>();
        listOne.add(itemOne);

        List<Item> listTwo = new ArrayList<>();
        listTwo.add(itemTwo);

        // WHEN
        FileHandler.saveListToFile(fileOne, listOne);
        FileHandler.saveListToFile(fileTwo, listTwo);

        List<Item> loadedFromFileOne = FileHandler.readListFromFile(fileOne);
        List<Item> loadedFromFileTwo = FileHandler.readListFromFile(fileTwo);

        // THEN
        assertNotNull(loadedFromFileOne, "First file list should not be null");
        assertNotNull(loadedFromFileTwo, "Second file list should not be null");

        assertEquals(1, loadedFromFileOne.size());
        assertEquals(1, loadedFromFileTwo.size());

        assertEquals("Laptop", loadedFromFileOne.get(0).getName());
        assertEquals("Tablet", loadedFromFileTwo.get(0).getName());
    }
}
