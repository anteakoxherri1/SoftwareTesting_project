package com.electronicstore;

import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.SessionState;

import com.electronicstore.view.screens.*;
import com.electronicstore.view.components.CustomMenuBar;
import javafx.application.Application;
import javafx.application.Platform;

import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;



public class App extends Application {
    private Stage primaryStage;
    private BorderPane mainLayout;
    private CustomMenuBar menuBar;
    private SessionState sessionState;


    @Override
    public void start(Stage primaryStage) {
        try {
            Platform.setImplicitExit(true);
            primaryStage.setOnCloseRequest(event -> {
                Platform.exit();
                System.exit(0);
            });

            this.primaryStage = primaryStage;
            this.sessionState = SessionState.getInstance();
            initializeStage();
        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(1);
        }
    }

    private void initializeStage() {
        try {
            primaryStage.setTitle("Electronic Store Management System");
            mainLayout = new BorderPane();

            Scene scene = new Scene(mainLayout);
            scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());

            primaryStage.setScene(scene);

            Platform.runLater(() -> {
                primaryStage.centerOnScreen();
                primaryStage.setMaximized(true);

                showLoginScreen();
                primaryStage.show();
            });

        } catch (Exception e) {
            e.printStackTrace();
            Platform.exit();
            System.exit(1);
        }
    }

    public void showLoginScreen() {
        LoginView loginView = new LoginView(this);
        mainLayout.setCenter(loginView);
        menuBar = null;
        mainLayout.setTop(null);
    }

    public void showRegisterScreen() {
        RegisterView registerView = new RegisterView(this);
        mainLayout.setCenter(registerView);
    }

    public void initializeUserInterface() {
        // Create menu bar
        menuBar = new CustomMenuBar(primaryStage, this);
        mainLayout.setTop(menuBar);

        // Show appropriate dashboard based on user role
        if (sessionState.isCashier()) {
            showCashierDashboard();
        } else if (sessionState.isManager()) {
            showManagerDashboard();
        } else if (sessionState.isAdministrator()) {
            showAdminDashboard();
        }
    }

    public void showCashierDashboard() {
        CashierDashboard dashboard = new CashierDashboard(this);
        mainLayout.setCenter(dashboard);
    }

    public void showManagerDashboard() {
        ManagerDashboard managerDashboard = new ManagerDashboard(this);
        mainLayout.setCenter(managerDashboard.getView());
    }

    public void showAdminDashboard() {
        AdminDashboard dashboard = new AdminDashboard(this);
        mainLayout.setCenter(dashboard);
    }

    public void showBillGeneration() {
        BillGenerationView billView = new BillGenerationView(this);
        mainLayout.setCenter(billView);
    }

    public void showInventoryManagement() {
        InventoryManagementView inventoryView = new InventoryManagementView(this);
        mainLayout.setCenter(inventoryView);
    }

    public void showUserManagement() {
        UserManagementView userView = new UserManagementView(this);
        mainLayout.setCenter(userView);
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public CustomMenuBar getMenuBar() {
        return menuBar;
    }

    public void logout() {
        sessionState.endSession();
        showLoginScreen();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public User getCurrentUser() {
        return sessionState.getCurrentUser();
    }
}