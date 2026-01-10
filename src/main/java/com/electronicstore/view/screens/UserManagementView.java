package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.users.*;
import com.electronicstore.view.components.AlertDialog;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import org.w3c.dom.Node;

import java.util.HashMap;
import java.util.Map;

public class UserManagementView extends BorderPane {

    // ---------- Sonar: constants for duplicated literals ----------
    private static final String ROLE_ALL = "All";
    private static final String ROLE_CASHIER = "Cashier";
    private static final String ROLE_MANAGER = "Manager";
    private static final String ROLE_ADMIN = "Administrator";

    private static final String LABEL_USERNAME = "Username:";
    private static final String LABEL_NAME = "Name:";
    private static final String LABEL_EMAIL = "Email:";
    private static final String LABEL_PHONE = "Phone:";
    private static final String LABEL_ROLE = "Role:";
    private static final String LABEL_SECTOR = "Sector:";
    private static final String LABEL_STATUS = "Status:";

    private static final String KEY_USERNAME = "username";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_ROLE = "role";
    private static final String KEY_SECTOR = "sector";

    private static final String MSG_SUCCESS = "Success";
    private static final String MSG_ERROR = "Error";
    private static final String MSG_USER_ADDED = "User added successfully.";
    private static final String MSG_USER_ADD_FAILED = "Failed to add user.";
    private static final String MSG_USER_UPDATED = "User updated successfully.";
    private static final String MSG_USER_UPDATE_FAILED = "Failed to update user.";

    private static final String CSS_BUTTON = "button";
    private static final String CSS_BUTTON_SMALL = "button-small";
    // ------------------------------------------------------------

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
        roleFilter.getItems().addAll(ROLE_ALL, ROLE_CASHIER, ROLE_MANAGER, ROLE_ADMIN);
        roleFilter.setValue(ROLE_ALL);
        roleFilter.setOnAction(e -> filterUsers());

        Button clearFiltersButton = new Button("Clear Filters");
        clearFiltersButton.getStyleClass().add("button-secondary");
        clearFiltersButton.setOnAction(e -> clearFilters());

        searchFilterBox.getChildren().addAll(
                new Label("Search:"), searchField,
                new Label(LABEL_ROLE), roleFilter,
                clearFiltersButton
        );

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button addUserButton = new Button("Add New User");
        addUserButton.getStyleClass().addAll(CSS_BUTTON, "button-primary");
        addUserButton.setOnAction(e -> showAddUserDialog());

        actionButtons.getChildren().addAll(addUserButton);

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
            private final Button toggleActiveButton = new Button();

