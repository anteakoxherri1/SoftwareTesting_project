package com.electronicstore.unit.model.inventory;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * UNIT TEST for Supplier class.
 */
class SupplierUnitTest {

    private Item createItem(String id, String name, Category category, Supplier supplier) {
        return new Item(id, name, category, supplier,
                LocalDate.now(), 10.0, 15.0, 5);
    }

    @Test
    void addCategory_shouldNotAllowDuplicates() {
        Supplier supplier = new Supplier("S1", "Dell", "contact");
        Category category = new Category("C1", "Laptops", 5, "IT");

        supplier.addCategory(category);
        supplier.addCategory(category);

        assertEquals(1, supplier.getProductCategories().size(),
                "Duplicate categories should not be added");
    }

    @Test
    void addProduct_shouldAddOnlyIfCategoryIsAllowed() {
        Supplier supplier = new Supplier("S1", "Dell", "contact");

        Category allowed = new Category("C1", "Laptops", 5, "IT");
        Category notAllowed = new Category("C2", "TV", 3, "Home");

        supplier.addCategory(allowed);

        Item validItem = createItem("I1", "Laptop", allowed, supplier);
        Item invalidItem = createItem("I2", "TV", notAllowed, supplier);

        supplier.addProduct(validItem);
        supplier.addProduct(invalidItem);

        List<Item> products = supplier.getProducts();

        assertTrue(products.contains(validItem));
        assertFalse(products.contains(invalidItem),
                "Products with unsupported categories must not be added");
    }
    @Test
    void removeProduct_shouldRemoveExistingProduct() {
        Supplier supplier = new Supplier("S1", "Dell", "contact");
        Category category = new Category("C1", "Laptops", 5, "IT");
        supplier.addCategory(category);

        Item item = createItem("I1", "Laptop", category, supplier);
        supplier.addProduct(item);
        supplier.removeProduct(item);

        assertTrue(supplier.getProducts().isEmpty(),
                "Product should be removed from supplier");
    }

    @Test
    void getProducts_shouldReturnCopyOfList() {
        Supplier supplier = new Supplier("S1", "Dell", "contact");
        Category category = new Category("C1", "Laptops", 5, "IT");
        supplier.addCategory(category);

        Item item = createItem("I1", "Laptop", category, supplier);
        supplier.addProduct(item);

        List<Item> copy = supplier.getProducts();
        copy.clear();

        assertEquals(1, supplier.getProducts().size(),
                "Internal product list must remain unchanged");
    }
}
