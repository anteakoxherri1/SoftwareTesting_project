package com.electronicstore.view.screens;

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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class InventoryManagementController implements Initializable {
    private TextField searchField;
    private ComboBox<Category> categoryFilter;
    private ComboBox<Supplier> supplierFilter;
    private TableView<Item> itemsTable;
    private TableColumn<Item, String> idColumn;
    private TableColumn<Item, String> nameColumn;
    private TableColumn<Item, String> categoryColumn;
    private TableColumn<Item, String> supplierColumn;
    private TableColumn<Item, StockLevelIndicator> stockColumn;
    private TableColumn<Item, Double> purchasePriceColumn;
    private TableColumn<Item, Double> sellingPriceColumn;
    private TableColumn<Item, Void> actionColumn;
    private Label totalItemsLabel;
    private Label lowStockLabel;
    private Label categoriesLabel;
    private Label suppliersLabel;

    private App app;
    private InventoryController inventoryController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        inventoryController = new InventoryController();
        setupTableColumns();
        setupSearch();
        loadInventoryData();
    }

    public void setApp(App app) {
        this.app = app;
    }

    private void setupTableColumns() {
        idColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getId()));

        nameColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getName()));

        categoryColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getCategory().getName()));

        supplierColumn.setCellValueFactory(data ->
                new SimpleStringProperty(data.getValue().getSupplier().getName()));

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

        purchasePriceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getPurchasePrice()).asObject());

        sellingPriceColumn.setCellValueFactory(data ->
                new SimpleDoubleProperty(data.getValue().getSellingPrice()).asObject());

        setupActionColumn();
    }

    private void setupActionColumn() {
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
    }

    private void setupSearch() {
        // Variable to hold the search timer
        final java.util.Timer[] searchTimer = {null};

        // Setup search field with debouncing
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
            }, 300); // 300ms delay
        });

        // Setup category filter
        categoryFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterItems());

        // Setup supplier filter
        supplierFilter.valueProperty().addListener((obs, oldVal, newVal) -> filterItems());
    }

    private void loadInventoryData() {
        categoryFilter.setItems(FXCollections.observableArrayList(
                inventoryController.getAllCategories()));
        supplierFilter.setItems(FXCollections.observableArrayList(
                inventoryController.getAllSuppliers()));
        refreshItemsTable();
    }

    @FXML
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
                    // Search text filter
                    boolean matchesSearch = searchText.isEmpty() ||
                            item.getName().toLowerCase().contains(searchText) ||
                            item.getId().toLowerCase().contains(searchText) ||
                            item.getCategory().getName().toLowerCase().contains(searchText) ||
                            item.getSupplier().getName().toLowerCase().contains(searchText);

                    // Category filter
                    boolean matchesCategory = selectedCategory == null ||
                            item.getCategory().getId().equals(selectedCategory.getId());

                    // Supplier filter
                    boolean matchesSupplier = selectedSupplier == null ||
                            item.getSupplier().getId().equals(selectedSupplier.getId());

                    return matchesSearch && matchesCategory && matchesSupplier;
                })
                .toList();

        // Update table and summary
        itemsTable.setItems(FXCollections.observableArrayList(filteredItems));
        updateFilteredSummary(filteredItems);
    }

    private void updateFilteredSummary(List<Item> filteredItems) {
        List<Item> lowStockItems = filteredItems.stream()
                .filter(item -> item.getStockQuantity() <= item.getCategory().getMinStockLevel())
                .toList();

        // Update summary labels for filtered view
        totalItemsLabel.setText(String.valueOf(filteredItems.size()));
        lowStockLabel.setText(String.valueOf(lowStockItems.size()));

        // Categories and Suppliers counts remain unchanged as they show total counts
        List<Category> categories = inventoryController.getAllCategories();
        List<Supplier> suppliers = inventoryController.getAllSuppliers();
        categoriesLabel.setText(String.valueOf(categories.size()));
        suppliersLabel.setText(String.valueOf(suppliers.size()));
    }

    @FXML
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

    @FXML
    private void showCategoryManagementDialog() {
        Dialog<Category> dialog = new Dialog<>();
        dialog.setTitle("Manage Categories");
        dialog.setDialogPane(new CategoryManagementDialog(inventoryController));
        dialog.showAndWait();
        refreshItemsTable();
    }

    @FXML
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

    public void openManagerDashboard(ActionEvent actionEvent) {
        app.showManagerDashboard();
    }
}