/*package com.electronicstore.view.components;

import com.electronicstore.App;
import com.electronicstore.model.utils.SessionState;
import javafx.scene.control.*;
import javafx.stage.Stage;

public class CustomMenuBar extends MenuBar {
    private final SessionState sessionState;
    private final Stage primaryStage;
    private App app;

    public CustomMenuBar(Stage primaryStage, App app) {
        this.primaryStage = primaryStage;
        this.sessionState = SessionState.getInstance();
        this.app = app;
        initializeMenus();
    }

    private void initializeMenus() {
        // File Menu
        Menu fileMenu = new Menu("File");
        MenuItem logout = new MenuItem("Logout");
        MenuItem exit = new MenuItem("Exit");

        this.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        this.getStyleClass().add("dialog-pane");

        logout.setOnAction(e -> handleLogout());
        exit.setOnAction(e -> handleExit());

        fileMenu.getItems().addAll(logout, new SeparatorMenuItem(), exit);

        // Operations Menu
        Menu operationsMenu = new Menu("Operations");
        operationsMenu.setVisible(false);

        // Create menus based on user role
        if (sessionState.isCashier()) {
            MenuItem newBill = new MenuItem("New Bill");
            MenuItem viewBills = new MenuItem("View Today's Bills");
            operationsMenu.getItems().addAll(newBill, viewBills);
            operationsMenu.setVisible(true);
        }

        if (sessionState.isManager()) {
            MenuItem inventory = new MenuItem("Inventory Management");
            MenuItem suppliers = new MenuItem("Supplier Management");
            MenuItem reports = new MenuItem("Reports");
            operationsMenu.getItems().addAll(inventory, suppliers, reports);
            operationsMenu.setVisible(true);
        }

        if (sessionState.isAdministrator()) {
            MenuItem users = new MenuItem("User Management");
            MenuItem system = new MenuItem("System Settings");
            MenuItem reports = new MenuItem("System Reports");
            operationsMenu.getItems().addAll(users, system, reports);
            operationsMenu.setVisible(true);
        }

        // Help Menu
        Menu helpMenu = new Menu("Help");
        MenuItem about = new MenuItem("About");
        MenuItem help = new MenuItem("Help");

        about.setOnAction(e -> showAboutDialog());
        help.setOnAction(e -> showHelpDialog());

        helpMenu.getItems().addAll(about, help);

        // Add all menus to the menu bar
        getMenus().addAll(fileMenu, operationsMenu, helpMenu);
    }

    private void handleLogout() {
        if (AlertDialog.showConfirmation("Logout", "Are you sure you want to logout?")) {
            sessionState.endSession();
            app.showLoginScreen();
        }
    }

    private void handleExit() {
        if (AlertDialog.showConfirmation("Exit", "Are you sure you want to exit?")) {
            primaryStage.close();
        }
    }

    private void showAboutDialog() {
        AlertDialog.showInfo("About",
                "Electronic Store Management System\nVersion 1.0\n© 2025");
    }

    private void showHelpDialog() {
        AlertDialog.showInfo("Help",
                "For assistance, please contact your system administrator.");
    }

    public void updateMenus() {
        getMenus().clear();
        initializeMenus();
    }

    // Event handlers for menu items
    public void setOnNewBill(Runnable action) {
        if (sessionState.isCashier()) {
            findMenuItem("New Bill").setOnAction(e -> action.run());
        }
    }

    public void setOnViewBills(Runnable action) {
        if (sessionState.isCashier()) {
            findMenuItem("View Today's Bills").setOnAction(e -> action.run());
        }
    }

    public void setOnInventory(Runnable action) {
        if (sessionState.isManager()) {
            findMenuItem("Inventory Management").setOnAction(e -> action.run());
        }
    }

    private MenuItem findMenuItem(String text) {
        for (Menu menu : getMenus()) {
            for (MenuItem item : menu.getItems()) {
                if (item instanceof MenuItem && item.getText().equals(text)) {
                    return item;
                }
            }
        }
        return null;
    }
}
*/