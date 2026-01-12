package com.electronicstore.controller;

import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import com.electronicstore.view.components.AlertDialog;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class ReportController {

    private static final String BILLS_FILE = "bills.dat";
    private static final DateTimeFormatter DATE_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final String NL = System.lineSeparator();
    private static final String LABEL_PERIOD = "Period: ";

    private final SessionState sessionState;

    public ReportController() {
        this.sessionState = SessionState.getInstance();
    }

    // ======================
    // LOAD BILLS (NO EXCEPTIONS)
    // ======================
    private List<Bill> loadBills() {
        return FileHandler.readListFromFile(BILLS_FILE);
    }

    // ======================
    // SALES REPORTS
    // ======================
    public Map<String, Double> getDailySalesReport(LocalDate date) {
        return loadBills().stream()
                .filter(bill -> bill.getDate().equals(date))
                .collect(Collectors.groupingBy(
                        Bill::getCashierId,
                        Collectors.summingDouble(Bill::getTotalAmount)
                ));
    }

    public Map<LocalDate, Double> getMonthlySalesReport(int year, int month) {
        return loadBills().stream()
                .filter(bill -> {
                    LocalDate d = bill.getDate();
                    return d.getYear() == year && d.getMonthValue() == month;
                })
                .collect(Collectors.groupingBy(
                        Bill::getDate,
                        Collectors.summingDouble(Bill::getTotalAmount)
                ));
    }

    // ======================
    // INVENTORY REPORTS
    // ======================
    public Map<String, Integer> getItemsSoldReport(LocalDate start, LocalDate end) {
        return loadBills().stream()
                .filter(bill -> {
                    LocalDate d = bill.getDate();
                    return !d.isBefore(start) && !d.isAfter(end);
                })
                .flatMap(bill -> bill.getItems().stream())
                .collect(Collectors.groupingBy(
                        si -> si.getItem().getName(),
                        Collectors.summingInt(SaleItem::getQuantity)
                ));
    }

    // ======================
    // PERFORMANCE REPORT
    // ======================
    public Map<String, Map<String, Double>> getCashierPerformanceReport(LocalDate date) {
        if (!sessionState.isManager() && !sessionState.isAdministrator()) {
            return new HashMap<>();
        }

        Map<String, Map<String, Double>> report = new HashMap<>();

        loadBills().stream()
                .filter(bill -> bill.getDate().equals(date))
                .forEach(bill -> {
                    String cashierId = bill.getCashierId();
                    report.putIfAbsent(cashierId, new HashMap<>());
                    Map<String, Double> stats = report.get(cashierId);

                    stats.merge("totalSales", bill.getTotalAmount(), Double::sum);
                    stats.merge("billCount", 1.0, Double::sum);
                });

        report.forEach((id, stats) -> {
            double total = stats.getOrDefault("totalSales", 0.0);
            double count = stats.getOrDefault("billCount", 0.0);
            stats.put("averagePerBill", count > 0 ? total / count : 0.0);
        });

        return report;
    }

    // ======================
    // EXPORT REPORT (TXT)
    // ======================
    public boolean exportReport(String content, String reportType) {
        String filename = reportType.toLowerCase().replace(" ", "_") + "_"
                + LocalDate.now().format(DATE_FORMATTER) + ".txt";

        FileHandler.exportBillToTextFile(filename, content);
        return true;
    }

    // ======================
    // CSV EXPORT
    // ======================
    private <T> void exportDataToCSV(List<T> data, String filename) throws IOException {
        if (data == null || data.isEmpty()) {
            throw new IOException("No data to export");
        }

        Path filePath = FileHandler.DATA_DIRECTORY.resolve(filename);

        String[] headers = getHeaders(data.get(0));

        CSVFormat format = CSVFormat.DEFAULT.builder()
                .setHeader(headers)
                .build();

        try (FileWriter writer = new FileWriter(filePath.toFile());
             CSVPrinter printer = new CSVPrinter(writer, format)) {

            for (T item : data) {
                printer.printRecord(getValues(item));
            }

            printer.flush();
            AlertDialog.showInfo("Export Successful", filename);
        }
    }

    // ======================
    // CSV HELPERS
    // ======================
    private String[] getHeaders(Object o) {
        return switch (o) {
            case Item ignored -> new String[]{"ID", "Name", "Category", "Supplier", "Buy", "Sell", "Stock"};
            case Category ignored -> new String[]{"ID", "Name", "Min Stock", "Sector"};
            case Supplier ignored -> new String[]{"ID", "Name", "Contact"};
            case Bill ignored -> new String[]{"Bill No", "Date", "Cashier", "Total", "Items"};
            default -> throw new IllegalArgumentException();
        };
    }

    private Object[] getValues(Object o) {
        if (o instanceof Item i)
            return new Object[]{i.getId(), i.getName(), i.getCategory().getName(),
                    i.getSupplier().getName(), i.getPurchasePrice(),
                    i.getSellingPrice(), i.getStockQuantity()};
        if (o instanceof Category c)
            return new Object[]{c.getId(), c.getName(), c.getMinStockLevel(), c.getSector()};
        if (o instanceof Supplier s)
            return new Object[]{s.getId(), s.getName(), s.getContact()};
        if (o instanceof Bill b)
            return new Object[]{b.getBillNumber(), b.getDate(),
                    b.getCashierId(), b.getTotalAmount(),
                    b.getItems().size()};
        throw new IllegalArgumentException();
    }
}

