package com.electronicstore.view.screens;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.view.components.AlertDialog;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.scene.control.DialogPane;
import javafx.scene.control.TableView;
import javafx.scene.control.TableColumn;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.scene.control.*;

public class CategoryManagementDialog extends DialogPane {
    private final TableView<Category> categoryTable;
    private final InventoryController inventoryController;

    public CategoryManagementDialog(InventoryController controller) {
        this.inventoryController = controller;

        setHeaderText("Manage Categories");

        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        this.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        this.getStyleClass().add("dialog-pane");

        // Add Category Form
        GridPane addForm = new GridPane();
        addForm.setHgap(10);
        addForm.setVgap(10);
        addForm.setPadding(new Insets(0, 0, 20, 0));

        TextField nameField = new TextField();
        TextField minStockField = new TextField();
        TextField sectorField = new TextField();
        Button addButton = new Button("Add Category");

        addForm.add(new Label("Name:"), 0, 0);
        addForm.add(nameField, 1, 0);
        addForm.add(new Label("Min Stock:"), 0, 1);
        addForm.add(minStockField, 1, 1);
        addForm.add(new Label("Sector:"), 0, 2);
        addForm.add(sectorField, 1, 2);
        addForm.add(addButton, 1, 3);

        // Categories Table
        categoryTable = new TableView<>();
        categoryTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        TableColumn<Category, String> idCol = new TableColumn<>("ID");
        idCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));

        TableColumn<Category, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));

        TableColumn<Category, Number> stockCol = new TableColumn<>("Min Stock");
        stockCol.setCellValueFactory(data -> new SimpleIntegerProperty(data.getValue().getMinStockLevel()));

        TableColumn<Category, String> sectorCol = new TableColumn<>("Sector");
        sectorCol.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getSector()));

        TableColumn<Category, Void> actionsCol = new TableColumn<>("Actions");
        actionsCol.setCellFactory(col -> new TableCell<>() {
            private final Button deleteButton = new Button("Delete");
            {
                deleteButton.getStyleClass().addAll("button", "button-danger");
                deleteButton.setOnAction(e -> handleDeleteCategory(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : deleteButton);
            }
        });

        categoryTable.getColumns().addAll(idCol, nameCol, stockCol, sectorCol, actionsCol);

        addButton.setOnAction(e -> {
            try {
                String name = nameField.getText().trim();
                int minStock = Integer.parseInt(minStockField.getText().trim());
                String sector = sectorField.getText().trim();

                if (name.isEmpty() || sector.isEmpty()) {
                    AlertDialog.showError("Validation Error", "All fields are required");
                    return;
                }

                if (inventoryController.addCategory(name, minStock, sector)) {
                    refreshTable();
                    nameField.clear();
                    minStockField.clear();
                    sectorField.clear();
                    AlertDialog.showInfo("Success", "Category added successfully");
                } else {
                    AlertDialog.showError("Error", "Failed to add category");
                }
            } catch (NumberFormatException ex) {
                AlertDialog.showError("Validation Error", "Please enter a valid number for minimum stock");
            }
        });

        content.getChildren().addAll(
                new Label("Add New Category:"),
                addForm,
                new Separator(),
                new Label("Existing Categories:"),
                categoryTable
        );

        setContent(content);
        getButtonTypes().add(ButtonType.CLOSE);

        refreshTable();
    }

    private void handleDeleteCategory(Category category) {
        if (AlertDialog.showConfirmation("Confirm Delete",
                "Are you sure you want to delete this category?")) {
            inventoryController.deleteCategory(category);
            refreshTable();
        }
    }

    private void refreshTable() {
        categoryTable.setItems(FXCollections.observableArrayList(
                inventoryController.getAllCategories()
        ));
    }
}