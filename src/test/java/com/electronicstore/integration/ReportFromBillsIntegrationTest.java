package com.electronicstore.integration;

import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class ReportFromBillsIntegrationTest {

    // ==========================
    // TEST CASE 1
    // ==========================
    @Test
    void saveAndReadSingleBill_successfully() {
        // GIVEN
        String testFile = "test_single_bill.dat";
        List<Bill> bills = new ArrayList<>();

        Bill bill = new Bill("B-TEST-1", "CASHIER-1");
        bills.add(bill);

        // WHEN
        FileHandler.saveListToFile(testFile, bills);
        List<Bill> loadedBills = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedBills);
        assertEquals(1, loadedBills.size());
        assertEquals("B-TEST-1", loadedBills.get(0).getBillNumber());
        assertEquals("CASHIER-1", loadedBills.get(0).getCashierId());
    }

    // ==========================
    // TEST CASE 2
    // ==========================
    @Test
    void saveAndReadMultipleBills_successfully() {
        // GIVEN
        String testFile = "test_multiple_bills.dat";
        List<Bill> bills = new ArrayList<>();

        bills.add(new Bill("B-TEST-1", "CASHIER-1"));
        bills.add(new Bill("B-TEST-2", "CASHIER-2"));
        bills.add(new Bill("B-TEST-3", "CASHIER-1"));

        // WHEN
        FileHandler.saveListToFile(testFile, bills);
        List<Bill> loadedBills = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedBills);
        assertEquals(3, loadedBills.size());

        assertEquals("B-TEST-1", loadedBills.get(0).getBillNumber());
        assertEquals("B-TEST-2", loadedBills.get(1).getBillNumber());
        assertEquals("B-TEST-3", loadedBills.get(2).getBillNumber());
    }
}
