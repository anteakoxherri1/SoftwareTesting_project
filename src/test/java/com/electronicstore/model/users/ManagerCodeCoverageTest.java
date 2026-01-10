package com.electronicstore.model.users;

import static org.junit.jupiter.api.Assertions.*;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

class ManagerCodeCoverageTest {

    private Manager manager;
    private Item item;

    @BeforeEach
    void setUp() {
        manager = new Manager("M1", "manager1", "pass", "Manager", "m@mail.com", "123");

        Category category = new Category("C1", "Laptops", 5, "Electronics");
        Supplier supplier = new Supplier("S1", "HP", "hp@mail.com");

        item = new Item(
                "I1",
                "Laptop",
                category,
                supplier,
                LocalDate.now(),
                500,
                800,
                10
        );
    }
    //_______Code Coverage Testing_____
    @Test
    void addInventory_zeroQuantity_shouldReturnFalseOrNoChange() {
        boolean result = manager.addInventory(item, 0);
        assertFalse(result);
    }
    //  invalid quantity
    @Test
    void addInventory_invalidQuantity_shouldReturnFalse() {
        boolean result = manager.addInventory(item, 0);
        assertFalse(result);
    }

    //  sector mismatch
    @Test
    void addInventory_sectorMismatch_shouldReturnFalse() {
        item.getCategory().setSector("HomeAppliances");
        boolean result = manager.addInventory(item, 5);
        assertFalse(result);
    }
}
