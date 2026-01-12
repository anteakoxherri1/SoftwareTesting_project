package com.electronicstore.integration;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReadNonExistingFileIntegrationTest {

    @Test
    void readFromNonExistingFile_returnsEmptyList() {
        // GIVEN
        String nonExistingFile = "this_file_does_not_exist.dat";

        // WHEN
        List<Item> loadedItems = FileHandler.readListFromFile(nonExistingFile);

        // THEN
        assertNotNull(loadedItems, "List should not be null");
        assertTrue(loadedItems.isEmpty(), "List should be empty when file does not exist");
    }
}
