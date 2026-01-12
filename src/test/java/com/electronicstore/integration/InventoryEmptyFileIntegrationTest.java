package com.electronicstore.integration;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class InventoryEmptyFileIntegrationTest {

    @Test
    void readItems_fromEmptyFile_returnsEmptyList() {
        
        String testFile = "empty_items.dat";
        List<Item> emptyList = new ArrayList<>();

        
        FileHandler.saveListToFile(testFile, emptyList);

        
        List<Item> loadedItems = FileHandler.readListFromFile(testFile);

        
        assertNotNull(loadedItems, "List should not be Null");
        assertTrue(loadedItems.isEmpty(), "List should be empty");
    }
}
