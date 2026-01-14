/*package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.BillingController;
import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.model.sales.SaleItem;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.util.List;

public class BillGenerationView extends BorderPane {
    private final App app;
    private final BillingController billingController;
    private final InventoryController inventoryController;

    private ComboBox<Item> itemComboBox;
    private Spinner<Integer> quantitySpinner;
    private TableView<SaleItem> billItemsTable;
    private Text totalAmountText;
    private Bill currentBill;

    public BillGenerationView(App app) {
        this.app = app;
        this.billingController = new BillingController();
        this.inventoryController = new InventoryController();

        getStyleClass().add("bill-generation-view");
        setPadding(new Insets(20));

        initializeComponents();
        startNewBill();
    }

    private void initializeComponents() {
        setTop(createTopSection());
        setCenter(createCenterSection());
        setRight(createRightSection());
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        topSection.getStyleClass().add("bill-header");

        // Title and Bill Number
        Label titleLabel = new Label("Generate New Bill");
        titleLabel.getStyleClass().add("view-title");

        // Item Selection Area
        HBox itemSelectionArea = new HBox(10);
        itemSelectionArea.setAlignment(Pos.CENTER_LEFT);

        // Item dropdown
        itemComboBox = new ComboBox<>();
        itemComboBox.setPromptText("Select Item");
        itemComboBox.setPrefWidth(300);
        itemComboBox.setMaxWidth(300);

        // Quantity spinner
        quantitySpinner = new Spinner<>(1, 100, 1);
        quantitySpinner.setEditable(true);
        quantitySpinner.setPrefWidth(100);

        // Add Item button
        Button addButton = new Button("Add to Bill");
        addButton.getStyleClass().addAll("button", "button-primary");
        addButton.setOnAction(e -> addItemToBill());

        itemSelectionArea.getChildren().addAll(
                new Label("Item:"), itemComboBox,
                new Label("Quantity:"), quantitySpinner,
                addButton
        );

        topSection.getChildren().addAll(titleLabel, itemSelectionArea);
        return topSection;
    }

    private VBox createCenterSection() {
        VBox centerSection = new VBox(15);
        centerSection.getStyleClass().add("bill-items-section");

        // Create table
        billItemsTable = new TableView<>();
        billItemsTable.getStyleClass().add("bill-items-table");

        // Item name column
        TableColumn<SaleItem, String> nameColumn = new TableColumn<>("Item");
        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getItem().getName()
                )
        );

        // Quantity column
        TableColumn<SaleItem, Integer> quantityColumn = new TableColumn<>("Quantity");
        quantityColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleIntegerProperty(
                        data.getValue().getQuantity()
                ).asObject()
        );

        // Price column
        TableColumn<SaleItem, Double> priceColumn = new TableColumn<>("Price");
        priceColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().getPrice()
                ).asObject()
        );

        // Subtotal column
        TableColumn<SaleItem, Double> subtotalColumn = new TableColumn<>("Subtotal");
        subtotalColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleDoubleProperty(
                        data.getValue().calculateSubtotal()
                ).asObject()
        );

        // Action column
        TableColumn<SaleItem, Void> actionColumn = new TableColumn<>("Action");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Remove");
            {
                deleteButton.getStyleClass().addAll("button", "button-danger");
                deleteButton.setOnAction(e -> removeItemFromBill(getIndex()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        billItemsTable.getColumns().addAll(
                nameColumn, quantityColumn, priceColumn, subtotalColumn, actionColumn
        );

        centerSection.getChildren().add(billItemsTable);
        return centerSection;
    }

    private VBox createRightSection() {
        VBox rightSection = new VBox(15);
        rightSection.getStyleClass().add("bill-summary-section");
        rightSection.setPrefWidth(300);
        rightSection.setPadding(new Insets(20));

        // Total Amount
        VBox totalBox = new VBox(5);
        Label totalLabel = new Label("Total Amount");
        totalLabel.getStyleClass().add("total-label");
        totalAmountText = new Text("$0.00");
        totalAmountText.getStyleClass().add("total-amount");
        totalBox.getChildren().addAll(totalLabel, totalAmountText);

        // Action Buttons
        VBox buttonsBox = new VBox(10);
        Button finalizeButton = new Button("Finalize Bill");
        finalizeButton.getStyleClass().addAll("button", "button-success");
        finalizeButton.setOnAction(e -> finalizeBill());

        Button cancelButton = new Button("Cancel");
        cancelButton.getStyleClass().addAll("button", "button-danger");
        cancelButton.setOnAction(e -> cancelBill());

        buttonsBox.getChildren().addAll(finalizeButton, cancelButton);

        rightSection.getChildren().addAll(totalBox, new Separator(), buttonsBox);
        return rightSection;
    }

    private void startNewBill() {
        currentBill = billingController.createNewBill();
        billItemsTable.getItems().clear();
        updateTotalAmount();
        loadAvailableItems();
    }

    private void loadAvailableItems() {
        // Load items from inventory
        List<Item> availableItems = inventoryController.getAvailableItems();
        itemComboBox.getItems().setAll(availableItems);
    }

    private void addItemToBill() {
        Item selectedItem = itemComboBox.getValue();
        if (selectedItem == null) {
            AlertDialog.showWarning("Warning", "Please select an item.");
            return;
        }

        int quantity = quantitySpinner.getValue();
        if (billingController.addItemToBill(selectedItem, quantity)) {
            refreshBillItems();
            itemComboBox.setValue(null);
            quantitySpinner.getValueFactory().setValue(1);
        } else {
            AlertDialog.showError("Error", "Failed to add item to bill. Please check stock availability.");
        }
    }

    private void removeItemFromBill(int index) {
        if (index >= 0 && index < billItemsTable.getItems().size()) {
            SaleItem item = billItemsTable.getItems().get(index);
            if (billingController.removeItemFromBill(item)) {
                refreshBillItems();
            }
        }
    }

    private void refreshBillItems() {
        billItemsTable.getItems().setAll(currentBill.getItems());
        updateTotalAmount();
    }

    private void updateTotalAmount() {
        totalAmountText.setText(String.format("$%.2f", currentBill.getTotalAmount()));
    }

    private void finalizeBill() {
        if (billItemsTable.getItems().isEmpty()) {
            AlertDialog.showWarning("Warning", "Cannot finalize empty bill.");
            return;
        }

        if (AlertDialog.showConfirmation("Confirm", "Are you sure you want to finalize this bill?")) {
            if (billingController.finalizeBill()) {
                AlertDialog.showInfo("Success", "Bill has been finalized successfully.");
                app.showCashierDashboard();
            } else {
                AlertDialog.showError("Error", "Failed to finalize bill.");
            }
        }
    }

    private void cancelBill() {
        if (!billItemsTable.getItems().isEmpty()) {
            if (!AlertDialog.showConfirmation("Confirm", "Are you sure you want to cancel this bill?")) {
                return;
            }
        }
        app.showCashierDashboard();
    }
}
*/