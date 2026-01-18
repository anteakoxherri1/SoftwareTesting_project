package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.users.*;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.collections.FXCollections;
import javafx.scene.text.Text;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class UserManagementView extends BorderPane {
    private final App app;
    private final UserManagementController userController;
    private TableView<User> usersTable;
    private ComboBox<String> roleFilter;
    private TextField searchField;
    private VBox userDetailsBox;

    public UserManagementView(App app) {
        this.app = app;
        this.userController = new UserManagementController();

        getStyleClass().add("user-management-view");
        setPadding(new Insets(20));

        initializeComponents();
        loadUsers();
    }

    private void initializeComponents() {
        setTop(createTopSection());
        setCenter(createCenterSection());
        setRight(createRightSection());
    }

    private VBox createTopSection() {
       
    	VBox topSection = new VBox(15);
        topSection.getStyleClass().add("user-management-header");

        // Title
        Label titleLabel = new Label("User Management");
        titleLabel.getStyleClass().add("view-title");

        // Search and Filter Section
        HBox searchFilterBox = new HBox(10);
        searchFilterBox.setAlignment(Pos.CENTER_LEFT);

        // Search field
        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, old, newValue) -> filterUsers());

        // Role filter
        roleFilter = new ComboBox<>();
        roleFilter.setPromptText("Filter by Role");
        roleFilter.getItems().addAll("All", "Cashier", "Manager", "Administrator");
        roleFilter.setValue("All");
        roleFilter.setOnAction(e -> filterUsers());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.getStyleClass().add("button-secondary");
        clearFiltersButton.setOnAction(e -> clearFilters());

        searchFilterBox.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label("Role:"), roleFilter,
                clearFiltersButton
        );

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button addUserButton = new Button("Add New User");
        addUserButton.getStyleClass().addAll("button", "button-primary");
        addUserButton.setOnAction(e -> showAddUserDialog());
        
        Button backButton = new Button("← Back");
    	backButton.setOnAction(e -> app.showAdminDashboard());

        actionButtons.getChildren().addAll(addUserButton,backButton);

        topSection.getChildren().addAll(titleLabel, searchFilterBox, actionButtons);
        return topSection;
    }

    private VBox createCenterSection() {
        VBox centerSection = new VBox(15);
        centerSection.getStyleClass().add("users-table-section");

        // Create table
        usersTable = new TableView<>();
        usersTable.getStyleClass().add("users-table");

        // ID Column
        TableColumn<User, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getId()));

        // Username Column
        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getUsername()));

        // Name Column
        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getName()));

        // Email Column
        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(data.getValue().getEmail()));

        // Role Column
        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(data ->
                new javafx.beans.property.SimpleStringProperty(
                        data.getValue().getClass().getSimpleName()));

        // Status Column
        TableColumn<User, Node> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellFactory(col -> new TableCell<>() {
            private final Label statusLabel = new Label();

            @Override
            protected void updateItem(Node item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableView().getItems().get(getIndex());
                    statusLabel.setText(user.isActive() ? "Active" : "Inactive");
                    statusLabel.getStyleClass().setAll(
                            "status-label",
                            user.isActive() ? "status-active" : "status-inactive"
                    );
                    setGraphic(statusLabel);
                }
            }
        });

        // Actions Column
        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            private final HBox actions = new HBox(5);
            private final Button editButton = new Button("Edit");
            private final Button resetPassButton = new Button("Reset Pass");
            private final Button deleteButton = new Button("Delete");


            {
                editButton.getStyleClass().addAll("button", "button-small");
                resetPassButton.getStyleClass().addAll("button", "button-small");
                deleteButton.getStyleClass().addAll("button", "button-small", "button-danger");


                actions.getChildren().addAll(editButton, resetPassButton, deleteButton);

                editButton.setOnAction(e -> editUser(getTableRow().getItem()));
                resetPassButton.setOnAction(e -> resetPassword(getTableRow().getItem()));
                deleteButton.setOnAction(e -> deleteUser(getTableRow().getItem()));
            
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow().getItem() == null) {
                    setGraphic(null);
                } else {
                    setGraphic(actions);
                }
            }
        });

          /*  @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        setGraphic(actions);
                    }
                }
            }
        });
        */

        usersTable.getColumns().addAll(
                idColumn, usernameColumn, nameColumn, emailColumn,
                roleColumn, statusColumn, actionColumn
        );

        // Selection listener for showing user details
        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldSelection, newSelection) -> showUserDetails(newSelection));

        centerSection.getChildren().add(usersTable);
        return centerSection;
    }

    private VBox createRightSection() {
        VBox rightSection = new VBox(15);
        rightSection.getStyleClass().add("user-details-section");
        rightSection.setPrefWidth(300);

        Label detailsTitle = new Label("User Details");
        detailsTitle.getStyleClass().add("section-title");

        userDetailsBox = new VBox(10);
        userDetailsBox.getStyleClass().add("details-container");

        rightSection.getChildren().addAll(detailsTitle, new Separator(), userDetailsBox);
        return rightSection;
    }

    private void loadUsers() {
        usersTable.setItems(FXCollections.observableArrayList(
                userController.getAllUsers()));
    }

    private void filterUsers() {
        String searchText = searchField.getText().toLowerCase();
        String selectedRole = roleFilter.getValue();

        usersTable.setItems(FXCollections.observableArrayList(
                userController.getAllUsers().stream()
                        .filter(user ->
                                (searchText.isEmpty() ||
                                        user.getName().toLowerCase().contains(searchText) ||
                                        user.getUsername().toLowerCase().contains(searchText)) &&
                                        (selectedRole.equals("All") ||
                                                user.getClass().getSimpleName().equals(selectedRole)))
                        .toList()));
    }

    private void clearFilters() {
        searchField.clear();
        roleFilter.setValue("All");
        loadUsers();
    }

    private void showUserDetails(User user) {
        userDetailsBox.getChildren().clear();
        if (user != null) {
            userDetailsBox.getChildren().addAll(
                    createDetailRow("Username:", user.getUsername()),
                    createDetailRow("Name:", user.getName()),
                    createDetailRow("Email:", user.getEmail()),
                    createDetailRow("Phone:", user.getPhone()),
                    createDetailRow("Role:", user.getClass().getSimpleName()),
                    createDetailRow("Status:", user.isActive() ? "Active" : "Inactive")
            );

            if (user instanceof Cashier) {
                userDetailsBox.getChildren().add(
                        createDetailRow("Sector:", ((Cashier) user).getSector()));
            }
        }
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("detail-label");
        Text valueNode = new Text(value);
        valueNode.getStyleClass().add("detail-value");
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private void showAddUserDialog() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter User Details");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField username = new TextField();
        TextField name = new TextField();
        TextField email = new TextField();
        TextField phone = new TextField();
        PasswordField password = new PasswordField();
        ComboBox<String> role = new ComboBox<>();
        TextField sector = new TextField();

        role.getItems().addAll("Cashier", "Manager");
        role.setValue("Cashier");

        grid.addRow(0, new Label("Username:"), username);
        grid.addRow(1, new Label("Name:"), name);
        grid.addRow(2, new Label("Email:"), email);
        grid.addRow(3, new Label("Phone:"), phone);
        grid.addRow(4, new Label("Password:"), password);
        grid.addRow(5, new Label("Role:"), role);
        grid.addRow(6, new Label("Sector:"), sector);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, String> results = new HashMap<>();
                results.put("username", username.getText());
                results.put("name", name.getText());
                results.put("email", email.getText());
                results.put("phone", phone.getText());
                results.put("password", password.getText());
                results.put("role", role.getValue());
                results.put("sector", sector.getText());
                return results;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(results -> {
            if (userController.addUser(
                    results.get("username"),
                    results.get("password"),
                    results.get("name"),
                    results.get("email"),
                    results.get("phone"),
                    results.get("role"),
                    results.get("sector"))) {
                AlertDialog.showInfo("Success", "User added successfully.");
                loadUsers();
            } else {
                AlertDialog.showError("Error", "Failed to add user.");
            }
        });
    }

    private void editUser(User user) {
        if (user == null) {
            return;
        }

        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Details");

        // Create the dialog content
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20));

        TextField username = new TextField(user.getUsername());
        TextField name = new TextField(user.getName());
        TextField email = new TextField(user.getEmail());
        TextField phone = new TextField(user.getPhone());
        ComboBox<String> role = new ComboBox<>();
        TextField sector = new TextField();

        role.getItems().addAll("Cashier", "Manager");
        role.setValue(user.getClass().getSimpleName());

        if (user instanceof Cashier) {
            sector.setText(((Cashier) user).getSector());
        }

        grid.addRow(0, new Label("Username:"), username);
        grid.addRow(1, new Label("Name:"), name);
        grid.addRow(2, new Label("Email:"), email);
        grid.addRow(3, new Label("Phone:"), phone);
        grid.addRow(4, new Label("Role:"), role);
        grid.addRow(5, new Label("Sector:"), sector);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, String> results = new HashMap<>();
                results.put("username", username.getText());
                results.put("name", name.getText());
                results.put("email", email.getText());
                results.put("phone", phone.getText());
                results.put("role", role.getValue());
                results.put("sector", sector.getText());
                return results;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(results -> {
            if (userController.editUser(
                    user,
                    results.get("username"),
                    results.get("name"),
                    results.get("email"),
                    results.get("phone"),
                    results.get("role"),
                    results.get("sector"))) {
                AlertDialog.showInfo("Success", "User updated successfully.");
                loadUsers();
            } else {
                AlertDialog.showError("Error", "Failed to update user.");
            }
        });
    }

    private void resetPassword(User user) {
        if (AlertDialog.showConfirmation("Reset Password",
                "Are you sure you want to reset the password for " + user.getUsername() + "?")) {
            if (userController.resetPassword(user)) {
                AlertDialog.showInfo("Success", "Password reset successfully.");
            } else {
                AlertDialog.showError("Error", "Failed to reset password.");
            }
        }
    }
    private void deleteUser(User user) {
        if (user == null) return;

        if (AlertDialog.showConfirmation(
                "Delete User",
                "Are you sure you want to delete user '" + user.getUsername() +
                "'?\n\nThis action cannot be undone.")) {

            if (userController.deleteUser(user.getId())) {
                AlertDialog.showInfo("Success", "User deleted successfully.");
                loadUsers(); // refresh table
            } else {
                AlertDialog.showError("Error", "Failed to delete user.");
            }
        }
    }


    private void toggleUserActive(User user) {
        if (user == null) return;

        String action = user.isActive() ? "deactivate" : "activate";

        if (AlertDialog.showConfirmation(
                "Confirm Action",
                "Are you sure you want to " + action + " user " + user.getUsername() + "?")) {

            if (userController.toggleUserActive(user.getId())) {
                AlertDialog.showInfo("Success",
                        "User " + action + "d successfully.");
                loadUsers(); // refresh table
            } else {
                AlertDialog.showError("Error",
                        "Failed to " + action + " user.");
            }
        }
    }

}