package com.electronicstore.view.screens;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.util.StringConverter;

public class AddItemDialog extends DialogPane {
    private final InventoryController inventoryController;
    private final TextField nameField;
    private final ComboBox<Category> categoryCombo;
    private final ComboBox<Supplier> supplierCombo;
    private final TextField purchasePriceField;
    private final TextField sellingPriceField;
    private final TextField quantityField;
    private Button addButton;

    public AddItemDialog(InventoryController inventoryController) {
        this.inventoryController = inventoryController;

        this.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        this.getStyleClass().add("dialog-pane");

        // Initialize form fields
        nameField = createTextField("Enter item name");
        categoryCombo = createCategoryComboBox();
        supplierCombo = createSupplierComboBox();
        purchasePriceField = createTextField("Enter purchase price");
        sellingPriceField = createTextField("Enter selling price");
        quantityField = createTextField("Enter quantity");

        GridPane grid = createFormGrid();

        setupDialog(grid);

        setupValidation();
    }

    private TextField createTextField(String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.getStyleClass().add("form-field");
        return field;
    }

    private ComboBox<Category> createCategoryComboBox() {
        ComboBox<Category> combo = new ComboBox<>();
        combo.getItems().addAll(inventoryController.getAllCategories());
        combo.setConverter(new StringConverter<Category>() {
            @Override
            public String toString(Category category) {
                return category != null ? category.getName() : "";
            }

            @Override
            public Category fromString(String string) {
                return null;
            }
        });
        combo.getStyleClass().add("form-field");
        combo.setPromptText("Select category");
        return combo;
    }

    private ComboBox<Supplier> createSupplierComboBox() {
        ComboBox<Supplier> combo = new ComboBox<>();
        combo.getItems().addAll(inventoryController.getAllSuppliers());
        combo.setConverter(new StringConverter<Supplier>() {
            @Override
            public String toString(Supplier supplier) {
                return supplier != null ? supplier.getName() : "";
            }

            @Override
            public Supplier fromString(String string) {
                return null;
            }
        });
        combo.getStyleClass().add("form-field");
        combo.setPromptText("Select supplier");
        return combo;
    }

    private GridPane createFormGrid() {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        grid.getStyleClass().add("form-grid");

        // Add labels and fields
        grid.add(createLabel("Name:"), 0, 0);
        grid.add(nameField, 1, 0);
        grid.add(createLabel("Category:"), 0, 1);
        grid.add(categoryCombo, 1, 1);
        grid.add(createLabel("Supplier:"), 0, 2);
        grid.add(supplierCombo, 1, 2);
        grid.add(createLabel("Purchase Price:"), 0, 3);
        grid.add(purchasePriceField, 1, 3);
        grid.add(createLabel("Selling Price:"), 0, 4);
        grid.add(sellingPriceField, 1, 4);
        grid.add(createLabel("Quantity:"), 0, 5);
        grid.add(quantityField, 1, 5);

        return grid;
    }

    private Label createLabel(String text) {
        Label label = new Label(text);
        label.getStyleClass().add("form-label");
        return label;
    }

    private void setupDialog(GridPane grid) {
        this.setContent(grid);

        // Add buttons
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButtonType = new ButtonType("Cancel", ButtonBar.ButtonData.CANCEL_CLOSE);
        this.getButtonTypes().addAll(addButtonType, cancelButtonType);

        // Get the Add button and style it after the dialog is shown
        this.sceneProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                addButton = (Button) this.lookupButton(addButtonType);
                if (addButton != null) {
                    addButton.getStyleClass().add("primary-button");
                    validateFields(); // Initial validation
                }
            }
        });

        // Add button click handler
        this.lookupButton(addButtonType).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
            if (!handleAddAction()) {
                event.consume(); // Prevent dialog from closing if validation fails
            }
        });
    }

    private boolean handleAddAction() {
        try {
            // Validate input
            if (!validateInput()) {
                return false;
            }

            // Parse values
            double purchasePrice = Double.parseDouble(purchasePriceField.getText());
            double sellingPrice = Double.parseDouble(sellingPriceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            // Add item
            boolean success = inventoryController.addItem(
                    nameField.getText(),
                    categoryCombo.getValue(),
                    supplierCombo.getValue(),
                    purchasePrice,
                    sellingPrice,
                    quantity
            );

            if (!success) {
                AlertDialog.showError("Error", "Failed to add item. Please try again.");
                return false;
            }

            return true;
        } catch (NumberFormatException e) {
            AlertDialog.showError("Input Error", "Please enter valid numbers for prices and quantity.");
            return false;
        }
    }

    private void setupValidation() {
        // Real-time validation
        nameField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        categoryCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateFields());
        supplierCombo.valueProperty().addListener((obs, oldVal, newVal) -> validateFields());
        purchasePriceField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        sellingPriceField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
        quantityField.textProperty().addListener((obs, oldVal, newVal) -> validateFields());
    }

    private void validateFields() {
        if (addButton != null) {
            boolean valid = validateInput();
            addButton.setDisable(!valid);
        }
    }

    private boolean validateInput() {
        // Check for empty fields
        if (nameField.getText().trim().isEmpty() ||
                categoryCombo.getValue() == null ||
                supplierCombo.getValue() == null ||
                purchasePriceField.getText().trim().isEmpty() ||
                sellingPriceField.getText().trim().isEmpty() ||
                quantityField.getText().trim().isEmpty()) {
            return false;
        }

        // Validate numeric fields
        try {
            double purchasePrice = Double.parseDouble(purchasePriceField.getText());
            double sellingPrice = Double.parseDouble(sellingPriceField.getText());
            int quantity = Integer.parseInt(quantityField.getText());

            // Additional validation rules
            if (purchasePrice < 0 || sellingPrice < 0 || quantity < 0) {
                return false;
            }
            if (sellingPrice <= purchasePrice) {
                return false;
            }
        } catch (NumberFormatException e) {
            return false;
        }

        return true;
    }
}