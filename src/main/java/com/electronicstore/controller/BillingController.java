package com.electronicstore.controller;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import java.io.IOException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

// ADDED import (only add)
import java.util.stream.Collectors;

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
        if (currentBill == null) {
            return false;
        }

        if (!item.checkAvailability(quantity)) {
            return false;
        }

        SaleItem saleItem = new SaleItem(item, quantity);
        currentBill.addItem(saleItem);
        item.updateStock(-quantity);
        return true;
    }

    public boolean removeItemFromBill(SaleItem saleItem) {
        if (currentBill == null) {
            return false;
        }

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

        try {
            List<Bill> bills = loadBills();
            bills.add(currentBill);
            FileHandler.saveListToFile(bills, BILLS_FILE);

            // ADDED: persist updated stock quantities to items.dat (only add)
            persistUpdatedStock();

            FileHandler.exportBill(currentBill);

            Bill finalizedBill = currentBill;
            currentBill = null;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Bill> getDailyBills() {
        try {
            List<Bill> allBills = loadBills();
            return allBills.stream()
                    .filter(bill -> bill.getDate().equals(LocalDate.now()))
                    .filter(bill -> bill.getCashierId().equals(
                            sessionState.getCurrentUser().getId()))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Bill> loadBills() throws IOException {
        try {
            return FileHandler.readListFromFile(BILLS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public Bill getCurrentBill() {
        return currentBill;
    }

    // ADDED: helper to save updated stock after selling items (only add)
    private void persistUpdatedStock() {
        try {
            // Load items currently stored (items.dat)
            List<Item> storedItems = FileHandler.readListFromFile("items.dat");

            // Update only the items involved in this bill (by matching IDs)
            for (SaleItem s : currentBill.getItems()) {
                Item soldItem = s.getItem();

                for (int i = 0; i < storedItems.size(); i++) {
                    if (storedItems.get(i).getId().equals(soldItem.getId())) {
                        storedItems.set(i, soldItem); // soldItem already has updated stock
                        break;
                    }
                }
            }

            FileHandler.saveListToFile(storedItems, "items.dat");
        } catch (Exception ignored) {
            // Keep billing working even if saving items fails
        }
    }
}

/*
package com.electronicstore.controller;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import java.io.IOException;
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
        if (currentBill == null) {
            return false;
        }

        if (!item.checkAvailability(quantity)) {
            return false;
        }

        SaleItem saleItem = new SaleItem(item, quantity);
        currentBill.addItem(saleItem);
        item.updateStock(-quantity);
        return true;
    }

    public boolean removeItemFromBill(SaleItem saleItem) {
        if (currentBill == null) {
            return false;
        }

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

        try {
            List<Bill> bills = loadBills();
            bills.add(currentBill);
            FileHandler.saveListToFile(bills, BILLS_FILE);

            FileHandler.exportBill(currentBill);

            Bill finalizedBill = currentBill;
            currentBill = null;

            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public List<Bill> getDailyBills() {
        try {
            List<Bill> allBills = loadBills();
            return allBills.stream()
                    .filter(bill -> bill.getDate().equals(LocalDate.now()))
                    .filter(bill -> bill.getCashierId().equals(
                            sessionState.getCurrentUser().getId()))
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    private List<Bill> loadBills() throws IOException {
        try {
            return FileHandler.readListFromFile(BILLS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public Bill getCurrentBill() {
        return currentBill;
    }
}
*/
