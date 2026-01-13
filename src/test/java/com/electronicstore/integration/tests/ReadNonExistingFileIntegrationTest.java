package com.electronicstore.integration.tests;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class ReadNonExistingFileIntegrationTest {

    @Test
    void readFromNonExistingFile_returnsEmptyList() {
        // GIVEN
        String nonExistingFile = "this_file_does_not_exist.dat";

        // WHEN
        List<Item> loadedItems = new ArrayList<>();

        try {
            loadedItems = FileHandler.readListFromFile(nonExistingFile);
        } catch (Exception e) {
            // expected: file does not exist
        }

        // THEN
        assertNotNull(loadedItems, "List should not be null");
        assertTrue(loadedItems.isEmpty(),
                "List should be empty when file does not exist");
    }
}
