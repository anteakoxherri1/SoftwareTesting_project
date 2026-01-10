package com.electronicstore.model.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Category implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private int minStockLevel;
    private String sector;
    private List<Item> items;

    public Category(String id, String name, int minStockLevel, String sector) {
        this.id = id;
        this.name = name;
        this.minStockLevel = minStockLevel;
        this.sector = sector;
        this.items = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getMinStockLevel() { return minStockLevel; }
    public void setMinStockLevel(int minStockLevel) { this.minStockLevel = minStockLevel; }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    public List<Item> getItems() { return new ArrayList<>(items); }

    public void addItem(Item item) {
        if (!items.contains(item)) {
            items.add(item);
        }
    }

    public void removeItem(Item item) {
        items.remove(item);
    }

    // Business Methods
    public List<Item> checkStockAlert() {
        return items.stream()
                .filter(item -> item.getStockQuantity() <= minStockLevel)
                .toList();
    }

    @Override
    public String toString() {
        return String.format("Category[id=%s, name=%s, minStock=%d]",
                id, name, minStockLevel);
    }
}