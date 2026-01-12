package com.electronicstore.unit.model.inventory;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST for Category class.
 */
class CategoryUnitTest {

    // Helper method to create test items quickly
    private Item createItem(String id, String name, Category category, int stock) {
        return new Item(id, name, category, null,
                LocalDate.now(), 10.0, 15.0, stock);
    }

    @Test
    void addItem_shouldNotAddDuplicateItems() {
        // Purpose: ensure duplicate items are not added
        Category category = new Category("C1", "Laptops", 5, "IT");
        Item item = createItem("I1", "Laptop A", category, 10);

        category.addItem(item);
        category.addItem(item);

        assertEquals(1, category.getItems().size(),
                "Duplicate items must not be added");
    }

    @Test
    void removeItem_shouldRemoveItemFromCategory() {
        // Purpose: ensure items are removed correctly
        Category category = new Category("C1", "Laptops", 5, "IT");
        Item item = createItem("I1", "Laptop A", category, 10);

        category.addItem(item);
        category.removeItem(item);

        assertTrue(category.getItems().isEmpty(),
                "Item should be removed from the category");
    }

    @Test
    void getItems_shouldReturnCopyOfInternalList() {
        // Purpose: protect encapsulation (no external modification)
        Category category = new Category("C1", "Laptops", 5, "IT");
        Item item = createItem("I1", "Laptop A", category, 10);
        category.addItem(item);

        List<Item> returnedList = category.getItems();
        returnedList.clear();

        assertEquals(1, category.getItems().size(),
                "Internal list must not be affected by external changes");
    }

    @Test
    void toString_shouldContainImportantFields() {
        // Purpose: verify string representation
        Category category = new Category("C1", "Laptops", 5, "IT");
        String result = category.toString();

        assertTrue(result.contains("C1"));
        assertTrue(result.toLowerCase().contains("laptops"));
        assertTrue(result.contains("5"));
    }
}
