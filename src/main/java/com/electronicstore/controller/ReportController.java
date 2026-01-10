package com.electronicstore.controller;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import com.electronicstore.view.components.AlertDialog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportController {
    private static final String BILLS_FILE = "bills.dat";
    private static final String USERS_FILE = "users.dat";
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String NL = System.lineSeparator();

    private final SessionState sessionState;

    public ReportController() {
        this.sessionState = SessionState.getInstance();
    }

    // Sales Reports
    public Map<String, Double> getDailySalesReport(LocalDate date) {
        try {
            List<Bill> bills = loadBills();
            return bills.stream()
                    .filter(bill -> bill.getDate().equals(date))
                    .collect(Collectors.groupingBy(
                            Bill::getCashierId,
                            Collectors.summingDouble(Bill::getTotalAmount)
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    public Map<LocalDate, Double> getMonthlySalesReport(int year, int month) {
        try {
            List<Bill> bills = loadBills();
            return bills.stream()
                    .filter(bill -> {
                        LocalDate date = bill.getDate();
                        return date.getYear() == year && date.getMonthValue() == month;
                    })
                    .collect(Collectors.groupingBy(
                            Bill::getDate,
                            Collectors.summingDouble(Bill::getTotalAmount)
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Inventory Reports
    public Map<String, Integer> getItemsSoldReport(LocalDate startDate, LocalDate endDate) {
        try {
            List<Bill> bills = loadBills();
            return bills.stream()
                    .filter(bill -> {
                        LocalDate date = bill.getDate();
                        return !date.isBefore(startDate) && !date.isAfter(endDate);
                    })
                    .flatMap(bill -> bill.getItems().stream())
                    .collect(Collectors.groupingBy(
                            item -> item.getItem().getName(),
                            Collectors.summingInt(SaleItem::getQuantity)
                    ));
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Performance Reports
    public Map<String, Map<String, Double>> getCashierPerformanceReport(LocalDate date) {
        if (!sessionState.isManager() && !sessionState.isAdministrator()) {
            return new HashMap<>();
        }

        try {
            List<Bill> bills = loadBills();
            Map<String, Map<String, Double>> report = new HashMap<>();

            bills.stream()
                    .filter(bill -> bill.getDate().equals(date))
                    .forEach(bill -> {
                        String cashierId = bill.getCashierId();
                        report.computeIfAbsent(cashierId, k -> new HashMap<>());

                        Map<String, Double> cashierStats = report.get(cashierId);
                        cashierStats.merge("totalSales", bill.getTotalAmount(), Double::sum);
                        cashierStats.merge("billCount", 1.0, Double::sum);
                        cashierStats.merge("averagePerBill",
                                bill.getTotalAmount(),
                                Double::sum);
                    });

            // Calculate averages
            report.forEach((cashierId, stats) -> {
                double totalSales = stats.get("totalSales");
                double billCount = stats.get("billCount");
                stats.put("averagePerBill", totalSales / billCount);
            });

            return report;
        } catch (IOException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    // Financial Reports
    public Map<String, Double> getFinancialSummary(LocalDate startDate, LocalDate endDate) {
        if (!sessionState.isAdministrator()) {
            return new HashMap<>();
        }

        try {
            List<Bill> bills = loadBills();
            List<User> users = FileHandler.readListFromFile(USERS_FILE); // kept as-is (even if unused elsewhere)

            double totalRevenue = bills.stream()
                    .filter(bill -> {
                        LocalDate date = bill.getDate();
                        return !date.isBefore(startDate) && !date.isAfter(endDate);
                    })
                    .mapToDouble(Bill::getTotalAmount)
                    .sum();

            double totalPurchaseCost = bills.stream()
                    .filter(bill -> {
                        LocalDate date = bill.getDate();
                        return !date.isBefore(startDate) && !date.isAfter(endDate);
                    })
                    .flatMap(bill -> bill.getItems().stream())
                    .mapToDouble(item -> item.getItem().getPurchasePrice() * item.getQuantity())
                    .sum();

            Map<String, Double> summary = new HashMap<>();
            summary.put("totalRevenue", totalRevenue);
            summary.put("totalCost", totalPurchaseCost);
            summary.put("grossProfit", totalRevenue - totalPurchaseCost);

            return summary;
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            return new HashMap<>();
        }
    }

    private List<Bill> loadBills() throws IOException {
        try {
            return FileHandler.readListFromFile(BILLS_FILE);
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    public String generateReport(String reportType, LocalDate startDate, LocalDate endDate) {
        if (!sessionState.isManager() && !sessionState.isAdministrator()) {
            return "Insufficient permissions to generate reports.";
        }

        return switch (reportType) {
            case "Sales Report" -> generateSalesReport(startDate, endDate);
            case "Inventory Report" -> generateInventoryReport(startDate, endDate);
            case "Low Stock Report" -> generateLowStockReport();
            case "Profit Report" -> generateProfitReport(startDate, endDate);
            default -> "Invalid report type selected.";
        };
    }

    private String generateSalesReport(LocalDate startDate, LocalDate endDate) {
        try {
            List<Bill> bills = loadBills();
            Map<LocalDate, List<Bill>> dailyBills = bills.stream()
                    .filter(bill -> !bill.getDate().isBefore(startDate) && !bill.getDate().isAfter(endDate))
                    .collect(Collectors.groupingBy(Bill::getDate));

            StringBuilder report = new StringBuilder();
            report.append("SALES REPORT").append(NL);
            report.append("Period: ")
                    .append(startDate.format(DATE_FORMATTER))
                    .append(" to ")
                    .append(endDate.format(DATE_FORMATTER))
                    .append(NL).append(NL);

            dailyBills.forEach((date, dayBills) -> {
                double dailyTotal = dayBills.stream()
                        .mapToDouble(Bill::getTotalAmount)
                        .sum();
                int billCount = dayBills.size();

                report.append(String.format("Date: %s%n", date.format(DATE_FORMATTER)));
                report.append(String.format("Total Sales: $%.2f%n", dailyTotal));
                report.append(String.format("Number of Bills: %d%n", billCount));
                report.append(String.format("Average Bill Amount: $%.2f%n%n",
                        billCount > 0 ? (dailyTotal / billCount) : 0.0));
            });

            double periodTotal = dailyBills.values().stream()
                    .flatMap(List::stream)
                    .mapToDouble(Bill::getTotalAmount)
                    .sum();

            report.append(NL).append("PERIOD SUMMARY").append(NL);
            report.append(String.format("Total Sales: $%.2f%n", periodTotal));
            report.append(String.format("Total Days: %d%n", dailyBills.size()));

            return report.toString();
        } catch (IOException e) {
            return "Error generating sales report: " + e.getMessage();
        }
    }

    private String generateInventoryReport(LocalDate startDate, LocalDate endDate) {
        StringBuilder report = new StringBuilder();
        report.append("INVENTORY MOVEMENT REPORT").append(NL);
        report.append("Period: ")
                .append(startDate.format(DATE_FORMATTER))
                .append(" to ")
                .append(endDate.format(DATE_FORMATTER))
                .append(NL).append(NL);

        Map<String, Integer> itemsSold = getItemsSoldReport(startDate, endDate);
        List<Item> currentInventory = new InventoryController().getAllItems();

        itemsSold.forEach((itemName, soldQuantity) -> {
            Optional<Item> item = currentInventory.stream()
                    .filter(i -> i.getName().equals(itemName))
                    .findFirst();

            if (item.isPresent()) {
                report.append(String.format("Item: %s%n", itemName));
                report.append(String.format("Current Stock: %d%n", item.get().getStockQuantity()));
                report.append(String.format("Sold in Period: %d%n", soldQuantity));
                report.append(String.format("Minimum Level: %d%n%n",
                        item.get().getCategory().getMinStockLevel()));
            }
        });

        return report.toString();
    }

    private String generateLowStockReport() {
        StringBuilder report = new StringBuilder();
        report.append("LOW STOCK ALERT REPORT").append(NL);
        report.append("Generated on: ")
                .append(LocalDate.now().format(DATE_FORMATTER))
                .append(NL).append(NL);

        List<Item> lowStockItems = new InventoryController().checkLowStock();

        if (lowStockItems.isEmpty()) {
            report.append("No items are currently below minimum stock levels.").append(NL);
        } else {
            lowStockItems.forEach(item -> {
                report.append(String.format("Item: %s%n", item.getName()));
                report.append(String.format("Current Stock: %d%n", item.getStockQuantity()));
                report.append(String.format("Minimum Level: %d%n",
                        item.getCategory().getMinStockLevel()));
                report.append(String.format("Required Order: %d%n%n",
                        item.getCategory().getMinStockLevel() - item.getStockQuantity()));
            });

            report.append(String.format("%nTotal Low Stock Items: %d%n", lowStockItems.size()));
        }

        return report.toString();
    }

    private String generateProfitReport(LocalDate startDate, LocalDate endDate) {
        if (!sessionState.isAdministrator()) {
            return "Insufficient permissions to generate profit report.";
        }

        StringBuilder report = new StringBuilder();
        report.append("PROFIT REPORT").append(NL);
        report.append("Period: ")
                .append(startDate.format(DATE_FORMATTER))
                .append(" to ")
                .append(endDate.format(DATE_FORMATTER))
                .append(NL).append(NL);

        Map<String, Double> summary = getFinancialSummary(startDate, endDate);
        double totalRevenue = summary.getOrDefault("totalRevenue", 0.0);
        double totalCost = summary.getOrDefault("totalCost", 0.0);
        double grossProfit = summary.getOrDefault("grossProfit", 0.0);
        double marginPercentage = totalRevenue > 0 ? (grossProfit / totalRevenue) * 100 : 0.0;

        report.append(String.format("Total Revenue: $%.2f%n", totalRevenue));
        report.append(String.format("Total Cost: $%.2f%n", totalCost));
        report.append(String.format("Gross Profit: $%.2f%n", grossProfit));
        report.append(String.format("Profit Margin: %.2f%%%n%n", marginPercentage));

        // Add daily breakdown
        try {
            List<Bill> bills = loadBills();
            Map<LocalDate, List<Bill>> dailyBills = bills.stream()
                    .filter(bill -> !bill.getDate().isBefore(startDate) && !bill.getDate().isAfter(endDate))
                    .collect(Collectors.groupingBy(Bill::getDate));

            report.append("DAILY BREAKDOWN").append(NL);

            dailyBills.forEach((date, dayBills) -> {
                double dayRevenue = dayBills.stream()
                        .mapToDouble(Bill::getTotalAmount)
                        .sum();
                double dayCost = dayBills.stream()
                        .flatMap(bill -> bill.getItems().stream())
                        .mapToDouble(item -> item.getItem().getPurchasePrice() * item.getQuantity())
                        .sum();
                double dayProfit = dayRevenue - dayCost;

                report.append(String.format("%nDate: %s%n", date.format(DATE_FORMATTER)));
                report.append(String.format("Revenue: $%.2f%n", dayRevenue));
                report.append(String.format("Cost: $%.2f%n", dayCost));
                report.append(String.format("Profit: $%.2f%n", dayProfit));
            });
        } catch (IOException e) {
            report.append("Error generating daily breakdown: ").append(e.getMessage());
        }

        return report.toString();
    }

    public boolean exportReport(String reportContent, String reportType) {
        try {
            String filename = reportType.toLowerCase().replace(" ", "_") + "_"
                    + LocalDate.now().format(DATE_FORMATTER) + ".txt";
            FileHandler.saveToTextFile(reportContent, filename);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public boolean exportData(boolean exportItems, boolean exportCategories,
                              boolean exportSuppliers, boolean exportSales) {
        try {
            if (exportItems) {
                List<Item> items = new InventoryController().getAllItems();
                exportDataToCSV(items, "items_export.csv");
            }
            if (exportCategories) {
                List<Category> categories = new InventoryController().getAllCategories();
                exportDataToCSV(categories, "categories_export.csv");
            }
            if (exportSuppliers) {
                List<Supplier> suppliers = new InventoryController().getAllSuppliers();
                exportDataToCSV(suppliers, "suppliers_export.csv");
            }
            if (exportSales) {
                List<Bill> bills = loadBills();
                exportDataToCSV(bills, "sales_export.csv");
            }
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private <T> void exportDataToCSV(List<T> data, String filename) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IOException("No data to export");
        }

        // Removed hard-coded "/" delimiter
        Path filepath = Paths.get(FileHandler.DATA_DIRECTORY).resolve(filename);

        T firstItem = data.get(0);

        try (FileWriter fileWriter = new FileWriter(filepath.toFile());
             CSVPrinter csvPrinter = new CSVPrinter(
                     fileWriter,
                     CSVFormat.DEFAULT.withHeader(getHeaders(firstItem))
             )) {

            for (T item : data) {
                csvPrinter.printRecord(getValues(item));
            }

            csvPrinter.flush();
            AlertDialog.showInfo("Export Successful", "Data exported to " + filename);
        } catch (IOException e) {
            AlertDialog.showError("Export Error", "Failed to export data: " + e.getMessage());
            throw e;
        }
    }

    private String[] getHeaders(Object item) {
        if (item instanceof Item) {
            return new String[]{"ID", "Name", "Category", "Supplier", "Purchase Price", "Selling Price", "Stock Quantity"};
        } else if (item instanceof Category) {
            return new String[]{"ID", "Name", "Minimum Stock Level", "Sector"};
        } else if (item instanceof Supplier) {
            return new String[]{"ID", "Name", "Contact"};
        } else if (item instanceof Bill) {
            return new String[]{"Bill Number", "Date", "Cashier ID", "Total Amount", "Items"};
        } else {
            throw new IllegalArgumentException("Unsupported data type for CSV export");
        }
    }

    private Object[] getValues(Object item) {
        if (item instanceof Item i) {
            return new Object[]{
                    i.getId(),
                    i.getName(),
                    i.getCategory().getName(),
                    i.getSupplier().getName(),
                    i.getPurchasePrice(),
                    i.getSellingPrice(),
                    i.getStockQuantity()
            };
        } else if (item instanceof Category c) {
            return new Object[]{
                    c.getId(),
                    c.getName(),
                    c.getMinStockLevel(),
                    c.getSector()
            };
        } else if (item instanceof Supplier s) {
            return new Object[]{
                    s.getId(),
                    s.getName(),
                    s.getContact()
            };
        } else if (item instanceof Bill b) {
            return new Object[]{
                    b.getBillNumber(),
                    b.getDateTime().format(DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                    b.getCashierId(),
                    b.getTotalAmount(),
                    formatBillItems(b.getItems())
            };
        } else {
            throw new IllegalArgumentException("Unsupported data type for CSV export");
        }
    }

    private String formatBillItems(List<SaleItem> items) {
        return items.stream()
                .map(item -> String.format("%s (x%d)", item.getItem().getName(), item.getQuantity()))
                .collect(Collectors.joining("; "));
    }

    public List<String> getRecentActivities(LocalDate start, LocalDate end) {
        try {
            List<Bill> bills = loadBills();
            return bills.stream()
                    .filter(bill -> !bill.getDate().isBefore(start) && !bill.getDate().isAfter(end))
                    .map(bill -> String.format("Bill #%s by %s on %s",
                            bill.getBillNumber(), bill.getCashierId(), bill.getDate()))
                    .collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }

    public double calculateInventoryValue(LocalDate start, LocalDate end) {
        try {
            List<Item> items = new InventoryController().getAllItems();
            return items.stream()
                    .mapToDouble(item -> item.getPurchasePrice() * item.getStockQuantity())
                    .sum();
        } catch (Exception e) {
            e.printStackTrace();
            return 0.0;
        }
    }

    public int getTotalProducts() {
        try {
            List<Item> items = new InventoryController().getAllItems();
            return items.size();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getActiveSectors() {
        try {
            List<Category> categories = new InventoryController().getAllCategories();
            return (int) categories.stream()
                    .map(Category::getSector)
                    .distinct()
                    .count();
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<String> getLowStockAlerts() {
        try {
            List<Item> lowStockItems = new InventoryController().checkLowStock();
            return lowStockItems.stream()
                    .map(item -> String.format("%s (%d)", item.getName(), item.getStockQuantity()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            e.printStackTrace();
            return new ArrayList<>();
        }
    }
}
