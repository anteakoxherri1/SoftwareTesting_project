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

    private static final String USER_TYPE_ADMIN = "Administrator";
    private static final String USER_TYPE_CASHIER = "Cashier";
    private static final String USER_TYPE_MANAGER = "Manager";

    private static final String STYLE_INPUT_FIELD = "input-field";
    private static final String TITLE_VALIDATION_ERROR = "Validation Error";

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
        VBox formBox = new VBox(15);
        formBox.setAlignment(Pos.CENTER);
        formBox.setMaxWidth(400);
        formBox.setPadding(new javafx.geometry.Insets(20));

        Rectangle logoPlaceholder = new Rectangle(80, 80);
        logoPlaceholder.setFill(Color.LIGHTGRAY);
        logoPlaceholder.setArcWidth(10);
        logoPlaceholder.setArcHeight(10);

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

        VBox formContainer = new VBox(10);
        formContainer.setMaxWidth(400);
        formContainer.getStyleClass().add("form");

        usernameField = createFormField("Username", "Enter username");
        passwordField = createPasswordField("Password", "Enter password");
        confirmPasswordField = createPasswordField("Confirm Password", "Confirm your password");

        nameField = createFormField("Full Name", "Enter your full name");
        emailField = createFormField("Email", "Enter your email");
        phoneField = createFormField("Phone", "Enter your phone number");

        Label userTypeLabel = new Label("User Type");
        userTypeCombo = new ComboBox<>();
        userTypeCombo.getItems().addAll(USER_TYPE_ADMIN, USER_TYPE_CASHIER, USER_TYPE_MANAGER);
        userTypeCombo.setMaxWidth(Double.MAX_VALUE);
        userTypeCombo.getStyleClass().add(STYLE_INPUT_FIELD);

        // Sector (Cashier only) - LOCAL variable now (fixes warning)
        VBox sectorContainer = new VBox(5);
        Label sectorLabel = new Label("Sector");
        sectorField = createFormField("", "Enter sector");
        sectorContainer.getChildren().addAll(sectorLabel, sectorField);
        sectorContainer.setVisible(false);

        userTypeCombo.setOnAction(e -> {
            String selected = userTypeCombo.getValue();
            sectorContainer.setVisible(USER_TYPE_CASHIER.equals(selected));
        });

        Button registerButton = new Button("Register");
        registerButton.getStyleClass().addAll("button", "button-primary");
        registerButton.setOnAction(e -> handleRegistration());

        Button backButton = new Button("Back to Login");
        backButton.getStyleClass().addAll("button", "button-secondary");
        backButton.setOnAction(e -> app.showLoginScreen());

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

        getChildren().addAll(
                formBox,
                titleLabel,
                formContainer
        );
    }

    private TextField createFormField(String labelText, String promptText) {
        TextField field = new TextField();
        field.setPromptText(promptText);
        field.getStyleClass().add(STYLE_INPUT_FIELD);
        return field;
    }

    private PasswordField createPasswordField(String labelText, String promptText) {
        PasswordField field = new PasswordField();
        field.setPromptText(promptText);
        field.getStyleClass().add(STYLE_INPUT_FIELD);
        return field;
    }

    private void handleRegistration() {
        if (!validateFields()) {
            return;
        }

        String username = usernameField.getText();
        String password = passwordField.getText();
        String name = nameField.getText();
        String email = emailField.getText();
        String phone = phoneField.getText();
        String userType = userTypeCombo.getValue();
        String sector = sectorField.getText();

        boolean success = userController.addUser(
                username,
                password,
                name,
                email,
                phone,
                userType.toLowerCase(),
                USER_TYPE_CASHIER.equals(userType) ? sector : null
        );

        if (success) {
            AlertDialog.showInfo("Success", "Registration successful! Please login.");
            app.showLoginScreen();
        } else {
            AlertDialog.showError("Error", "Registration failed. Please try again.");
        }
    }

    private boolean validateFields() {
        if (usernameField.getText().isEmpty() ||
                passwordField.getText().isEmpty() ||
                confirmPasswordField.getText().isEmpty() ||
                nameField.getText().isEmpty() ||
                emailField.getText().isEmpty() ||
                phoneField.getText().isEmpty() ||
                userTypeCombo.getValue() == null) {

            AlertDialog.showError(TITLE_VALIDATION_ERROR, "All fields are required.");
            return false;
        }

        if (!passwordField.getText().equals(confirmPasswordField.getText())) {
            AlertDialog.showError(TITLE_VALIDATION_ERROR, "Passwords do not match.");
            return false;
        }

        if (USER_TYPE_CASHIER.equals(userTypeCombo.getValue()) && sectorField.getText().isEmpty()) {
            AlertDialog.showError(TITLE_VALIDATION_ERROR, "Sector is required for Cashier.");
            return false;
        }

        if (!emailField.getText().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            AlertDialog.showError(TITLE_VALIDATION_ERROR, "Invalid email format.");
            return false;
        }

        if (!phoneField.getText().matches("\\d{10}")) {
            AlertDialog.showError(TITLE_VALIDATION_ERROR, "Phone number must be 10 digits.");
            return false;
        }

        return true;
    }
}
