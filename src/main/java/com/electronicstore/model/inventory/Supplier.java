package com.electronicstore.model.inventory;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Supplier  implements Serializable {
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String contact;
    private List<Category> productCategories;
    private List<Item> products;

    public Supplier(String id, String name, String contact) {
        this.id = id;
        this.name = name;
        this.contact = contact;
        this.productCategories = new ArrayList<>();
        this.products = new ArrayList<>();
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getContact() { return contact; }
    public void setContact(String contact) { this.contact = contact; }

    public List<Category> getProductCategories() {
        return new ArrayList<>(productCategories);
    }

    public void addCategory(Category category) {
        if (!productCategories.contains(category)) {
            productCategories.add(category);
        }
    }

    public void removeCategory(Category category) {
        productCategories.remove(category);
    }

    // Business Methods
    public List<Item> getProducts() {
        return new ArrayList<>(products);
    }

    public void addProduct(Item product) {
        if (!products.contains(product) &&
                productCategories.contains(product.getCategory())) {
            products.add(product);
        }
    }

    public void removeProduct(Item product) {
        products.remove(product);
    }

    @Override
    public String toString() {
        return String.format("Supplier[id=%s, name=%s, contact=%s]",
                id, name, contact);
    }
}