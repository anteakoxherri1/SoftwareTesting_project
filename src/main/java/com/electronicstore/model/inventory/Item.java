package com.electronicstore.model.inventory;

import java.io.Serializable;
import java.time.LocalDate;

public class Item implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private Category category;
    private Supplier supplier;
    private LocalDate purchaseDate;
    private double purchasePrice;
    private double sellingPrice;
    private int stockQuantity;

    public Item(String id, String name, Category category, Supplier supplier,
                LocalDate purchaseDate, double purchasePrice, double sellingPrice,
                int stockQuantity) {
        this.id = id;
        this.name = name;
        this.category = category;
        this.supplier = supplier;
        this.purchaseDate = purchaseDate;
        this.purchasePrice = purchasePrice;
        this.sellingPrice = sellingPrice;
        this.stockQuantity = stockQuantity;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public Category getCategory() { return category; }
    public void setCategory(Category category) { this.category = category; }

    public Supplier getSupplier() { return supplier; }
    public void setSupplier(Supplier supplier) { this.supplier = supplier; }

    public LocalDate getPurchaseDate() { return purchaseDate; }
    public void setPurchaseDate(LocalDate purchaseDate) { this.purchaseDate = purchaseDate; }

    public double getPurchasePrice() { return purchasePrice; }
    public void setPurchasePrice(double purchasePrice) { this.purchasePrice = purchasePrice; }

    public double getSellingPrice() { return sellingPrice; }
    public void setSellingPrice(double sellingPrice) { this.sellingPrice = sellingPrice; }

    public int getStockQuantity() { return stockQuantity; }

    // Business Methods
    public boolean updateStock(int quantity) {
        int newQuantity = this.stockQuantity + quantity;
        if (newQuantity >= 0) {
            this.stockQuantity = newQuantity;
            return true;
        }
        return false;
    }

    public boolean checkAvailability(int requestedQuantity) {
        return stockQuantity >= requestedQuantity;
    }

    @Override
    public String toString() {
        return String.format("Item[id=%s, name=%s, price=%.2f, stock=%d]",
                id, name, sellingPrice, stockQuantity);
    }

    public void setStockQuantity(int quantity) {
        this.stockQuantity = quantity;
    }
}