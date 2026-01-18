package com.electronicstore.ui.systemtest;

import com.electronicstore.App;
import javafx.application.Platform;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextInputControl;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.testfx.framework.junit5.ApplicationTest;
import org.testfx.util.WaitForAsyncUtils;

import java.util.UUID;

public abstract class BaseFrontendSystemTest extends ApplicationTest {

    protected App app;
    protected Stage primaryStage;

    @Override
    public void start(Stage stage) throws Exception {
        // Keep JavaFX running during tests
        Platform.setImplicitExit(false);

        this.primaryStage = stage;

        // Don’t let closing the window stop the test run
        stage.setOnCloseRequest(e -> e.consume());

        app = new App();
        app.start(stage);

        // Bring the app window in front (Eclipse sometimes hides it)
        Platform.runLater(() -> {
            stage.show();
            stage.toFront();
            stage.requestFocus();
            stage.setAlwaysOnTop(true);
            stage.setAlwaysOnTop(false);
        });

        WaitForAsyncUtils.waitForFxEvents();
    }

    @BeforeEach
    void resetToLogin() {
        // Always start each test from the login screen
        interact(() -> app.showLoginScreen());
        WaitForAsyncUtils.waitForFxEvents();
    }

    @AfterEach
    void afterEach() {
        // Make sure all UI events are finished
        WaitForAsyncUtils.waitForFxEvents();
    }

    // ---------- helpers ----------

    protected TextInputControl inputByPrompt(String promptText) {
        return lookup((Node n) ->
                (n instanceof TextInputControl t) && promptText.equals(t.getPromptText())
        ).query();
    }

    protected void setTextByPrompt(String promptText, String value) {
        TextInputControl field = inputByPrompt(promptText);
        clickOn(field);
        field.clear();
        write(value);
    }

    protected void clickButton(String text) {
        clickOn(text);
        WaitForAsyncUtils.waitForFxEvents();
    }

    protected void closeAlertIfPresent() {
        // Close common popup buttons if they show up
        if (lookup("OK").tryQuery().isPresent()) clickOn("OK");
        else if (lookup("Close").tryQuery().isPresent()) clickOn("Close");
        else if (lookup("Cancel").tryQuery().isPresent()) clickOn("Cancel");
        WaitForAsyncUtils.waitForFxEvents();
    }

    protected void selectComboValue(String value) {
        ComboBox<?> combo = lookup((Node n) -> n instanceof ComboBox<?> && n.isVisible()).query();
        clickOn(combo);
        clickOn(value);
        WaitForAsyncUtils.waitForFxEvents();
    }

    protected String uniqueUser(String prefix) {
        // Helps avoid duplicate usernames between test runs
        return prefix + "_" + UUID.randomUUID().toString().substring(0, 8);
    }

    protected void goToRegisterFromLogin() {
        clickOn("Don't have an account? Register");
        WaitForAsyncUtils.waitForFxEvents();
    }

    protected void registerCashierUser(String username, String password) {
        // Quick register flow for a cashier user
        goToRegisterFromLogin();

        setTextByPrompt("Enter username", username);
        setTextByPrompt("Enter password", password);
        setTextByPrompt("Confirm your password", password);
        setTextByPrompt("Enter your full name", "Test Cashier");
        setTextByPrompt("Enter your email", "cashier@test.com");
        setTextByPrompt("Enter your phone number", "0690000000");

        selectComboValue("Cashier");
        setTextByPrompt("Enter sector", "FrontDesk");

        clickButton("Register");
        closeAlertIfPresent();

        WaitForAsyncUtils.waitForFxEvents();
    }

    protected void login(String username, String password) {
        // Login helper
        setTextByPrompt("Enter your username", username);
        setTextByPrompt("Enter your password", password);
        clickButton("Login");
        WaitForAsyncUtils.waitForFxEvents();
    }
}
