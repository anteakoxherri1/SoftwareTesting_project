package com.electronicstore.controller;

import com.electronicstore.model.inventory.*;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import java.io.IOException;
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

    // Item management
    public boolean addItem(String name, Category category, Supplier supplier,
                           double purchasePrice, double sellingPrice, int quantity) {
        if (!sessionState.isManager()) {
            return false;
        }

        try {
            List<Item> items = loadItems();
            String itemId = "I" + UUID.randomUUID().toString().substring(0, 8);

            Item newItem = new Item(itemId, name, category, supplier,
                    LocalDate.now(), purchasePrice,
                    sellingPrice, quantity);
            items.add(newItem);

            FileHandler.saveListToFile(items, ITEMS_FILE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean updateItemStock(String itemId, int quantity) {
        try {
            List<Item> items = loadItems();
            Optional<Item> item = items.stream()
                    .filter(i -> i.getId().equals(itemId))
                    .findFirst();

            if (item.isPresent()) {
                if (item.get().updateStock(quantity)) {
                    FileHandler.saveListToFile(items, ITEMS_FILE);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Category management
    public boolean addCategory(String name, int minStockLevel, String sector) {
        if (!sessionState.isManager()) {
            return false;
        }

        try {
            List<Category> categories = loadCategories();
            String categoryId = "C" + UUID.randomUUID().toString().substring(0, 8);

            Category newCategory = new Category(categoryId, name, minStockLevel, sector);
            categories.add(newCategory);

            FileHandler.saveListToFile(categories, CATEGORIES_FILE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Supplier management
    public boolean addSupplier(String name, String contact) {
        if (!sessionState.isManager()) {
            return false;
        }

        try {
            List<Supplier> suppliers = loadSuppliers();
            String supplierId = "S" + UUID.randomUUID().toString().substring(0, 8);

            Supplier newSupplier = new Supplier(supplierId, name, contact);
            suppliers.add(newSupplier);

            FileHandler.saveListToFile(suppliers, SUPPLIERS_FILE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    // Stock alerts
    public List<Item> checkLowStock() {
        try {
            List<Item> items = loadItems();
            List<Category> categories = loadCategories();

            return items.stream()
                    .filter(item -> {
                        Category category = item.getCategory();
                        return item.getStockQuantity() <= category.getMinStockLevel();
                    })
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    // Load data methods
    private List<Item> loadItems() throws IOException {
        try {
            return FileHandler.readListFromFile(ITEMS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private List<Category> loadCategories() throws IOException {
        try {
            return FileHandler.readListFromFile(CATEGORIES_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private List<Supplier> loadSuppliers() throws IOException {
        try {
            return FileHandler.readListFromFile(SUPPLIERS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public List<Item> getAvailableItems() {
        try {
            return loadItems().stream()
                    .filter(item -> item.getStockQuantity() > 0)
                    .toList();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Category> getAllCategories() {
        try {
            return loadCategories();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Supplier> getAllSuppliers() {
        try {
            return loadSuppliers();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public List<Item> getAllItems() {
        try {
            return loadItems();
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public boolean updateItem(Item item) {
        if (!sessionState.isManager()) {
            return false;
        }

        try {
            List<Item> items = loadItems();

            // Find and replace the item
            for (int i = 0; i < items.size(); i++) {
                if (items.get(i).getId().equals(item.getId())) {
                    items.set(i, item);
                    FileHandler.saveListToFile(items, ITEMS_FILE);
                    return true;
                }
            }
            return false;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean deleteItem(String id) {
        if (!sessionState.isManager()) {
            return false;
        }

        try {
            List<Item> items = loadItems();
            items.removeIf(item -> item.getId().equals(id));

            FileHandler.saveListToFile(items, ITEMS_FILE);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void deleteCategory(Category category) {
        if (!sessionState.isManager()) {
            return;
        }

        try {
            List<Category> categories = loadCategories();
            categories.removeIf(c -> c.getId().equals(category.getId()));

            FileHandler.saveListToFile(categories, CATEGORIES_FILE);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}