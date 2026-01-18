package com.electronicstore.ui.systemtest;

import com.electronicstore.model.utils.FileHandler;
import com.electronicstore.model.utils.SessionState;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AuthFlowFrontendSystemTest extends BaseFrontendSystemTest {

    private static String adminU, adminP;
    private static String mgrU, mgrP;
    private static String cashU, cashP;

    @BeforeAll
    static void cleanUsersFile() throws Exception {
        // Start fresh so register works every time
        Path usersPath = Paths.get(FileHandler.DATA_DIRECTORY).resolve("users.dat");
        Files.deleteIfExists(usersPath);
    }

    // ---------- ADMIN FLOW ----------

    @Test
    @Order(1)
    void admin_register_login_logout() {
        adminU = uniqueUser("admin");
        adminP = "Admin123!";

        // register -> login -> check role -> logout
        registerUser(adminU, adminP, "Administrator", null);
        login(adminU, adminP);

        SessionState session = SessionState.getInstance();
        assertTrue(session.isAdministrator(), "Admin should be logged in as Administrator.");

        // basic menu should show
        verifyThat("File", isVisible());
        verifyThat("Operations", isVisible());
        verifyThat("User Management", isVisible());

        logoutAndWait();
        assertFalse(session.isLoggedIn(), "Session should end after logout.");
    }

    // ---------- MANAGER FLOW ----------

    @Test
    @Order(2)
    void manager_register_login_logout() {
        mgrU = uniqueUser("mgr");
        mgrP = "Mgr123!";

        registerUser(mgrU, mgrP, "Manager", null);
        login(mgrU, mgrP);

        SessionState session = SessionState.getInstance();
        assertTrue(session.isManager(), "Manager should be logged in as Manager.");

        // menu should show
        verifyThat("File", isVisible());
        verifyThat("Operations", isVisible());

        // manager dashboard buttons
        verifyThat("New Item", isVisible());
        verifyThat("Manage Inventory", isVisible());
        verifyThat("Manage Suppliers", isVisible());
        verifyThat("Manage Categories", isVisible());

        logoutAndWait();
        assertFalse(session.isLoggedIn(), "Session should end after logout.");
    }

    // ---------- CASHIER FLOW ----------

    @Test
    @Order(3)
    void cashier_register_login_logout() {
        cashU = uniqueUser("cash");
        cashP = "Cash123!";

        registerUser(cashU, cashP, "Cashier", "Accessories");
        login(cashU, cashP);

        SessionState session = SessionState.getInstance();
        assertTrue(session.isCashier(), "Cashier should be logged in as Cashier.");

        // menu should show
        verifyThat("File", isVisible());
        verifyThat("Operations", isVisible());

        // cashier screen basics
        verifyThat("New Bill", isVisible());
        verifyThat("Today's Bills", isVisible());

        logoutAndWait();
        assertFalse(session.isLoggedIn(), "Session should end after logout.");
    }

    // ---------- Helpers ----------

    private void logoutAndWait() {
        SessionState session = SessionState.getInstance();

        // logout from menu
        clickOn("File");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        clickOn("Logout");
        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // confirm logout (button text depends on alert)
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Yes").tryQuery().isPresent()) clickOn("Yes");
        else if (lookup("Cancel").tryQuery().isPresent()) {
            fail("Logout dialog did not show OK/Yes.");
        }

        org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

        // wait up to ~3 seconds for login screen to come back
        long deadline = System.nanoTime() + TimeUnit.SECONDS.toNanos(3);

        while (System.nanoTime() < deadline) {
            org.testfx.util.WaitForAsyncUtils.waitForFxEvents();

            boolean loggedOut = !session.isLoggedIn();
            boolean loginVisible = lookup("Login").tryQuery().isPresent();

            if (loggedOut && loginVisible) {
                verifyThat("Login", isVisible());
                return;
            }

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        fail("Logout did not complete in time. session=" + session);
    }

    private void registerUser(String username, String password, String role, String sectorOrNull) {
        // simple register flow
        goToRegisterFromLogin();

        setTextByPrompt("Enter username", username);
        setTextByPrompt("Enter password", password);
        setTextByPrompt("Confirm your password", password);
        setTextByPrompt("Enter your full name", "System Test " + role);
        setTextByPrompt("Enter your email", username + "@test.com");
        setTextByPrompt("Enter your phone number", "0690000000");

        selectComboValue(role);

        // sector only for cashier (if field is visible)
        if (sectorOrNull != null) {
            if (lookup((n) -> n instanceof javafx.scene.control.TextInputControl t
                    && "Enter sector".equals(t.getPromptText())
                    && n.isVisible()).tryQuery().isPresent()) {
                setTextByPrompt("Enter sector", sectorOrNull);
            }
        }

        clickButton("Register");
        closeAlertIfPresent();
    }
}
