/*package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.view.components.AlertDialog;
import com.electronicstore.view.components.StockLevelIndicator;
import javafx.application.Platform;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import java.util.List;

public class InventoryManagementView extends BorderPane {
    private TextField searchField;
    private ComboBox<Category> categoryFilter;
    private ComboBox<Supplier> supplierFilter;
    private TableView<Item> itemsTable;
    private Label totalItemsLabel;
    private Label lowStockLabel;
    private Label categoriesLabel;
    private Label suppliersLabel;

    private App app;
    private InventoryController inventoryController;

    public InventoryManagementView(App app) {
        this.app = app;
        this.inventoryController = new InventoryController();

        getStyleClass().add("inventory-view");
        setPadding(new Insets(20, 20, 10, 20));

        setupTop();
        setupCenter();
        setupRight();
        setupSearch();
        loadInventoryData();
    }

    private void setupTop() {
        VBox headerBox = new VBox(15);
        headerBox.getStyleClass().add("inventory-header");
        headerBox.setPadding(new Insets(20, 20, 10, 20));

        Label titleLabel = new Label("Inventory Management");
        titleLabel.getStyleClass().add("view-title");

        // Search and filter controls
        HBox filterBox = new HBox(10);
        filterBox.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search items...");
        searchField.setPrefWidth(250);

        categoryFilter = new ComboBox<>();
        categoryFilter.setPromptText("Filter by Category");
        categoryFilter.setPrefWidth(200);

        supplierFilter = new ComboBox<>();
        supplierFilter.setPromptText("Filter by Supplier");
        supplierFilter.setPrefWidth(200);

        Button clearFiltersBtn = new Button("Clear Filters");
        clearFiltersBtn.setOnAction(e -> clearFilters());

        filterBox.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Category:"), categoryFilter,
                new Label("Supplier:"), supplierFilter,
                clearFiltersBtn
        );

        // Action buttons
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER_LEFT);

        Button addItemBtn = new Button("Add New Item");
        addItemBtn.getStyleClass().addAll("button", "button-primary");
        addItemBtn.setOnAction(e -> showAddItemDialog());

        Button manageCategoriesBtn = new Button("Manage Categories");
        manageCategoriesBtn.getStyleClass().addAll("button", "button-secondary");
        manageCategoriesBtn.setOnAction(e -> showCategoryManagementDialog());

        Button manageSuppliersBtn = new Button("Manage Suppliers");
        manageSuppliersBtn.getStyleClass().addAll("button", "button-secondary");
        manageSuppliersBtn.setOnAction(e -> showSupplierManagementDialog());

        Button dashboardBtn = new Button("Back to Dashboard");
        dashboardBtn.getStyleClass().addAll("button", "button-info");
        dashboardBtn.setOnAction(e -> app.showManagerDashboard());

        buttonBox.getChildren().addAll(
                addItemBtn, manageCategoriesBtn, manageSuppliersBtn, dashboardBtn
        );

        headerBox.getChildren().addAll(titleLabel, filterBox, buttonBox);
        setTop(headerBox);
    }

    private void setupCenter() {
        VBox contentBox = new VBox(15);
        contentBox.getStyleClass().add("inventory-content");
        contentBox.setPadding(new Insets(20, 20, 10, 20));

        itemsTable = new TableView<>();
        itemsTable.getStyleClass().add("inventory-table");
        VBox.setVgrow(itemsTable, Priority.ALWAYS);

        // Configure table columns
        TableColumn<Item, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        TableColumn<Item, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Item, String> categoryColumn = new TableColumn<>("Category");
        categoryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory().getName()));

        TableColumn<Item, String> supplierColumn = new TableColumn<>("Supplier");
        supplierColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSupplier().getName()));

        TableColumn<Item, StockLevelIndicator> stockColumn = new TableColumn<>("Stock Level");
        stockColumn.setCellFactory(col -> new TableCell<>() {
            private final StockLevelIndicator indicator = new StockLevelIndicator();

            @Override
            protected void updateItem(StockLevelIndicator item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Item currentItem = getTableView().getItems().get(getIndex());
                    indicator.updateStockLevel(
                            currentItem.getStockQuantity(),
                            currentItem.getCategory().getMinStockLevel()
                    );
                    setGraphic(indicator);
                }
            }
        });

        TableColumn<Item, Double> purchasePriceColumn = new TableColumn<>("Purchase Price");
        purchasePriceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPurchasePrice()).asObject());

        TableColumn<Item, Double> sellingPriceColumn = new TableColumn<>("Selling Price");
        sellingPriceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSellingPrice()).asObject());

        TableColumn<Item, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");
            private final HBox actions = new HBox(5, editButton, deleteButton);

            {
                editButton.getStyleClass().addAll("button", "button-small");
                deleteButton.getStyleClass().addAll("button", "button-small", "button-danger");

                editButton.setOnAction(e -> editItem(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(e -> deleteItem(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });

        itemsTable.getColumns().addAll(
                idColumn, nameColumn, categoryColumn, supplierColumn,
                stockColumn, purchasePriceColumn, sellingPriceColumn, actionColumn
        );

        itemsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        contentBox.getChildren().add(itemsTable);
        setCenter(contentBox);
    }

    private void setupRight() {
        VBox summaryBox = new VBox(15);
        summaryBox.getStyleClass().add("inventory-summary");
        summaryBox.setPrefWidth(250);
        summaryBox.setPadding(new Insets(0, 0, 0, 20));

        Label summaryTitle = new Label("Inventory Summary");
        summaryTitle.getStyleClass().add("section-title");

        Separator separator = new Separator();

        VBox statsBox = new VBox(10);

        // Total Items
        HBox totalItemsBox = new HBox(10);
        totalItemsBox.setAlignment(Pos.CENTER_LEFT);
        totalItemsLabel = new Label("0");
        totalItemsLabel.getStyleClass().add("summary-value");
        totalItemsBox.getChildren().addAll(new Label("Total Items"), totalItemsLabel);

        // Low Stock Items
        HBox lowStockBox = new HBox(10);
        lowStockBox.setAlignment(Pos.CENTER_LEFT);
        lowStockLabel = new Label("0");
        lowStockLabel.getStyleClass().add("summary-value");
        lowStockBox.getChildren().addAll(new Label("Low Stock Items"), lowStockLabel);

        // Categories
        HBox categoriesBox = new HBox(10);
        categoriesBox.setAlignment(Pos.CENTER_LEFT);
        categoriesLabel = new Label("0");
        categoriesLabel.getStyleClass().add("summary-value");
        categoriesBox.getChildren().addAll(new Label("Categories"), categoriesLabel);

        // Suppliers
        HBox suppliersBox = new HBox(10);
        suppliersBox.setAlignment(Pos.CENTER_LEFT);
        suppliersLabel = new Label("0");
        suppliersLabel.getStyleClass().add("summary-value");
        suppliersBox.getChildren().addAll(new Label("Suppliers"), suppliersLabel);

        statsBox.getChildren().addAll(
                totalItemsBox, lowStockBox, categoriesBox, suppliersBox
        );

        summaryBox.getChildren().addAll(summaryTitle, separator, statsBox);
        setRight(summaryBox);
    }

    private void setupSearch() {
        final java.util.Timer[] searchTimer = {null};

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            if (searchTimer[0] != null) {
                searchTimer[0].cancel();
            }
            searchTimer[0] = new java.util.Timer();
            searchTimer[0].schedule(new java.util.TimerTask() {
                @Override
                public void run() {
                    Platform.runLater(() -> filterItems());
                }
            }, 300);
        });

        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterItems());
        supplierFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterItems());
    }

    private void loadInventoryData() {
        categoryFilter.setItems(FXCollections.observableArrayList(
                inventoryController.getAllCategories()));
        supplierFilter.setItems(FXCollections.observableArrayList(
                inventoryController.getAllSuppliers()));
        refreshItemsTable();
    }

    private void clearFilters() {
        searchField.clear();
        categoryFilter.setValue(null);
        supplierFilter.setValue(null);
        refreshItemsTable();
    }

    private void filterItems() {
        String searchText = searchField.getText().toLowerCase().trim();
        Category selectedCategory = categoryFilter.getValue();
        Supplier selectedSupplier = supplierFilter.getValue();

        List<Item> allItems = inventoryController.getAllItems();
        List<Item> filteredItems = allItems.stream()
                .filter(item -> {
                    boolean matchesSearch = searchText.isEmpty() ||
                            item.getName().toLowerCase().contains(searchText) ||
                            item.getId().toLowerCase().contains(searchText) ||
                            item.getCategory().getName().toLowerCase().contains(searchText) ||
                            item.getSupplier().getName().toLowerCase().contains(searchText);

                    boolean matchesCategory = selectedCategory == null ||
                            item.getCategory().getId().equals(selectedCategory.getId());

                    boolean matchesSupplier = selectedSupplier == null ||
                            item.getSupplier().getId().equals(selectedSupplier.getId());

                    return matchesSearch && matchesCategory && matchesSupplier;
                })
                .toList();

        itemsTable.setItems(FXCollections.observableArrayList(filteredItems));
        updateFilteredSummary(filteredItems);
    }

    private void updateFilteredSummary(List<Item> filteredItems) {
        List<Item> lowStockItems = filteredItems.stream()
                .filter(item -> item.getStockQuantity() <= item.getCategory().getMinStockLevel())
                .toList();

        totalItemsLabel.setText(String.valueOf(filteredItems.size()));
        lowStockLabel.setText(String.valueOf(lowStockItems.size()));

        List<Category> categories = inventoryController.getAllCategories();
        List<Supplier> suppliers = inventoryController.getAllSuppliers();
        categoriesLabel.setText(String.valueOf(categories.size()));
        suppliersLabel.setText(String.valueOf(suppliers.size()));
    }

    private void showAddItemDialog() {
        if (inventoryController.getAllCategories().isEmpty()) {
            AlertDialog.showError("Error", "Please add at least one category before adding items.");
            return;
        }

        if (inventoryController.getAllSuppliers().isEmpty()) {
            AlertDialog.showError("Error", "Please add at least one supplier before adding items.");
            return;
        }

        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Add New Item");
        dialog.setDialogPane(new AddItemDialog(inventoryController));
        dialog.showAndWait();
        refreshItemsTable();
    }

    private void showCategoryManagementDialog() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Manage Categories");
        dialog.setDialogPane(new CategoryManagementDialog(inventoryController));
        dialog.showAndWait();
        refreshItemsTable();
    }

    private void showSupplierManagementDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Supplier Management");
        dialog.setDialogPane(new SupplierManagementDialog(inventoryController));
        dialog.showAndWait();
        refreshItemsTable();
    }

    private void editItem(Item item) {
        Dialog<Item> dialog = new Dialog<>();
        dialog.setTitle("Edit Item");
        dialog.setDialogPane(new EditItemDialog(inventoryController, item));
        dialog.showAndWait();
        refreshItemsTable();
    }

    private void deleteItem(Item item) {
        if (AlertDialog.showConfirmation("Confirm Delete",
                "Are you sure you want to delete this item?")) {
            if (inventoryController.deleteItem(item.getId())) {
                refreshItemsTable();
                AlertDialog.showInfo("Success", "Item deleted successfully.");
            } else {
                AlertDialog.showError("Error", "Failed to delete item.");
            }
        }
    }

    private void refreshItemsTable() {
        itemsTable.setItems(FXCollections.observableArrayList(
                inventoryController.getAllItems()));
        updateSummary();
    }

    private void updateSummary() {
        List<Item> items = inventoryController.getAllItems();
        List<Item> lowStockItems = inventoryController.checkLowStock();
        List<Category> categories = inventoryController.getAllCategories();
        List<Supplier> suppliers = inventoryController.getAllSuppliers();

        totalItemsLabel.setText(String.valueOf(items.size()));
        lowStockLabel.setText(String.valueOf(lowStockItems.size()));
        categoriesLabel.setText(String.valueOf(categories.size()));
        suppliersLabel.setText(String.valueOf(suppliers.size()));
    }
}
*/