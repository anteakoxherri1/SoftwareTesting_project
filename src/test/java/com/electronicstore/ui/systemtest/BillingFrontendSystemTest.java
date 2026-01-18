package com.electronicstore.ui.systemtest;

import com.electronicstore.controller.InventoryController;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.users.Manager;
import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Spinner;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextInputControl;
import javafx.scene.input.KeyCode;
import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import java.io.IOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class BillingFrontendSystemTest extends BaseFrontendSystemTest {

    private static final String ADMIN_USER = "admin1";
    private static final String ADMIN_PASS = "1234";

    private static final String MANAGER_USER = "mgr1";
    private static final String MANAGER_PASS = "1111";

    private static final String CASHIER_USER = "cash1";
    private static final String CASHIER_PASS = "2222";

    private final SessionState session = SessionState.getInstance();

    @BeforeEach
    void cleanAndSeed() throws IOException {
        // Clean saved files so test starts fresh
        deleteDirectoryIfExists(Paths.get(FileHandler.DATA_DIRECTORY));
        Files.createDirectories(Paths.get(FileHandler.DATA_DIRECTORY));
        Files.createDirectories(Paths.get(FileHandler.DATA_DIRECTORY).resolve("bills"));

        session.endSession();

        // Create users + inventory for UI tests
        seedUsersBackend();
        seedInventoryBackend();

        interact(() -> app.showLoginScreen());
        waitFx();
    }

    // =========================
    // TEST 1: normal sale flow
    // =========================
    @Test
    void sell_and_save_bill() {
        login(CASHIER_USER, CASHIER_PASS);
        waitFx();

        // Open bill screen
        openNewBillFromCashierDashboard();
        verifyThat("Generate New Bill", isVisible());

        // Add item
        selectBillItemByName("Wireless Mouse M185");
        setVisibleSpinnerValue(2);
        clickButton("Add to Bill");

        // Finalize bill
        clickButton("Finalize Bill");
        clickConfirmButtonIfPresent();

        assertWindowWithTitleShowing("Success");
        closeAlertIfPresentOrCloseButtons();

        // Back on dashboard
        verifyThat("New Bill", isVisible());

        // Open receipt window and check it has basic info
        clickOn("View Details");
        waitFx();

        assertWindowWithTitleShowing("Bill Details");

        TextArea receipt = lookup(n -> n instanceof TextArea).query();
        String receiptText = receipt.getText();

        assertTrue(receiptText.contains("Wireless Mouse M185"));
        assertTrue(receiptText.contains("Total Amount"));
        assertTrue(receiptText.contains("$"));

        // Close bill dialog
        clickOn("OK");
        waitFx();

        // Logout
        logoutViaMenu();

        // Backend check: stock should be updated (10 -> 8)
        session.startSession(new Manager(
                "U200",
                MANAGER_USER,
                MANAGER_PASS,
                "Inventory Manager",
                "manager@estore.com",
                "0671111111"
        ));

        InventoryController invReload = new InventoryController();
        List<Item> items = invReload.getAllItems();
        assertFalse(items.isEmpty());

        Item reloaded = items.get(0);
        assertEquals(8, reloaded.getStockQuantity());

        session.endSession();
    }

    // =========================
    // TEST 2: empty bill case
    // =========================
    @Test
    void empty_bill_warning() {
        login(CASHIER_USER, CASHIER_PASS);
        waitFx();

        openNewBillFromCashierDashboard();
        clickButton("Finalize Bill");

        // Should show warning because nothing was added
        assertWindowWithTitleShowing("Warning");
        closeAlertIfPresentOrCloseButtons();

        logoutViaMenu();
    }

    // =========================
    // TEST 3: oversell case
    // =========================
    @Test
    void oversell_error() {
        // Add low stock item (stock=1)
        seedLowStockItemBackend();

        login(CASHIER_USER, CASHIER_PASS);
        waitFx();

        openNewBillFromCashierDashboard();

        // Try to add more than stock
        selectBillItemByName("HDMI Cable 2m");
        setVisibleSpinnerValue(5);
        clickButton("Add to Bill");

        // Should show error
        assertWindowWithTitleShowing("Error");
        closeAlertIfPresentOrCloseButtons();

        logoutViaMenu();
    }

    // =====================================================
    // UI helpers
    // =====================================================
    private void openNewBillFromCashierDashboard() {
        verifyThat("New Bill", isVisible());
        clickOn("New Bill");
        waitFx();
    }

    private void logoutViaMenu() {
        clickOn("File");
        waitFx();
        clickOn("Logout");
        waitFx();
        clickConfirmButtonIfPresent();
        verifyThat("Login", isVisible());
    }

    private void selectBillItemByName(String itemName) {
        @SuppressWarnings("unchecked")
        ComboBox<Item> combo = (ComboBox<Item>)
                lookup(n -> n instanceof ComboBox<?> && n.isVisible()).query();

        interact(() -> {
            Item found = combo.getItems().stream()
                    .filter(i -> itemName.equals(i.getName()))
                    .findFirst()
                    .orElseThrow();
            combo.getSelectionModel().select(found);
        });

        waitFx();
    }

    private void setVisibleSpinnerValue(int value) {
        Spinner<?> spinner = lookup(n -> n instanceof Spinner<?> && n.isVisible()).query();
        TextInputControl editor = (TextInputControl) spinner.lookup(".text-field");
        clickOn(editor);
        editor.clear();
        write(String.valueOf(value));
        push(KeyCode.ENTER);
        waitFx();
    }

    // =====================================================
    // Alert helpers
    // =====================================================
    private void assertWindowWithTitleShowing(String title) {
        long deadline = System.currentTimeMillis() + 3000;

        while (System.currentTimeMillis() < deadline) {
            for (Window w : Window.getWindows()) {
                if (w.isShowing() && w instanceof Stage s && title.equals(s.getTitle())) {
                    return;
                }
            }
            waitFx();
        }
        fail("Expected window with title: " + title);
    }

    private void clickConfirmButtonIfPresent() {
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");
        else if (lookup("Confirm").tryQuery().isPresent()) clickOn("Confirm");
        waitFx();
    }

    private void closeAlertIfPresentOrCloseButtons() {
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Close").tryQuery().isPresent()) clickOn("Close");
        waitFx();
    }

    private void waitFx() {
        WaitForAsyncUtils.waitForFxEvents();
    }

    // =====================================================
    // Backend seeding
    // =====================================================
    private void seedUsersBackend() {
        UserManagementController users = new UserManagementController();

        users.addUser(ADMIN_USER, ADMIN_PASS,
                "System Admin", "admin@estore.com", "0670000000",
                "administrator", null);

        users.addUser(MANAGER_USER, MANAGER_PASS,
                "Inventory Manager", "manager@estore.com", "0671111111",
                "manager", null);

        users.addUser(CASHIER_USER, CASHIER_PASS,
                "Front Desk Cashier", "cashier@estore.com", "0672222222",
                "cashier", "Electronics");
    }

    private void seedInventoryBackend() {
        session.startSession(new Manager(
                "U200", MANAGER_USER, MANAGER_PASS,
                "Inventory Manager", "manager@estore.com", "0671111111"));

        InventoryController inv = new InventoryController();
        Category accessories = new Category("C1", "Accessories", 3, "Electronics");
        Supplier logitech = new Supplier("S1", "Logitech", "support@logitech.com");

        inv.addItem("Wireless Mouse M185", accessories, logitech, 10.00, 15.00, 10);
        session.endSession();
    }

    private void seedLowStockItemBackend() {
        session.startSession(new Manager(
                "U200", MANAGER_USER, MANAGER_PASS,
                "Inventory Manager", "manager@estore.com", "0671111111"));

        InventoryController inv = new InventoryController();
        Category cables = new Category("C2", "Cables", 2, "Electronics");
        Supplier samsung = new Supplier("S2", "Samsung", "support@samsung.com");

        inv.addItem("HDMI Cable 2m", cables, samsung, 3.00, 7.00, 1);
        session.endSession();
    }

    private static void deleteDirectoryIfExists(Path dir) throws IOException {
        if (!Files.exists(dir)) return;

        Files.walk(dir)
                .sorted(Comparator.reverseOrder())
                .forEach(p -> {
                    try { Files.deleteIfExists(p); }
                    catch (IOException ignored) {}
                });
    }
}
