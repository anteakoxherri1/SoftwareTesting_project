package com.electronicstore.controller;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class BillingController {

    private static final String BILLS_FILE = "bills.dat";
    private final SessionState sessionState;
    private Bill currentBill;

    public BillingController() {
        this.sessionState = SessionState.getInstance();
    }

    public Bill createNewBill() {
        if (!sessionState.isCashier()) {
            throw new IllegalStateException("Only cashiers can create bills");
        }

        String billNumber = "B" + UUID.randomUUID().toString().substring(0, 8);
        currentBill = new Bill(billNumber, sessionState.getCurrentUser().getId());
        return currentBill;
    }

    public boolean addItemToBill(Item item, int quantity) {
        if (currentBill == null) return false;
        if (!item.checkAvailability(quantity)) return false;

        SaleItem saleItem = new SaleItem(item, quantity);
        currentBill.addItem(saleItem);
        item.updateStock(-quantity);
        return true;
    }

    public boolean removeItemFromBill(SaleItem saleItem) {
        if (currentBill == null) return false;

        currentBill.removeItem(saleItem);
        saleItem.getItem().updateStock(saleItem.getQuantity());
        return true;
    }

    public double getBillTotal() {
        return currentBill != null ? currentBill.getTotalAmount() : 0.0;
    }

    public boolean finalizeBill() {
        if (currentBill == null || currentBill.getItems().isEmpty()) {
            return false;
        }

        List<Bill> bills = loadBills();
        bills.add(currentBill);

        // ✅ FIX 1: renditja e saktë e parametrave
        FileHandler.saveListToFile(BILLS_FILE, bills);

        // ✅ FIX 2: eksport korrekt i faturës
        FileHandler.exportBillToTextFile(
                currentBill.getBillNumber(),
                currentBill.toString()
        );

        currentBill = null;
        return true;
    }

    public List<Bill> getDailyBills() {
        List<Bill> allBills = loadBills();

        return allBills.stream()
                .filter(bill -> bill.getDate().equals(LocalDate.now()))
                .filter(bill -> bill.getCashierId().equals(
                        sessionState.getCurrentUser().getId()))
                .toList();
    }

    private List<Bill> loadBills() {
        // ✅ FIX 3: pa try-catch të panevojshëm
        return FileHandler.readListFromFile(BILLS_FILE);
    }

    public Bill getCurrentBill() {
        return currentBill;
    }
}