            {
                editButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);
                resetPassButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);
                toggleActiveButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);

                actions.getChildren().addAll(editButton, resetPassButton, toggleActiveButton);

                editButton.setOnAction(e -> editUser(getTableRow().getItem()));
                resetPassButton.setOnAction(e -> resetPassword(getTableRow().getItem()));
                toggleActiveButton.setOnAction(e -> toggleUserActive(getTableRow().getItem()));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    User user = getTableRow().getItem();
                    if (user != null) {
                        toggleActiveButton.setText(user.isActive() ? "Deactivate" : "Activate");
                        setGraphic(actions);
                    }
                }
            }
        });

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
                                (searchText.isEmpty()
                                        || user.getName().toLowerCase().contains(searchText)
                                        || user.getUsername().toLowerCase().contains(searchText))
                                        && (selectedRole.equals(ROLE_ALL)
                                        || user.getClass().getSimpleName().equals(selectedRole)))
                        .toList()));
    }

    private void clearFilters() {
        searchField.clear();
        roleFilter.setValue(ROLE_ALL);
        loadUsers();
    }

    private void showUserDetails(User user) {
        userDetailsBox.getChildren().clear();
        if (user != null) {
            userDetailsBox.getChildren().addAll(
                    createDetailRow(LABEL_USERNAME, user.getUsername()),
                    createDetailRow(LABEL_NAME, user.getName()),
                    createDetailRow(LABEL_EMAIL, user.getEmail()),
                    createDetailRow(LABEL_PHONE, user.getPhone()),
                    createDetailRow(LABEL_ROLE, user.getClass().getSimpleName()),
                    createDetailRow(LABEL_STATUS, user.isActive() ? "Active" : "Inactive")
            );

            if (user instanceof Cashier) {
                userDetailsBox.getChildren().add(
                        createDetailRow(LABEL_SECTOR, ((Cashier) user).getSector()));
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

        role.getItems().addAll(ROLE_CASHIER, ROLE_MANAGER);
        role.setValue(ROLE_CASHIER);

        grid.addRow(0, new Label(LABEL_USERNAME), username);
        grid.addRow(1, new Label(LABEL_NAME), name);
        grid.addRow(2, new Label(LABEL_EMAIL), email);
        grid.addRow(3, new Label(LABEL_PHONE), phone);
        grid.addRow(4, new Label("Password:"), password);
        grid.addRow(5, new Label(LABEL_ROLE), role);
        grid.addRow(6, new Label(LABEL_SECTOR), sector);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, String> results = new HashMap<>();
                results.put(KEY_USERNAME, username.getText());
                results.put(KEY_NAME, name.getText());
                results.put(KEY_EMAIL, email.getText());
                results.put(KEY_PHONE, phone.getText());
                results.put(KEY_PASSWORD, password.getText());
                results.put(KEY_ROLE, role.getValue());
                results.put(KEY_SECTOR, sector.getText());
                return results;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(results -> {
            if (userController.addUser(
                    results.get(KEY_USERNAME),
                    results.get(KEY_PASSWORD),
                    results.get(KEY_NAME),
                    results.get(KEY_EMAIL),
                    results.get(KEY_PHONE),
                    results.get(KEY_ROLE),
                    results.get(KEY_SECTOR))) {
                AlertDialog.showInfo(MSG_SUCCESS, MSG_USER_ADDED);
                loadUsers();
            } else {
                AlertDialog.showError(MSG_ERROR, MSG_USER_ADD_FAILED);
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

        role.getItems().addAll(ROLE_CASHIER, ROLE_MANAGER);
        role.setValue(user.getClass().getSimpleName());

        if (user instanceof Cashier) {
            sector.setText(((Cashier) user).getSector());
        }

        grid.addRow(0, new Label(LABEL_USERNAME), username);
        grid.addRow(1, new Label(LABEL_NAME), name);
        grid.addRow(2, new Label(LABEL_EMAIL), email);
        grid.addRow(3, new Label(LABEL_PHONE), phone);
        grid.addRow(4, new Label(LABEL_ROLE), role);
        grid.addRow(5, new Label(LABEL_SECTOR), sector);

        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        dialog.setResultConverter(buttonType -> {
            if (buttonType == ButtonType.OK) {
                Map<String, String> results = new HashMap<>();
                results.put(KEY_USERNAME, username.getText());
                results.put(KEY_NAME, name.getText());
                results.put(KEY_EMAIL, email.getText());
                results.put(KEY_PHONE, phone.getText());
                results.put(KEY_ROLE, role.getValue());
                results.put(KEY_SECTOR, sector.getText());
                return results;
            }
            return null;
        });

        dialog.showAndWait().ifPresent(results -> {
            if (userController.editUser(
                    user,
                    results.get(KEY_USERNAME),
                    results.get(KEY_NAME),
                    results.get(KEY_EMAIL),
                    results.get(KEY_PHONE),
                    results.get(KEY_ROLE),
                    results.get(KEY_SECTOR))) {
                AlertDialog.showInfo(MSG_SUCCESS, MSG_USER_UPDATED);
                loadUsers();
            } else {
                AlertDialog.showError(MSG_ERROR, MSG_USER_UPDATE_FAILED);
            }
        });
    }

    private void resetPassword(User user) {
        if (AlertDialog.showConfirmation("Reset Password",
                "Are you sure you want to reset the password for " + user.getUsername() + "?")) {
            if (userController.resetPassword(user)) {
                AlertDialog.showInfo(MSG_SUCCESS, "Password reset successfully.");
            } else {
                AlertDialog.showError(MSG_ERROR, "Failed to reset password.");
            }
        }
    }

    private void toggleUserActive(User user) {
        String action = user.isActive() ? "deactivate" : "activate";
        if (AlertDialog.showConfirmation("Confirm Action",
                "Are you sure you want to " + action + " user " + user.getUsername() + "?")) {
            // your toggle logic here (left as-is)
        }
    }
}
