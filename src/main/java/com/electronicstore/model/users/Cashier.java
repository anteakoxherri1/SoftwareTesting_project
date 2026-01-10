package com.electronicstore.model.users;

import com.electronicstore.model.sales.Bill;

import java.io.Serializable;
import java.util.List;
import java.util.ArrayList;
import java.time.LocalDate;

public class Cashier extends User implements Serializable {
    private static final long serialVersionUID = 1L;
    private String sector;
    private List<Bill> dailyBills;

    public Cashier(String id, String username, String password, String name,
                   String email, String phone, String sector) {
        super(id, username, password, name, email, phone);
        this.sector = sector;
        this.dailyBills = new ArrayList<>();
    }

    public String getSector() { return sector; }
    public void setSector(String sector) { this.sector = sector; }

    @Override
    public boolean login(String username, String password) {
        return this.getUsername().equals(username) && this.getPassword().equals(password);
    }

    @Override
    public void logout() {
        System.out.println("Cashier logged out: " + this.getUsername());
    }

    @Override
    public boolean changePassword(String oldPassword, String newPassword) {
        if (this.getPassword().equals(oldPassword)) {
            this.setPassword(newPassword);
            return true;
        }
        return false;
    }

    public List<Bill> viewDailyBills() {
        return dailyBills.stream()
                .filter(bill -> bill.getDate().equals(LocalDate.now()))
                .toList();
    }

    public double getTotalSales() {
        return dailyBills.stream()
                .filter(bill -> bill.getDate().equals(LocalDate.now()))
                .mapToDouble(Bill::getTotalAmount)
                .sum();
    }
}