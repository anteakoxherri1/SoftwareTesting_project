package com.electronicstore.controller;

import com.electronicstore.model.inventory.*;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;

import java.time.LocalDate;
import java.util.*;

public class InventoryController {

    private static final String ITEMS_FILE = "items.dat";
    private static final String CATEGORIES_FILE = "categories.dat";
    private static final String SUPPLIERS_FILE = "suppliers.dat";

    private final SessionState sessionState;

    public InventoryController() {
        this.sessionState = SessionState.getInstance();
    }

    // ======================
    // ITEM MANAGEMENT
    // ======================
    public boolean addItem(String name, Category category, Supplier supplier,
                           double purchasePrice, double sellingPrice, int quantity) {

        if (!sessionState.isManager()) {
            return false;
        }

        List<Item> items = loadItems();
        String itemId = "I" + UUID.randomUUID().toString().substring(0, 8);

        Item newItem = new Item(
                itemId,
                name,
                category,
                supplier,
                LocalDate.now(),
                purchasePrice,
                sellingPrice,
                quantity
        );

        items.add(newItem);
        FileHandler.saveListToFile(ITEMS_FILE, items);
        return true;
    }

    public boolean updateItemStock(String itemId, int quantity) {
        List<Item> items = loadItems();

        for (Item item : items) {
            if (item.getId().equals(itemId)) {
                if (item.updateStock(quantity)) {
                    FileHandler.saveListToFile(ITEMS_FILE, items);
                    return true;
                }
                return false;
            }
        }
        return false;
    }

    // ======================
    // CATEGORY MANAGEMENT
    // ======================
    public boolean addCategory(String name, int minStockLevel, String sector) {
        if (!sessionState.isManager()) {
            return false;
        }

        List<Category> categories = loadCategories();
        String categoryId = "C" + UUID.randomUUID().toString().substring(0, 8);

        Category newCategory = new Category(categoryId, name, minStockLevel, sector);
        categories.add(newCategory);

        FileHandler.saveListToFile(CATEGORIES_FILE, categories);
        return true;
    }

    // ======================
    // SUPPLIER MANAGEMENT
    // ======================
    public boolean addSupplier(String name, String contact) {
        if (!sessionState.isManager()) {
            return false;
        }

        List<Supplier> suppliers = loadSuppliers();
        String supplierId = "S" + UUID.randomUUID().toString().substring(0, 8);

        Supplier newSupplier = new Supplier(supplierId, name, contact);
        suppliers.add(newSupplier);

        FileHandler.saveListToFile(SUPPLIERS_FILE, suppliers);
        return true;
    }

    // ======================
    // STOCK ALERTS
    // ======================
    public List<Item> checkLowStock() {
        List<Item> items = loadItems();

        return items.stream()
                .filter(item ->
                        item.getStockQuantity()
                                <= item.getCategory().getMinStockLevel())
                .toList();
    }

    // ======================
    // LOADERS (NO EXCEPTIONS)
    // ======================
    private List<Item> loadItems() {
        return FileHandler.readListFromFile(ITEMS_FILE);
    }

    private List<Category> loadCategories() {
        return FileHandler.readListFromFile(CATEGORIES_FILE);
    }

    private List<Supplier> loadSuppliers() {
        return FileHandler.readListFromFile(SUPPLIERS_FILE);
    }

    // ======================
    // GETTERS
    // ======================
    public List<Item> getAvailableItems() {
        return loadItems().stream()
                .filter(item -> item.getStockQuantity() > 0)
                .toList();
    }

    public List<Category> getAllCategories() {
        return loadCategories();
    }

    public List<Supplier> getAllSuppliers() {
        return loadSuppliers();
    }

    public List<Item> getAllItems() {
        return loadItems();
    }

    // ======================
    // UPDATE & DELETE
    // ======================
    public boolean updateItem(Item item) {
        if (!sessionState.isManager()) {
            return false;
        }

        List<Item> items = loadItems();

        for (int i = 0; i < items.size(); i++) {
            if (items.get(i).getId().equals(item.getId())) {
                items.set(i, item);
                FileHandler.saveListToFile(ITEMS_FILE, items);
                return true;
            }
        }
        return false;
    }

    public boolean deleteItem(String id) {
        if (!sessionState.isManager()) {
            return false;
        }

        List<Item> items = loadItems();
        boolean removed = items.removeIf(item -> item.getId().equals(id));

        if (removed) {
            FileHandler.saveListToFile(ITEMS_FILE, items);
        }
        return removed;
    }

    public void deleteCategory(Category category) {
        if (!sessionState.isManager()) {
            return;
        }

        List<Category> categories = loadCategories();
        categories.removeIf(c -> c.getId().equals(category.getId()));

        FileHandler.saveListToFile(CATEGORIES_FILE, categories);
    }
}
