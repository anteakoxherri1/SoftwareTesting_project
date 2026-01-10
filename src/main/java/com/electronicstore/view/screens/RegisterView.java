package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public class RegisterView extends VBox {
    private final App app;
    private final UserManagementController userController;

    private TextField usernameField;
    private PasswordField passwordField;
    private PasswordField confirmPasswordField;
    private TextField nameField;
    private TextField emailField;
    private TextField phoneField;
    private ComboBox<String> userTypeCombo;
    private TextField sectorField;
    private VBox sectorContainer;

    public RegisterView(App app) {
        this.app = app;
        this.userController = new UserManagementController();

        setAlignment(Pos.CENTER);
        setSpacing(15);
        setPadding(new javafx.geometry.Insets(20));
        getStyleClass().add("register-container");

        initializeComponents();
    }

    private void initializeComponents() {
        // Logo and Title
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setPadding(new javafx.geometry.Insets(20));

        Rectangle logoPlaceholder = new Rectangle(80, 80);
        logoPlaceholder.setFill(Color.LIGHTGRAY);
        logoPlaceholder.setArcWidth(10);
        logoPlaceholder.setArcHeight(10);

        // Try to load the logo image
        try {
            InputStream imageStream = getClass().getResourceAsStream("/images/logo.png");
            if (imageStream != null) {
                Image logo = new Image(imageStream);
                ImageView logoView = new ImageView(logo);
                logoView.setFitHeight(80);
                logoView.setFitWidth(80);
                formBox.getChildren().add(logoView);
            } else {
                formBox.getChildren().add(logoPlaceholder);
            }
        } catch (Exception e) {
            System.err.println("Logo loading failed: " + e.getMessage());
            formBox.getChildren().add(logoPlaceholder);
        }

        Label titleLabel = new Label("Create New Account");
        titleLabel.getStyleClass().add("login-title");

        // Create form container
        VBox formContainer = new VBox(10);
        formContainer.setMaxWidth(400);
        formContainer.getStyleClass().add("form");

        // Username field
        usernameField = createFormField("Username", "Enter username");

        // Password fields
        passwordField = createPasswordField("Password", "Enter password");
        confirmPasswordField = createPasswordField("Confirm Password", "Confirm your password");

        // Personal information fields
        nameField = createFormField("Full Name", "Enter your full name");
        emailField = createFormField("Email", "Enter your email");
        phoneField = createFormField("Phone", "Enter your phone number");

        // User type selection
        Label userTypeLabel = new Label("User Type");
        userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll("Administrator", "Cashier", "Manager");
        userTypeCombo.setMaxWidth(Double.MAX_VALUE);
        userTypeCombo.getStyleClass().add("input-field");

        // Sector field (visible only for Cashier)
        sectorContainer = new VBox(5);
        Label sectorLabel = new Label("Sector");
        sectorField = createFormField("", "Enter sector");
        sectorContainer.getChildren().addAll(sectorLabel, sectorField);
        sectorContainer.setVisible(false);

        // Show/hide sector field based on user type selection
        userTypeCombo.setOnAction(e -> {
            sectorContainer.setVisible(userTypeCombo.getValue().equals("Cashier"));
        });

        // Buttons
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().addAll("button", "button-primary");
        registerButton.setOnAction(e -> handleRegistration());

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().addAll("button", "button-secondary");
        backButton.setOnAction(e -> app.showLoginScreen());

        // Add all components to form
        formContainer.getChildren().addAll(
                usernameField,
                passwordField,
                confirmPasswordField,
                nameField,
                emailField,
                phoneField,
                userTypeLabel,
                userTypeCombo,
                sectorContainer,
                new Separator(),
                registerButton,
                backButton
        );

        // Add everything to main container
        getChildren().addAll(
                formBox,
                titleLabel,
                formContainer
        );
    }

    private TextField createFormField(String labelText, String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.getStyleClass().add("input-field");
        return field;
    }

    private PasswordField createPasswordField(String labelText, String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.getStyleClass().add("input-field");
        return field;
    }

    private void handleRegistration() {
        // Validate fields
        if (!validateFields()) {
            return;
        }

        // Get all field values
        String username = usernameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String userType = userTypeCombo.getValue();
        String sector = sectorField.getText();

        // Attempt registration
        boolean success = userController.addUser(
                username,
                password,
                name,
                email,
                phone,
                userType.toLowerCase(),
                userType.equals("Cashier") ? sector : null
        );

        if (success) {
            AlertDialog.showInfo("Success", "Registration successful! Please login.");
            app.showLoginScreen();
        } else {
            AlertDialog.showError("Error", "Registration failed. Please try again.");
        }
    }

    private boolean validateFields() {
        // Validate required fields
        if (usernameField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() ||
                nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                phoneField.getText().isEmpty() ||
                userTypeCombo.getValue() == null) {

            AlertDialog.showError("Validation Error", "All fields are required.");
            return false;
        }

        // Validate password match
        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertDialog.showError("Validation Error", "Passwords do not match.");
            return false;
        }

        // Validate sector for Cashier
        if (userTypeCombo.getValue().equals("Cashier") && sectorField.getText().isEmpty()) {
            AlertDialog.showError("Validation Error", "Sector is required for Cashier.");
            return false;
        }

        // Validate email format
        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertDialog.showError("Validation Error", "Invalid email format.");
            return false;
        }

        // Validate phone number (simple format check)
        if (!phoneField.getText().matches("\\d{10}")) {
            AlertDialog.showError("Validation Error", "Phone number must be 10 digits.");
            return false;
        }

        return true;
    }
}