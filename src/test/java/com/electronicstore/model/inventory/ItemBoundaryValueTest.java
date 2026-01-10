package com.electronicstore.model.inventory;

import static org.junit.jupiter.api.Assertions.*;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class ItemBoundaryValueTest {

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
                10  
        );
    }
    
//-------- Boundary Value Testing: checkAvailability --------
     @Test
void checkAvailability_requestedMoreThanStock_shouldFail() {
 boolean result = item.checkAvailability(11);
 assertFalse(result);
}
    @Test
void checkAvailability_requestedEqualToStock_shouldPass() {
 boolean result = item.checkAvailability(10);
 assertTrue(result);
}

@Test
void checkAvailability_requestedLessThanStock_shouldPass() {
 boolean result = item.checkAvailability(5);
 assertTrue(result);
 
}
}
