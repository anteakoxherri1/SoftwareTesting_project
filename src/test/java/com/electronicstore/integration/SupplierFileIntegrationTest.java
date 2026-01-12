package com.electronicstore.integration;

import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class SupplierFileIntegrationTest {

    // ✅ TEST CASE 1 – Single Supplier
    @Test
    void saveAndReadSupplier_successfully() {
        // GIVEN
        String testFile = "test_suppliers.dat";

        Supplier supplier = new Supplier(
                "S1",
                "TechSupplier",
                "contact@test.com"
        );

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(supplier);

        // WHEN
        FileHandler.saveListToFile(testFile, suppliers);
        List<Supplier> loadedSuppliers = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedSuppliers, "List should not be null");
        assertEquals(1, loadedSuppliers.size());

        Supplier loadedSupplier = loadedSuppliers.get(0);
        assertEquals("TechSupplier", loadedSupplier.getName());
        assertEquals("contact@test.com", loadedSupplier.getContact());
    }

    // ✅ TEST CASE 2 – Multiple Suppliers
    @Test
    void saveAndReadMultipleSuppliers_successfully() {
        // GIVEN
        String testFile = "test_suppliers_multiple.dat";

        Supplier s1 = new Supplier("S1", "SupplierOne", "one@test.com");
        Supplier s2 = new Supplier("S2", "SupplierTwo", "two@test.com");

        List<Supplier> suppliers = new ArrayList<>();
        suppliers.add(s1);
        suppliers.add(s2);

        // WHEN
        FileHandler.saveListToFile(testFile, suppliers);
        List<Supplier> loadedSuppliers = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedSuppliers, "List should not be null");
        assertEquals(2, loadedSuppliers.size());

        assertEquals("SupplierOne", loadedSuppliers.get(0).getName());
        assertEquals("SupplierTwo", loadedSuppliers.get(1).getName());
    }
}
