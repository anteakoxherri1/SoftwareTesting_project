package com.electronicstore.integration;

import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class FileErrorHandlingIntegrationTest {

    @Test
    void readingMissingFileDoesNotCrashSystem() {

        
        List<?> data = FileHandler.readListFromFile("non_existing_file.dat");

        
        assertNotNull(data);
        assertTrue(data.isEmpty());
    }
}
