package com.electronicstore.model.sales;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Bill implements Serializable {
    private static final long serialVersionUID = 1L;
    private String billNumber;
    private LocalDateTime date;
    private String cashierId;
    private List<SaleItem> items;
    private double totalAmount;

    public Bill(String billNumber, String cashierId) {
        this.billNumber = billNumber;
        this.date = LocalDateTime.now();
        this.cashierId = cashierId;
        this.items = new ArrayList<>();
        this.totalAmount = 0.0;
    }
    public Bill(String billNumber, String cashierId, LocalDateTime date, double totalAmount) {
        this.billNumber = billNumber;
        this.cashierId = cashierId;
        this.date = date;
        this.items = new ArrayList<>();
        this.totalAmount = totalAmount;
    }

    // Getters and Setters
    public String getBillNumber() { return billNumber; }
    public void setBillNumber(String billNumber) { this.billNumber = billNumber; }

    public LocalDateTime getDateTime() { return date; }
    public LocalDate getDate() { return date.toLocalDate(); }

    public String getCashierId() { return cashierId; }
    public void setCashierId(String cashierId) { this.cashierId = cashierId; }

    public List<SaleItem> getItems() { return new ArrayList<>(items); }

    public double getTotalAmount() { return totalAmount; }

    // Business Methods
    public void addItem(SaleItem item) {
        items.add(item);
        calculateTotal();
    }

    public void removeItem(SaleItem item) {
        items.remove(item);
        calculateTotal();
    }

    public void calculateTotal() {
        this.totalAmount = items.stream()
                .mapToDouble(SaleItem::calculateSubtotal)
                .sum();
    }

    public String generatePrintableFormat() {
        StringBuilder sb = new StringBuilder();
        sb.append("=================================%n");
        sb.append("         ELECTRONIC STORE        %n");
        sb.append("=================================%n");
        sb.append(String.format("Bill Number: %s%n", billNumber));
        sb.append(String.format("Date: %s%n", date));
        sb.append(String.format("Cashier ID: %s%n", cashierId));
        sb.append("---------------------------------%n");
        sb.append("Items:%n");

        items.forEach(item ->
                sb.append(String.format(
                        "%s x%d = $%.2f%n",
                        item.getItem().getName(),
                        item.getQuantity(),
                        item.calculateSubtotal()
                ))
        );

        sb.append("---------------------------------%n");
        sb.append(String.format("Total Amount: $%.2f%n", totalAmount));
        sb.append("=================================%n");
        sb.append("Thank you for shopping with us!%n");

        return sb.toString();
    }

    @Override
    public String toString() {
        return String.format(
                "Bill[number=%s, date=%s, total=%.2f]",
                billNumber, date, totalAmount
        );
    }
}
