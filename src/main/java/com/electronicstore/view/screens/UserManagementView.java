/*package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.User;
import com.electronicstore.view.components.AlertDialog;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManagementView extends BorderPane {

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

        Label titleLabel = new Label("User Management");
        titleLabel.getStyleClass().add("view-title");

        HBox searchFilterBox = new HBox(10);
        searchFilterBox.setAlignment(Pos.CENTER_LEFT);

        searchField = new TextField();
        searchField.setPromptText("Search users...");
        searchField.setPrefWidth(250);
        searchField.textProperty().addListener((obs, oldV, newV) -> filterUsers());

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

        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        Button addUserButton = new Button("Add New User");
        addUserButton.getStyleClass().addAll(CSS_BUTTON, "button-primary");
        addUserButton.setOnAction(e -> showAddUserDialog());

        actionButtons.getChildren().add(addUserButton);

        topSection.getChildren().addAll(titleLabel, searchFilterBox, actionButtons);
        return topSection;
    }

    private VBox createCenterSection() {
        VBox centerSection = new VBox(15);
        centerSection.getStyleClass().add("users-table-section");

        usersTable = new TableView<>();
        usersTable.getStyleClass().add("users-table");

        TableColumn<User, String> idColumn = new TableColumn<>("ID");
        idColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getId()));

        TableColumn<User, String> usernameColumn = new TableColumn<>("Username");
        usernameColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getUsername()));

        TableColumn<User, String> nameColumn = new TableColumn<>("Name");
        nameColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getName()));

        TableColumn<User, String> emailColumn = new TableColumn<>("Email");
        emailColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getEmail()));

        TableColumn<User, String> roleColumn = new TableColumn<>("Role");
        roleColumn.setCellValueFactory(d -> new javafx.beans.property.SimpleStringProperty(d.getValue().getClass().getSimpleName()));

        TableColumn<User, String> statusColumn = new TableColumn<>("Status");
        statusColumn.setCellFactory(col -> new TableCell<>() {
            private final Label statusLabel = new Label();

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }

                User user = getTableRow().getItem();
                statusLabel.setText(user.isActive() ? "Active" : "Inactive");
                statusLabel.getStyleClass().setAll(
                        "status-label",
                        user.isActive() ? "status-active" : "status-inactive"
                );
                setGraphic(statusLabel);
            }
        });

        TableColumn<User, Void> actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || getTableRow() == null || getTableRow().getItem() == null) {
                    setGraphic(null);
                    return;
                }
                setGraphic(buildActions(getTableRow().getItem()));
            }

            // ✅ No initializer block anymore (fixes Sonar initializer warning)
            private HBox buildActions(User user) {
                Button editButton = new Button("Edit");
                Button resetPassButton = new Button("Reset Pass");
                Button toggleActiveButton = new Button(user.isActive() ? "Deactivate" : "Activate");

                editButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);
                resetPassButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);
                toggleActiveButton.getStyleClass().addAll(CSS_BUTTON, CSS_BUTTON_SMALL);

                editButton.setOnAction(e -> editUser(user));
                resetPassButton.setOnAction(e -> resetPassword(user));
                toggleActiveButton.setOnAction(e -> toggleUserActive(user));

                HBox actions = new HBox(5, editButton, resetPassButton, toggleActiveButton);
                return actions;
            }
        });

        usersTable.getColumns().addAll(
                idColumn, usernameColumn, nameColumn, emailColumn,
                roleColumn, statusColumn, actionColumn
        );

        usersTable.getSelectionModel().selectedItemProperty().addListener(
                (obs, oldS, newS) -> showUserDetails(newS)
        );

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
        usersTable.setItems(FXCollections.observableArrayList(userController.getAllUsers()));
    }

    private void filterUsers() {
        String searchText = safeLower(searchField.getText());
        String selectedRole = roleFilter.getValue();

        List<User> filtered = userController.getAllUsers().stream()
                .filter(u -> matchesSearch(u, searchText))
                .filter(u -> matchesRole(u, selectedRole))
                .toList();

        usersTable.setItems(FXCollections.observableArrayList(filtered));
    }

    private static boolean matchesSearch(User user, String searchText) {
        if (searchText.isEmpty()) return true;
        return safeLower(user.getName()).contains(searchText)
                || safeLower(user.getUsername()).contains(searchText);
    }

    private static boolean matchesRole(User user, String selectedRole) {
        if (selectedRole == null || ROLE_ALL.equals(selectedRole)) return true;
        return user.getClass().getSimpleName().equals(selectedRole);
    }

    private static String safeLower(String s) {
        return s == null ? "" : s.toLowerCase();
    }

    private void clearFilters() {
        searchField.clear();
        roleFilter.setValue(ROLE_ALL);
        loadUsers();
    }

    private void showUserDetails(User user) {
        userDetailsBox.getChildren().clear();
        if (user == null) return;

        userDetailsBox.getChildren().addAll(
                createDetailRow(LABEL_USERNAME, user.getUsername()),
                createDetailRow(LABEL_NAME, user.getName()),
                createDetailRow(LABEL_EMAIL, user.getEmail()),
                createDetailRow(LABEL_PHONE, user.getPhone()),
                createDetailRow(LABEL_ROLE, user.getClass().getSimpleName()),
                createDetailRow(LABEL_STATUS, user.isActive() ? "Active" : "Inactive")
        );

        // ✅ Sonar: instanceof pattern variable (fix #1)
        if (user instanceof Cashier cashier) {
            userDetailsBox.getChildren().add(createDetailRow(LABEL_SECTOR, cashier.getSector()));
        }
    }

    private HBox createDetailRow(String label, String value) {
        HBox row = new HBox(10);
        row.setAlignment(Pos.CENTER_LEFT);
        Label labelNode = new Label(label);
        labelNode.getStyleClass().add("detail-label");
        Text valueNode = new Text(value == null ? "" : value);
        valueNode.getStyleClass().add("detail-value");
        row.getChildren().addAll(labelNode, valueNode);
        return row;
    }

    private void showAddUserDialog() {
        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Add New User");
        dialog.setHeaderText("Enter User Details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        AddEditForm form = buildAddForm();
        dialog.getDialogPane().setContent(form.grid);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? form.toResultMap(true) : null);

        dialog.showAndWait().ifPresent(results -> {
            boolean ok = userController.addUser(
                    results.get(KEY_USERNAME),
                    results.get(KEY_PASSWORD),
                    results.get(KEY_NAME),
                    results.get(KEY_EMAIL),
                    results.get(KEY_PHONE),
                    results.get(KEY_ROLE),
                    results.get(KEY_SECTOR)
            );

            if (ok) {
                AlertDialog.showInfo(MSG_SUCCESS, MSG_USER_ADDED);
                loadUsers();
            } else {
                AlertDialog.showError(MSG_ERROR, MSG_USER_ADD_FAILED);
            }
        });
    }

    private void editUser(User user) {
        if (user == null) return;

        Dialog<Map<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Edit User");
        dialog.setHeaderText("Edit User Details");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

        AddEditForm form = buildEditForm(user);
        dialog.getDialogPane().setContent(form.grid);

        dialog.setResultConverter(btn -> btn == ButtonType.OK ? form.toResultMap(false) : null);

        dialog.showAndWait().ifPresent(results -> {
            boolean ok = userController.editUser(
                    user,
                    results.get(KEY_USERNAME),
                    results.get(KEY_NAME),
                    results.get(KEY_EMAIL),
                    results.get(KEY_PHONE),
                    results.get(KEY_ROLE),
                    results.get(KEY_SECTOR)
            );

            if (ok) {
                AlertDialog.showInfo(MSG_SUCCESS, MSG_USER_UPDATED);
                loadUsers();
            } else {
                AlertDialog.showError(MSG_ERROR, MSG_USER_UPDATE_FAILED);
            }
        });
    }

    private AddEditForm buildAddForm() {
        AddEditForm form = new AddEditForm();

        form.role.getItems().addAll(ROLE_CASHIER, ROLE_MANAGER);
        form.role.setValue(ROLE_CASHIER);

        addRow(form.grid, 0, LABEL_USERNAME, form.username);
        addRow(form.grid, 1, LABEL_NAME, form.name);
        addRow(form.grid, 2, LABEL_EMAIL, form.email);
        addRow(form.grid, 3, LABEL_PHONE, form.phone);
        addRow(form.grid, 4, "Password:", form.password);
        addRow(form.grid, 5, LABEL_ROLE, form.role);
        addRow(form.grid, 6, LABEL_SECTOR, form.sector);

        return form;
    }

    private AddEditForm buildEditForm(User user) {
        AddEditForm form = new AddEditForm();

        form.username.setText(user.getUsername());
        form.name.setText(user.getName());
        form.email.setText(user.getEmail());
        form.phone.setText(user.getPhone());

        form.role.getItems().addAll(ROLE_CASHIER, ROLE_MANAGER);
        form.role.setValue(user.getClass().getSimpleName());

        // ✅ Sonar: instanceof pattern variable (fix #2)
        if (user instanceof Cashier cashier) {
            form.sector.setText(cashier.getSector());
        }

        addRow(form.grid, 0, LABEL_USERNAME, form.username);
        addRow(form.grid, 1, LABEL_NAME, form.name);
        addRow(form.grid, 2, LABEL_EMAIL, form.email);
        addRow(form.grid, 3, LABEL_PHONE, form.phone);
        addRow(form.grid, 4, LABEL_ROLE, form.role);
        addRow(form.grid, 5, LABEL_SECTOR, form.sector);

        return form;
    }

    private static void addRow(GridPane grid, int row, String label, Control field) {
        grid.addRow(row, new Label(label), field);
    }

    private static class AddEditForm {
        final GridPane grid = new GridPane();
        final TextField username = new TextField();
        final TextField name = new TextField();
        final TextField email = new TextField();
        final TextField phone = new TextField();
        final PasswordField password = new PasswordField();
        final ComboBox<String> role = new ComboBox<>();
        final TextField sector = new TextField();

        AddEditForm() {
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20));
        }

        Map<String, String> toResultMap(boolean includePassword) {
            Map<String, String> results = new HashMap<>();
            results.put(KEY_USERNAME, username.getText());
            results.put(KEY_NAME, name.getText());
            results.put(KEY_EMAIL, email.getText());
            results.put(KEY_PHONE, phone.getText());
            results.put(KEY_ROLE, role.getValue());
            results.put(KEY_SECTOR, sector.getText());

            if (includePassword) {
                results.put(KEY_PASSWORD, password.getText());
            }
            return results;
        }
    }

    private void resetPassword(User user) {
        if (user == null) return;

        if (AlertDialog.showConfirmation(
                "Reset Password",
                "Are you sure you want to reset the password for " + user.getUsername() + "?"
        )) {
            if (userController.resetPassword(user)) {
                AlertDialog.showInfo(MSG_SUCCESS, "Password reset successfully.");
            } else {
                AlertDialog.showError(MSG_ERROR, "Failed to reset password.");
            }
        }
    }

    private void toggleUserActive(User user) {
        if (user == null) return;

        String action = user.isActive() ? "deactivate" : "activate";
        if (AlertDialog.showConfirmation(
                "Confirm Action",
                "Are you sure you want to " + action + " user " + user.getUsername() + "?"
        )) {
            // your toggle logic here (left as-is)
        }
    }
}
*/
