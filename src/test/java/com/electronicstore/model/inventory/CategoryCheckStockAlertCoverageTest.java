package com.electronicstore.model.inventory;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CategoryCheckStockAlertCoverageTest {

    @Test
    public void coversTrueBranch_stockLessOrEqualMin_includedInAlert() {
        Category category = new Category("C1", "Laptops", 10, "IT");

        Item low = new Item("I1", "Low Stock Item", category, null,
                LocalDate.now(), 100, 150, 5);   // stock < min  => true

        Item equal = new Item("I2", "Equal Stock Item", category, null,
                LocalDate.now(), 100, 150, 10);  // stock = min  => true (boundary)

        category.addItem(low);
        category.addItem(equal);

        List<Item> alert = category.checkStockAlert();

        assertEquals(2, alert.size());
        assertTrue(alert.contains(low));
        assertTrue(alert.contains(equal));
    }

    @Test
    public void coversFalseBranch_stockGreaterThanMin_notIncludedInAlert() {
        Category category = new Category("C1", "Laptops", 10, "IT");

        Item ok = new Item("I3", "OK Stock Item", category, null,
                LocalDate.now(), 100, 150, 20); // stock > min => false

        category.addItem(ok);

        List<Item> alert = category.checkStockAlert();

        assertTrue(alert.isEmpty());
    }
}
