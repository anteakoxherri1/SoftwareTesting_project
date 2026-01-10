package com.electronicstore.view.screens;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.view.components.AlertDialog;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;

public class SupplierManagementDialog extends DialogPane {
    private final TableView<Supplier> supplierTable;
    private final InventoryController inventoryController;

    public SupplierManagementDialog(InventoryController controller) {
        this.inventoryController = controller;

        setHeaderText("Manage Suppliers");

        this.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        this.getStyleClass().add("dialog-pane");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));

        // Add Supplier Form
        GridPane addForm = new GridPane();
        addForm.setHgap(10);
        addForm.setVgap(10);
        addForm.setPadding(new Insets(0, 0, 20, 0));

        TextField nameField = new TextField();
        TextField contactField = new TextField();
        Button addButton = new Button("Add Supplier");

        addForm.add(new Label("Name:"), 0, 0);
        addForm.add(nameField, 1, 0);
        addForm.add(new Label("Contact:"), 0, 1);
        addForm.add(contactField, 1, 1);
        addForm.add(addButton, 1, 2);

        // Suppliers Table
        supplierTable = new TableView<>();
        supplierTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Supplier, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        TableColumn<Supplier, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Supplier, String> contactCol = new TableColumn<>("Contact");
        contactCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getContact()));

        TableColumn<Supplier, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final HBox actions = new HBox(5);
            private final Button editButton = new Button("Edit");
            private final Button deleteButton = new Button("Delete");

            {
                editButton.getStyleClass().addAll("button", "button-secondary");
                deleteButton.getStyleClass().addAll("button", "button-danger");

                // Removed unused supplier parameter by selecting from table inside the handler
                editButton.setOnAction(e -> handleEditSupplier());
                deleteButton.setOnAction(e -> handleDeleteSupplier());

                actions.getChildren().addAll(editButton, deleteButton);
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : actions);
            }
        });

        supplierTable.getColumns().addAll(idCol, nameCol, contactCol, actionsCol);

        addButton.setOnAction(e -> {
            String name = nameField.getText().trim();
            String contact = contactField.getText().trim();

            if (name.isEmpty() || contact.isEmpty()) {
                AlertDialog.showError("Validation Error", "All fields are required");
                return;
            }

            if (inventoryController.addSupplier(name, contact)) {
                refreshTable();
                nameField.clear();
                contactField.clear();
                AlertDialog.showInfo("Success", "Supplier added successfully");
            } else {
                AlertDialog.showError("Error", "Failed to add supplier");
            }
        });

        content.getChildren().addAll(
                new Label("Add New Supplier:"),
                addForm,
                new Separator(),
                new Label("Existing Suppliers:"),
                supplierTable
        );

        setContent(content);
        getButtonTypes().add(ButtonType.CLOSE);

        refreshTable();
    }

    private void handleEditSupplier() {
        Supplier supplier = supplierTable.getSelectionModel().getSelectedItem();
        if (supplier == null) {
            AlertDialog.showError("No Selection", "Please select a supplier to edit.");
            return;
        }
        // Implement edit functionality
    }

    private void handleDeleteSupplier() {
        Supplier supplier = supplierTable.getSelectionModel().getSelectedItem();
        if (supplier == null) {
            AlertDialog.showError("No Selection", "Please select a supplier to delete.");
            return;
        }

        if (AlertDialog.showConfirmation("Confirm Delete",
                "Are you sure you want to delete this supplier?")) {
            // Add delete functionality in controller
            refreshTable();
        }
    }

    private void refreshTable() {
        supplierTable.setItems(FXCollections.observableArrayList(
                inventoryController.getAllSuppliers()
        ));
    }
}
