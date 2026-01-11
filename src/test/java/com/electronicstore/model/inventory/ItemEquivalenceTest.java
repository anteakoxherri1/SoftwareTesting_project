package com.electronicstore.model.inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class ItemEquivalenceTest {

    private Item item;

    @BeforeEach
    void setUp() {
        Category category = new Category("C1", "Electronics", 5, "Tech");
        Supplier supplier = new Supplier("S1", "Samsung", "contact@samsung.com");

        item = new Item(
                "I1",
                "Laptop",
                category,
                supplier,
                LocalDate.now(),
                500.0,
                800.0,
                10   // stockQuantity
        );
    }

    // -------- Equivalence Class Testing: checkAvailability --------

    @Test
    void checkAvailability_validEquivalenceClass_shouldReturnTrue() {
        boolean result = item.checkAvailability(5); // representative value ≤ stock
        assertTrue(result);
    }

    @Test
    void checkAvailability_equalToStock_shouldReturnTrue() {
        boolean result = item.checkAvailability(10); // representative value = stock
        assertTrue(result);
    }

    @Test
    void checkAvailability_invalidEquivalenceClass_shouldReturnFalse() {
        boolean result = item.checkAvailability(11); // representative value > stock
        assertFalse(result);
    }
}
