package com.electronicstore.model.sales;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
    public String getBillNumber() {
        return billNumber;
    }

    public void setBillNumber(String billNumber) {
        this.billNumber = billNumber;
    }

    public LocalDateTime getDateTime() {
        return date;
    }

    public LocalDate getDate() {
        return date.toLocalDate();
    }

    public String getCashierId() {
        return cashierId;
    }

    public void setCashierId(String cashierId) {
        this.cashierId = cashierId;
    }

    public List<SaleItem> getItems() {
        return new ArrayList<>(items);
    }

    public double getTotalAmount() {
        return totalAmount;
    }

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

    // ✅ FIXED: proper printable format with real new lines
    public String generatePrintableFormat() {
        String nl = System.lineSeparator();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

        StringBuilder sb = new StringBuilder();

        sb.append("=================================").append(nl);
        sb.append("         ELECTRONIC STORE        ").append(nl);
        sb.append("=================================").append(nl);
        sb.append("Bill Number: ").append(billNumber).append(nl);
        sb.append("Date: ").append(date.format(formatter)).append(nl);
        sb.append("Cashier ID: ").append(cashierId).append(nl);
        sb.append("---------------------------------").append(nl);
        sb.append("Items:").append(nl);

        for (SaleItem item : items) {
            sb.append(item.getItem().getName())
              .append(" x").append(item.getQuantity())
              .append(" = $")
              .append(String.format("%.2f", item.calculateSubtotal()))
              .append(nl);
        }

        sb.append("---------------------------------").append(nl);
        sb.append("Total Amount: $")
          .append(String.format("%.2f", totalAmount))
          .append(nl);
        sb.append("=================================").append(nl);
        sb.append("Thank you for shopping with us!").append(nl);

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
