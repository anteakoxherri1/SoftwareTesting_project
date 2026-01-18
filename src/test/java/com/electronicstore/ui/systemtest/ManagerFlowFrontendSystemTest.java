package com.electronicstore.ui.systemtest;

import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextInputControl;
import javafx.scene.layout.Pane;
import javafx.stage.Window;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assumptions.assumeTrue;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ManagerFlowFrontendSystemTest extends BaseFrontendSystemTest {

    private static String mgrU, mgrP;

    private static String categoryName;
    private static String supplierName;
    private static String itemName;

    @BeforeAll
    static void cleanDataFiles() throws Exception {
        // Delete saved data so tests are consistent
        Path dataDir = Paths.get(FileHandler.DATA_DIRECTORY);

        Files.deleteIfExists(dataDir.resolve("users.dat"));
        Files.deleteIfExists(dataDir.resolve("items.dat"));
        Files.deleteIfExists(dataDir.resolve("categories.dat"));
        Files.deleteIfExists(dataDir.resolve("suppliers.dat"));
    }

    // ---------- TEST 1 ----------

    @Test
    @Order(1)
    void mgr_register_and_login() {
        mgrU = uniqueUser("mgr");
        mgrP = "Mgr123!";

        // register -> login -> check dashboard -> logout
        registerUser(mgrU, mgrP, "Manager", null);
        login(mgrU, mgrP);
        waitFx();

        assertTrue(SessionState.getInstance().isManager(), "Manager should be logged in.");

        verifyThat("New Item", isVisible());
        verifyThat("Manage Inventory", isVisible());
        verifyThat("Manage Suppliers", isVisible());
        verifyThat("Manage Categories", isVisible());

        logoutViaMenu();
    }

    // ---------- TEST 2: add category ----------

    @Test
    @Order(2)
    void add_category() {
        ensureManagerLoggedIn();

        categoryName = "Cat_" + UUID.randomUUID().toString().substring(0, 6);

        clickOn("Manage Categories");
        waitFx();

        // Category dialog should open
        verifyThat("Manage Categories", isVisible());

        Pane dialogPane = getTopDialogPane();
        List<TextField> fields = textFieldsInside(dialogPane);
        assertTrue(fields.size() >= 3, "Expected 3 TextFields in Category dialog.");

        // [0]=name, [1]=minStock, [2]=sector
        typeInto(fields.get(0), categoryName);
        typeInto(fields.get(1), "5");
        typeInto(fields.get(2), "Accessories");

        clickButtonInside(dialogPane, "Add Category");
        closeAlertIfPresent();

        // Category should show in table
        assertTrue(lookup(categoryName).tryQuery().isPresent(),
                "Category should appear in table after adding.");

        clickButtonInside(dialogPane, "Close");
        waitFx();

        logoutViaMenu();
    }

    // ---------- TEST 3: add supplier ----------

    @Test
    @Order(3)
    void add_supplier() {
        ensureManagerLoggedIn();

        supplierName = "Sup_" + UUID.randomUUID().toString().substring(0, 6);

        clickOn("Manage Suppliers");
        waitFx();

        verifyThat("Manage Suppliers", isVisible());

        Pane dialogPane = getTopDialogPane();
        List<TextField> fields = textFieldsInside(dialogPane);
        assertTrue(fields.size() >= 2, "Expected 2 TextFields in Supplier dialog.");

        // [0]=name, [1]=contact
        typeInto(fields.get(0), supplierName);
        typeInto(fields.get(1), "contact@test.com");

        clickButtonInside(dialogPane, "Add Supplier");
        closeAlertIfPresent();

        // Supplier should show in table
        assertTrue(lookup(supplierName).tryQuery().isPresent(),
                "Supplier should appear in table after adding.");

        clickButtonInside(dialogPane, "Close");
        waitFx();

        logoutViaMenu();
    }

    // ---------- TEST 4: add item ----------

    @Test
    @Order(4)
    void add_item() {
        ensureManagerLoggedIn();

        // Make sure category/supplier exist
        if (categoryName == null) categoryName = createCategoryForThisTest();
        if (supplierName == null) supplierName = createSupplierForThisTest();

        itemName = "Item_" + UUID.randomUUID().toString().substring(0, 6);

        clickOn("New Item");
        waitFx();

        // If app blocks opening, it shows error/warning
        if (lookup("Error").tryQuery().isPresent() || lookup("Warning").tryQuery().isPresent()) {
            closeAlertIfPresent();
            fail("Dashboard blocked New Item (missing category/supplier).");
        }

        // Get the dialog pane (AddItemDialog)
        Pane dialogPane = getTopDialogPane();

        // Quick check that this is the Add Item dialog
        assertTrue(dialogPane.lookupAll(".button").stream()
                        .anyMatch(n -> n instanceof Button b && "Add".equals(b.getText())),
                "Expected Add button in Add Item dialog.");

        // Fill item fields
        setTextByPrompt("Enter item name", itemName);

        clickOn("Select category");
        clickOn(categoryName);
        waitFx();

        clickOn("Select supplier");
        clickOn(supplierName);
        waitFx();

        setTextByPrompt("Enter purchase price", "10");
        setTextByPrompt("Enter selling price", "15");
        setTextByPrompt("Enter quantity", "20");

        // Add item
        clickButtonInside(dialogPane, "Add");
        waitFx();

        // Open inventory and check item exists
        clickOn("Manage Inventory");
        waitFx();

        verifyThat("Inventory Management", isVisible());
        assertTrue(lookup(itemName).tryQuery().isPresent(),
                "New item should appear in Inventory table after adding.");

        logoutViaMenu();
    }

    // ---------- TEST 5: delete supplier ----------

    @Test
    @Order(5)
    void delete_supplier() {
        ensureManagerLoggedIn();

        assumeTrue(supplierName != null, "Supplier missing; run tests in order.");

        clickOn("Manage Suppliers");
        waitFx();

        verifyThat("Manage Suppliers", isVisible());

        Pane dialogPane = getTopDialogPane();

        // Select supplier row and delete
        clickOn(supplierName);
        waitFx();

        clickButtonInside(dialogPane, "Delete");
        waitFx();

        // Confirm delete
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");
        waitFx();

        closeAlertIfPresent();

        // Supplier should not show anymore
        assertTrue(lookup(supplierName).tryQuery().isEmpty(),
                "Supplier should not be visible after deletion.");

        clickButtonInside(dialogPane, "Close");
        waitFx();

        logoutViaMenu();
    }

    // ---------- helpers ----------

    private void ensureManagerLoggedIn() {
        assumeTrue(mgrU != null && mgrP != null, "Manager not created; run test 1 first.");

        interact(() -> app.showLoginScreen());
        waitFx();

        login(mgrU, mgrP);
        waitFx();

        assertTrue(SessionState.getInstance().isManager(), "Manager login failed.");
        verifyThat("Manage Inventory", isVisible());
    }

    private void logoutViaMenu() {
        // Logout using File menu
        clickOn("File");
        waitFx();
        clickOn("Logout");
        waitFx();

        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");

        waitFx();
        assertFalse(SessionState.getInstance().isLoggedIn(), "Logout should end session.");
        verifyThat("Login", isVisible());
    }

    private void waitFx() {
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();
    }

    private void typeInto(TextInputControl field, String value) {
        clickOn(field);
        field.clear();
        write(value);
        waitFx();
    }

    private boolean isCategoryPresent(String name) {
        clickOn("Manage Categories");
        waitFx();
        boolean present = lookup(name).tryQuery().isPresent();
        Pane p = getTopDialogPane();
        clickButtonInside(p, "Close");
        waitFx();
        return present;
    }

    private boolean isSupplierPresent(String name) {
        clickOn("Manage Suppliers");
        waitFx();
        boolean present = lookup(name).tryQuery().isPresent();
        Pane p = getTopDialogPane();
        clickButtonInside(p, "Close");
        waitFx();
        return present;
    }

    private String createCategoryForThisTest() {
        String name = "Cat_" + UUID.randomUUID().toString().substring(0, 6);

        clickOn("Manage Categories");
        waitFx();
        Pane dialogPane = getTopDialogPane();

        List<TextField> fields = textFieldsInside(dialogPane);
        typeInto(fields.get(0), name);
        typeInto(fields.get(1), "5");
        typeInto(fields.get(2), "Accessories");

        clickButtonInside(dialogPane, "Add Category");
        closeAlertIfPresent();

        clickButtonInside(dialogPane, "Close");
        waitFx();
        return name;
    }

    private String createSupplierForThisTest() {
        String name = "Sup_" + UUID.randomUUID().toString().substring(0, 6);

        clickOn("Manage Suppliers");
        waitFx();
        Pane dialogPane = getTopDialogPane();

        List<TextField> fields = textFieldsInside(dialogPane);
        typeInto(fields.get(0), name);
        typeInto(fields.get(1), "contact@test.com");

        clickButtonInside(dialogPane, "Add Supplier");
        closeAlertIfPresent();

        clickButtonInside(dialogPane, "Close");
        waitFx();
        return name;
    }

    private Pane getTopDialogPane() {
        // Find the visible dialog pane
        Window top = Window.getWindows().stream()
                .filter(Window::isShowing)
                .reduce((a, b) -> b)
                .orElse(null);

        assertNotNull(top, "No window is showing.");

        Set<Node> panes = lookup(n ->
                n.isVisible()
                        && n.getStyleClass() != null
                        && n.getStyleClass().contains("dialog-pane")
        ).queryAll();

        for (Node n : panes) {
            if (n.getScene() != null && n.getScene().getWindow() == top && n instanceof Pane p) {
                return p;
            }
        }

        Optional<Node> any = panes.stream().findFirst();
        assertTrue(any.isPresent(), "Could not find any visible dialog-pane node.");
        return (Pane) any.get();
    }

    private List<TextField> textFieldsInside(Pane dialogPane) {
        Set<Node> nodes = dialogPane.lookupAll(".text-field");
        List<TextField> fields = nodes.stream()
                .filter(n -> n instanceof TextField && n.isVisible())
                .map(n -> (TextField) n)
                .sorted(Comparator
                        .comparingDouble((TextField t) -> t.localToScene(t.getBoundsInLocal()).getMinY())
                        .thenComparingDouble(t -> t.localToScene(t.getBoundsInLocal()).getMinX()))
                .collect(Collectors.toList());
        return fields;
    }

    private void clickButtonInside(Pane dialogPane, String text) {
        Set<Node> nodes = dialogPane.lookupAll(".button");
        for (Node n : nodes) {
            if (n instanceof Button b && b.isVisible() && text.equals(b.getText())) {
                clickOn(b);
                waitFx();
                return;
            }
        }
        fail("Button '" + text + "' not found inside dialog.");
    }

    private void registerUser(String username, String password, String role, String sectorOrNull) {
        // Register user from UI
        goToRegisterFromLogin();

        setTextByPrompt("Enter username", username);
        setTextByPrompt("Enter password", password);
        setTextByPrompt("Confirm your password", password);
        setTextByPrompt("Enter your full name", "System Test " + role);
        setTextByPrompt("Enter your email", username + "@test.com");
        setTextByPrompt("Enter your phone number", "0690000000");

        selectComboValue(role);

        if (sectorOrNull != null) {
            if (lookup((n) -> n instanceof TextInputControl t
                    && "Enter sector".equals(t.getPromptText())
                    && n.isVisible()).tryQuery().isPresent()) {
                setTextByPrompt("Enter sector", sectorOrNull);
            }
        }

        clickButton("Register");
        closeAlertIfPresent();
        waitFx();
    }
}
