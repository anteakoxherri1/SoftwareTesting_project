package com.electronicstore.model.sales;

import com.electronicstore.model.inventory.Item;

import java.io.Serializable;

public class SaleItem implements Serializable {
    private static final long serialVersionUID = 1L;
    private String itemId;
    private Item item;
    private int quantity;
    private double price;

    public SaleItem(Item item, int quantity) {
        this.item = item;
        this.itemId = item.getId();
        this.quantity = quantity;
        this.price = item.getSellingPrice();
    }

    // Getters and Setters
    public String getItemId() { return itemId; }

    public Item getItem() { return item; }
    public void setItem(Item item) {
        this.item = item;
        this.itemId = item.getId();
        this.price = item.getSellingPrice();
    }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    // Business Methods
    public double calculateSubtotal() {
        return quantity * price;
    }

    @Override
    public String toString() {
        return String.format("SaleItem[item=%s, quantity=%d, price=%.2f]",
                itemId, quantity, price);
    }
}