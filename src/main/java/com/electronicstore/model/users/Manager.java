package com.electronicstore.model.users;
import java.util.logging.Logger;

import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;

public class Manager extends User implements Serializable {
	private static final Logger LOGGER = Logger.getLogger(Manager.class.getName());

    private static final long serialVersionUID = 1L;
    private List<String> managedSectors;
    private List<Supplier> suppliers;

    public Manager(String id, String username, String password, String name,
                   String email, String phone) {
        super(id, username, password, name, email, phone);
        this.managedSectors = new ArrayList<>();
        this.suppliers = new ArrayList<>();
    }

    public List<String> getManagedSectors() { return managedSectors; }
    public void addManagedSector(String sector) {
        if (!managedSectors.contains(sector)) {
            managedSectors.add(sector);
        }
    }

    @Override
    public boolean login(String username, String password) {
        return this.getUsername().equals(username) && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        LOGGER.info("Manager logged out: " + this.getUsername());
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.getPassword().equals(oldPassword)) {
            this.setPassword(newPassword);
            return true;
        }
        return false;
    }

    // Manager specific methods
    public boolean addInventory(Item item, int quantity) {
        if (!managedSectors.contains(item.getCategory().getSector())) {
            return false;
        }
        item.updateStock(quantity);
        return true;
    }

    public void manageSuppliers(Supplier supplier, String action) {
        switch (action.toLowerCase()) {
            case "add" -> suppliers.add(supplier);
            case "remove" -> suppliers.remove(supplier);
            default -> throw new IllegalArgumentException("Invalid action: " + action);
        }
    }

    public List<Supplier> getSuppliers() {
        return new ArrayList<>(suppliers);
    }

    public String viewSectorStats(String sector) {
        if (!managedSectors.contains(sector)) {
            return "Not authorized for this sector";
        }
        // Implementation for generating sector statistics
        return "Stats for sector: " + sector;
    }
}