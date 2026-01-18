package com.electronicstore.ui.systemtest;

import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class UserAdminFrontendSystemTest extends BaseFrontendSystemTest {

    private static final String ADMIN_USER = "admin1";
    private static final String ADMIN_PASS = "1234";

    private final SessionState session = SessionState.getInstance();

    @BeforeEach
    void resetAndSeedAdmin() throws IOException {
        // Clean saved data so every test starts fresh
        session.endSession();

        deleteFolderIfExists(Paths.get(FileHandler.DATA_DIRECTORY));
        Files.createDirectories(Paths.get(FileHandler.DATA_DIRECTORY));
        Files.createDirectories(Paths.get(FileHandler.DATA_DIRECTORY).resolve("bills"));

        // Create one admin using backend (so we can login in UI)
        UserManagementController users = new UserManagementController();
        assertTrue(users.addUser(
                ADMIN_USER, ADMIN_PASS,
                "System Admin", "admin@estore.com", "0670000000",
                "administrator", null
        ));

        interact(() -> app.showLoginScreen());
        WaitForAsyncUtils.waitForFxEvents();
    }

    @Test
    void reset_password() {
        login(ADMIN_USER, ADMIN_PASS);

        assertTrue(session.isAdministrator());
        verifyThat("System Administrator Dashboard", isVisible());

        // Create cashier (backend)
        UserManagementController users = new UserManagementController();
        assertTrue(users.addUser(
                "cash1", "2222",
                "Cashier User", "cashier@estore.com", "0671111111",
                "cashier", "Electronics"
        ));

        // Open user management screen
        clickOn("User Management");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("User Management", isVisible());

        // Click Reset Pass inside cash1 row
        clickRowActionButton("cash1", "Reset Pass");

        // confirm
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");
        WaitForAsyncUtils.waitForFxEvents();

        closeAlertIfPresent();

        // Reload and verify password changed
        UserManagementController reload = new UserManagementController();
        User updated = reload.getAllUsers().stream()
                .filter(u -> u.getUsername().equals("cash1"))
                .findFirst()
                .orElseThrow();

        assertEquals("password", updated.getPassword(), "Reset Pass should set password to 'password'.");

        clickOn("← Back");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("System Administrator Dashboard", isVisible());

        logoutViaMenu();
        verifyThat("Login", isVisible());
    }

    @Test
    void edit_user() {
        login(ADMIN_USER, ADMIN_PASS);

        assertTrue(session.isAdministrator());
        verifyThat("System Administrator Dashboard", isVisible());

        // Create cashier (backend)
        UserManagementController users = new UserManagementController();
        assertTrue(users.addUser(
                "cash1", "2222",
                "Cashier User", "cashier@estore.com", "0671111111",
                "cashier", "Electronics"
        ));

        clickOn("User Management");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("User Management", isVisible());

        // Click Edit inside cash1 row
        clickRowActionButton("cash1", "Edit");
        WaitForAsyncUtils.waitForFxEvents();

        // Fill edit dialog fields
        setDialogFieldByLabel("Name:", "Updated Cashier");
        setDialogFieldByLabel("Email:", "newcashier@estore.com");
        setDialogFieldByLabel("Phone:", "0699999999");
        setDialogComboByLabel("Role:", "Cashier");
        setDialogFieldByLabel("Sector:", "Electronics");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();
        closeAlertIfPresent();

        // Reload and verify changes
        UserManagementController reload = new UserManagementController();
        User after = reload.getAllUsers().stream()
                .filter(u -> u.getUsername().equals("cash1"))
                .findFirst()
                .orElseThrow();

        assertEquals("Updated Cashier", after.getName());
        assertEquals("newcashier@estore.com", after.getEmail());
        assertEquals("0699999999", after.getPhone());

        clickOn("← Back");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("System Administrator Dashboard", isVisible());

        logoutViaMenu();
        verifyThat("Login", isVisible());
    }

    @Test
    void delete_user() {
        login(ADMIN_USER, ADMIN_PASS);

        assertTrue(session.isAdministrator());
        verifyThat("System Administrator Dashboard", isVisible());

        // Create cashier (backend)
        UserManagementController users = new UserManagementController();
        assertTrue(users.addUser(
                "cash1", "2222",
                "Cashier User", "cashier@estore.com", "0671111111",
                "cashier", "Electronics"
        ));

        clickOn("User Management");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("User Management", isVisible());

        // Click Delete inside cash1 row
        clickRowActionButton("cash1", "Delete");
        WaitForAsyncUtils.waitForFxEvents();

        // confirm
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");
        WaitForAsyncUtils.waitForFxEvents();

        closeAlertIfPresent();

        // Reload and check user is gone
        UserManagementController reload = new UserManagementController();
        assertTrue(reload.getAllUsers().stream().noneMatch(u -> u.getUsername().equals("cash1")),
                "cash1 should be deleted.");

        clickOn("← Back");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("System Administrator Dashboard", isVisible());

        logoutViaMenu();
        verifyThat("Login", isVisible());
    }

    @Test
    void add_user() {
        login(ADMIN_USER, ADMIN_PASS);

        assertTrue(session.isAdministrator());
        verifyThat("System Administrator Dashboard", isVisible());

        clickOn("User Management");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("User Management", isVisible());

        // Open add user dialog
        clickOn("Add New User");
        WaitForAsyncUtils.waitForFxEvents();

        String newU = "mgr_" + uniqueUser("u").substring(0, 6);
        setDialogFieldByLabel("Username:", newU);
        setDialogFieldByLabel("Name:", "New Manager");
        setDialogFieldByLabel("Email:", "newmanager@estore.com");
        setDialogFieldByLabel("Phone:", "0691234567");
        setDialogFieldByLabel("Password:", "1111");
        setDialogComboByLabel("Role:", "Manager");
        setDialogFieldByLabel("Sector:", "");

        clickOn("OK");
        WaitForAsyncUtils.waitForFxEvents();
        closeAlertIfPresent();

        // Reload and check user exists
        UserManagementController reload = new UserManagementController();
        assertTrue(reload.getAllUsers().stream().anyMatch(u -> u.getUsername().equals(newU)),
                "Expected new user to be created by admin via UI.");

        clickOn("← Back");
        WaitForAsyncUtils.waitForFxEvents();
        verifyThat("System Administrator Dashboard", isVisible());

        logoutViaMenu();
        verifyThat("Login", isVisible());
    }

    // ---------- Row button helper ----------

    private void clickRowActionButton(String username, String buttonText) {
        // Find the table row for this user, then click the button inside it
        Node row = lookup((Node n) ->
                n instanceof TableRow<?> tr &&
                        tr.isVisible() &&
                        tr.getItem() instanceof User u &&
                        username.equals(u.getUsername())
        ).query();

        Button btn = from(row).lookup(buttonText).queryButton();
        clickOn(btn);
        WaitForAsyncUtils.waitForFxEvents();
    }

    // ---------- Logout ----------

    private void logoutViaMenu() {
        clickOn("File");
        WaitForAsyncUtils.waitForFxEvents();
        clickOn("Logout");
        WaitForAsyncUtils.waitForFxEvents();

        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");

        WaitForAsyncUtils.waitForFxEvents();
    }

    // ---------- Dialog helpers ----------

    private void setDialogFieldByLabel(String labelText, String value) {
        GridPane grid = findVisibleDialogGrid();

        Label label = (Label) grid.getChildren().stream()
                .filter(n -> n instanceof Label && labelText.equals(((Label) n).getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Dialog label not found: " + labelText));

        Integer row = GridPane.getRowIndex(label);
        Integer col = GridPane.getColumnIndex(label);
        int r = row == null ? 0 : row;
        int c = col == null ? 0 : col;

        Node fieldNode = getNodeAt(grid, c + 1, r)
                .orElseThrow(() -> new AssertionError("Dialog field not found next to: " + labelText));

        if (!(fieldNode instanceof TextInputControl field)) {
            throw new AssertionError("Expected TextInputControl next to " + labelText + " but got " + fieldNode.getClass());
        }

        clickOn(field);
        field.clear();
        write(value);
        WaitForAsyncUtils.waitForFxEvents();
    }

    private void setDialogComboByLabel(String labelText, String valueToSelect) {
        GridPane grid = findVisibleDialogGrid();

        Label label = (Label) grid.getChildren().stream()
                .filter(n -> n instanceof Label && labelText.equals(((Label) n).getText()))
                .findFirst()
                .orElseThrow(() -> new AssertionError("Dialog label not found: " + labelText));

        int r = GridPane.getRowIndex(label) == null ? 0 : GridPane.getRowIndex(label);
        int c = GridPane.getColumnIndex(label) == null ? 0 : GridPane.getColumnIndex(label);

        Node node = getNodeAt(grid, c + 1, r)
                .orElseThrow(() -> new AssertionError("Dialog combo not found next to: " + labelText));

        if (!(node instanceof ComboBox<?> combo)) {
            throw new AssertionError("Expected ComboBox next to " + labelText + " but got " + node.getClass());
        }

        clickOn(combo);
        clickOn(valueToSelect);
        WaitForAsyncUtils.waitForFxEvents();
    }

    private GridPane findVisibleDialogGrid() {
        DialogPane pane = lookup((Node n) -> n instanceof DialogPane && n.isVisible()).query();
        Node content = pane.getContent();
        if (content instanceof GridPane gp) return gp;

        Optional<GridPane> nested = findFirstGridPane(content);
        return nested.orElseThrow(() -> new AssertionError("No GridPane found in visible dialog."));
    }

    private Optional<GridPane> findFirstGridPane(Node root) {
        if (root instanceof GridPane gp) return Optional.of(gp);
        if (root instanceof Parent p) {
            for (Node child : p.getChildrenUnmodifiable()) {
                Optional<GridPane> found = findFirstGridPane(child);
                if (found.isPresent()) return found;
            }
        }
        return Optional.empty();
    }

    private Optional<Node> getNodeAt(GridPane grid, int col, int row) {
        return grid.getChildren().stream()
                .filter(n -> {
                    Integer c = GridPane.getColumnIndex(n);
                    Integer r = GridPane.getRowIndex(n);
                    int cc = (c == null) ? 0 : c;
                    int rr = (r == null) ? 0 : r;
                    return cc == col && rr == row;
                })
                .findFirst();
    }

    // ---------- cleanup helper ----------

    private static void deleteFolderIfExists(Path root) throws IOException {
        if (!Files.exists(root)) return;

        Files.walk(root)
                .sorted(Comparator.reverseOrder())
                .forEach(path -> {
                    try {
                        Files.deleteIfExists(path);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
    }
}
