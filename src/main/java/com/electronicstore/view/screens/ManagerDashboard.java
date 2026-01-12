/*package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.InventoryController;
import com.electronicstore.controller.ReportController;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.view.components.AlertDialog;
import com.electronicstore.view.components.StockLevelIndicator;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.List;

public class ManagerDashboard {
    private final ManagerDashboardView view;
    private final App app;
    private final InventoryController inventoryController;
    private final ReportController reportController;

    public ManagerDashboard(App app) {
        this.app = app;
        this.inventoryController = new InventoryController();
        this.reportController = new ReportController();
        this.view = new ManagerDashboardView();

        initialize();
        setupEventHandlers();
    }

    private void initialize() {
        view.getWelcomeLabel().setText("Welcome, " + app.getCurrentUser().getName());
        view.getStartDate().setValue(LocalDate.now().minusMonths(1));
        view.getEndDate().setValue(LocalDate.now());
        refreshDashboard();
    }

    private void setupEventHandlers() {
        // Setup button event handlers
        view.getNewItemBtn().setOnAction(e -> handleNewItem());
        view.getManageInventoryBtn().setOnAction(e -> handleManageInventory());
        view.getManageSuppliersBtn().setOnAction(e -> handleManageSuppliers());
        view.getManageCategoriesBtn().setOnAction(e -> handleManageCategories());
        view.getViewReportsBtn().setOnAction(e -> handleViewReports());
        view.getApplyFilterBtn().setOnAction(e -> handleApplyDateFilter());
        //view.getGenerateReportBtn().setOnAction(this::handleGenerateReport);
        //view.getExportDataBtn().setOnAction(this::handleExportData);
    }

    private void handleNewItem() {
        if (inventoryController.getAllCategories().isEmpty()) {
            AlertDialog.showError("Error", "Please add at least one category before adding items.");
            return;
        }

        if (inventoryController.getAllSuppliers().isEmpty()) {
            AlertDialog.showError("Error", "Please add at least one supplier before adding items.");
            return;
        }

        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setDialogPane(new AddItemDialog(inventoryController));
        dialog.showAndWait();
        refreshDashboard();
    }

    private void handleManageInventory() {
        app.showInventoryManagement();
    }

    private void handleManageSuppliers() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supplier Management");
        dialog.setDialogPane(new SupplierManagementDialog(inventoryController));
        dialog.showAndWait();
        refreshDashboard();
    }

    private void handleManageCategories() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Category Management");
        dialog.setDialogPane(new CategoryManagementDialog(inventoryController));
        dialog.showAndWait();
        refreshDashboard();
    }

    private void handleViewReports() {

    }

    private void handleApplyDateFilter() {
        updateReports(view.getStartDate().getValue(), view.getEndDate().getValue());
    }

    private void refreshDashboard() {
        updateInventoryCard();
        List<Item> lowStockItems = inventoryController.checkLowStock();
        updateStockAlerts(lowStockItems);
        updateSalesChart();
    }

    private void updateInventoryCard() {
        VBox inventoryCard = view.getInventoryCard();
        inventoryCard.getChildren().clear();
        Label inventoryTitle = new Label("Inventory Statistics");
        inventoryTitle.getStyleClass().add("card-title");
        inventoryCard.getChildren().add(inventoryTitle);

        List<Item> items = inventoryController.getAllItems();

        if (items.isEmpty()) {
            VBox emptyState = new VBox(10);
            emptyState.setAlignment(Pos.CENTER);

            Label emptyLabel = new Label("No items in inventory");
            emptyLabel.getStyleClass().add("empty-state-label");

            Button addItemButton = new Button("Add Your First Item");
            addItemButton.getStyleClass().addAll("button", "button-primary");
            addItemButton.setOnAction(e -> handleNewItem());

            emptyState.getChildren().addAll(emptyLabel, addItemButton);
            inventoryCard.getChildren().add(emptyState);
        } else {
            updateInventoryStats(items);
        }
    }

    private void updateInventoryStats(List<Item> items) {
        double totalValue = items.stream()
                .mapToDouble(item -> item.getSellingPrice() * item.getStockQuantity())
                .sum();

        view.getTotalItemsLabel().setText(String.format("Total Items: %d", items.size()));
        view.getTotalValueLabel().setText(String.format("Total Value: $%.2f", totalValue));
        view.getCategoriesLabel().setText(String.format("Categories: %d",
                inventoryController.getAllCategories().size()));
        view.getSuppliersLabel().setText(String.format("Suppliers: %d",
                inventoryController.getAllSuppliers().size()));
    }

    private void updateStockAlerts(List<Item> lowStockItems) {
        VBox alertsBox = view.getAlertsBox();
        alertsBox.getChildren().clear();

        if (lowStockItems.isEmpty()) {
            Label noAlertsLabel = new Label("No stock alerts");
            noAlertsLabel.getStyleClass().add("no-alerts-label");
            alertsBox.getChildren().add(noAlertsLabel);
        } else {
            lowStockItems.forEach(this::createAlertCard);
        }
    }

    private void createAlertCard(Item item) {
        VBox alertCard = new VBox(5);
        alertCard.getStyleClass().add("alert-card");

        Label itemName = new Label(item.getName());
        itemName.getStyleClass().add("alert-title");

        StockLevelIndicator stockIndicator = new StockLevelIndicator();
        stockIndicator.updateStockLevel(
                item.getStockQuantity(),
                item.getCategory().getMinStockLevel()
        );

        Button orderButton = new Button("Order More");
        orderButton.setOnAction(e -> orderMoreStock(item));

        alertCard.getChildren().addAll(itemName, stockIndicator, orderButton);
        view.getAlertsBox().getChildren().add(alertCard);
    }

    private void orderMoreStock(Item item) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Order Stock");
        dialog.setHeaderText("Order more stock for: " + item.getName());

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new javafx.geometry.Insets(20, 150, 10, 10));

        TextField quantityField = new TextField();
        quantityField.setPromptText("Enter quantity to order");

        grid.add(new Label("Current Stock:"), 0, 0);
        grid.add(new Label(String.valueOf(item.getStockQuantity())), 1, 0);
        grid.add(new Label("Minimum Level:"), 0, 1);
        grid.add(new Label(String.valueOf(item.getCategory().getMinStockLevel())), 1, 1);
        grid.add(new Label("Order Quantity:"), 0, 2);
        grid.add(quantityField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        ButtonType orderButtonType = new ButtonType("Order", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(orderButtonType, ButtonType.CANCEL);

        Button orderButton = (Button) dialog.getDialogPane().lookupButton(orderButtonType);
        orderButton.setDisable(true);

        quantityField.textProperty().addListener((obs, oldVal, newVal) -> {
            orderButton.setDisable(newVal.trim().isEmpty() || !newVal.matches("\\d+") ||
                    Integer.parseInt(newVal.trim()) <= 0);
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == orderButtonType) {
                handleOrderSubmission(item, quantityField);
            }
            return null;
        });

        dialog.showAndWait();
    }

    private void handleOrderSubmission(Item item, TextField quantityField) {
        try {
            int quantity = Integer.parseInt(quantityField.getText().trim());
            if (inventoryController.updateItemStock(item.getId(), quantity)) {
                AlertDialog.showInfo("Success", "Stock order placed successfully.");
                refreshDashboard();
            } else {
                AlertDialog.showError("Error", "Failed to place stock order.");
            }
        } catch (NumberFormatException e) {
            AlertDialog.showError("Error", "Invalid quantity entered.");
        }
    }

    private void updateSalesChart() {
        if (view.getStartDate().getValue() != null && view.getEndDate().getValue() != null) {
           
        }
    }

    private void updateReports(LocalDate start, LocalDate end) {
        if (start != null && end != null) {
            if (start.isAfter(end)) {
                AlertDialog.showError("Error", "Start date must be before end date.");
                return;
            }

            updateSalesChart();

            List<String> activities = reportController.getRecentActivities(start, end);
            view.getRecentActivitiesList().getItems().clear();
            view.getRecentActivitiesList().getItems().addAll(activities);

            double periodValue = reportController.calculateInventoryValue(start, end);
            view.getTotalValueLabel().setText(String.format("Period Value: $%.2f", periodValue));
        }
    }

    public ManagerDashboardView getView() {
        return view;
    }
}

*/