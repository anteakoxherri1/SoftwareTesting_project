/*package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.LoginController;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.InputStream;

public class LoginView extends VBox {
    private final App app;
    private final LoginController loginController;
    private TextField usernameField;
    private PasswordField passwordField;

    public LoginView(App app) {
        this.app = app;
        this.loginController = new LoginController();

        setAlignment(Pos.CENTER);
        setSpacing(20);
        getStyleClass().add("login-container");

        initializeComponents();
    }

    private void initializeComponents() {
        // Logo
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

        // Title
        Label titleLabel = new Label("Electronic Store Management System");
        titleLabel.getStyleClass().add("login-title");

        // Username field
        VBox usernameBox = new VBox(5);
        Label usernameLabel = new Label("Username");
        usernameField = new TextField();
        usernameField.setPromptText("Enter your username");
        usernameField.getStyleClass().add("input-field");
        usernameBox.getChildren().addAll(usernameLabel, usernameField);

        // Password field
        VBox passwordBox = new VBox(5);
        Label passwordLabel = new Label("Password");
        passwordField = new PasswordField();
        passwordField.setPromptText("Enter your password");
        passwordField.getStyleClass().add("input-field");
        passwordBox.getChildren().addAll(passwordLabel, passwordField);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());

        // Register link
        Hyperlink registerLink = new Hyperlink("Don't have an account? Register");
        registerLink.setOnAction(e -> app.showRegisterScreen());

        // Add all components
        getChildren().addAll(
                formBox,
                titleLabel,
                usernameBox,
                passwordBox,
                loginButton,
                new Separator(),
                registerLink
        );
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            AlertDialog.showError("Login Error", "Please enter both username and password.");
            return;
        }

        if (loginController.login(username, password)) {
            app.initializeUserInterface();
        } else {
            AlertDialog.showError("Login Error", "Invalid username or password.");
            passwordField.clear();
        }
    }
}
*/