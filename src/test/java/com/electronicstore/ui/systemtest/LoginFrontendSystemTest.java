package com.electronicstore.ui.systemtest;

import javafx.stage.Stage;
import javafx.stage.Window;
import org.junit.jupiter.api.Test;
import org.testfx.util.WaitForAsyncUtils;

import static org.junit.jupiter.api.Assertions.fail;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class LoginFrontendSystemTest extends BaseFrontendSystemTest {

    @Test
    void empty_login_shows_error() {
        // Click login with empty fields
        clickButton("Login");

        assertWindowWithTitleShowing("Login Error");
        closeAnyAlert();

        // Should stay on login screen
        verifyThat("Login", isVisible());
    }

    @Test
    void wrong_login_shows_error() {
        // Type wrong username/password
        setTextByPrompt("Enter your username", "wrongUser");
        setTextByPrompt("Enter your password", "wrongPass");
        clickButton("Login");

        assertWindowWithTitleShowing("Login Error");
        closeAnyAlert();

        verifyThat("Login", isVisible());
    }

    @Test
    void register_then_login() {
        String u = uniqueUser("cashier");
        String p = "Pass123!";

        // Register user from UI
        registerCashierUser(u, p);

        // Login from UI
        login(u, p);

        // After login, menu should show
        verifyThat("File", isVisible());
    }

    // ---------- helpers ----------

    private void assertWindowWithTitleShowing(String title) {
        // Window title is not a Node, so we check open Stages
        long deadline = System.currentTimeMillis() + 3000;

        while (System.currentTimeMillis() < deadline) {
            for (Window w : Window.getWindows()) {
                if (!w.isShowing()) continue;

                if (w instanceof Stage s && title.equals(s.getTitle())) {
                    return;
                }
            }

            try { Thread.sleep(50); } catch (InterruptedException ignored) {}
        }

        fail("Expected a showing window with title: " + title);
    }

    private void closeAnyAlert() {
        // Close the alert using common buttons
        WaitForAsyncUtils.waitForFxEvents();

        if (lookup("OK").tryQuery().isPresent()) {
            clickOn("OK");
        } else if (lookup("Close").tryQuery().isPresent()) {
            clickOn("Close");
        } else if (lookup("Cancel").tryQuery().isPresent()) {
            clickOn("Cancel");
        } else {
            fail("Alert is showing but no close button (OK/Close/Cancel) was found.");
        }

        WaitForAsyncUtils.waitForFxEvents();
    }
}
