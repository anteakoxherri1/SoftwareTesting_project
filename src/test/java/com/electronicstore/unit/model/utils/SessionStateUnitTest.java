package com.electronicstore.unit.model.utils;

import com.electronicstore.model.users.Administrator;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.Manager;
import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.SessionState;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/** Purpose: verify session lifecycle, role detection, and session state handling.
 */
class SessionStateUnitTest {

    @AfterEach
    void cleanup() {
        // SessionState is a Singleton, so we must reset it after each test
        SessionState.getInstance().endSession();
    }

    /**Verifies that the system starts with no active session.
     */
    @Test
    void shouldBeLoggedOutByDefault() {
        SessionState session = SessionState.getInstance();

        assertFalse(session.isLoggedIn());
        assertEquals("No active session", session.getSessionInfo());
    }

    /**
     * Verifies that starting a session with a Cashier 
     */
    @Test
    void startSession_shouldSetCashierRoleCorrectly() {
        SessionState session = SessionState.getInstance();
        User cashier = new Cashier("U1", "cash", "1234",
                "Cashier", "c@test.com", "111", "IT");

        session.startSession(cashier);

        assertTrue(session.isLoggedIn());
        assertTrue(session.isCashier());
        assertFalse(session.isManager());
        assertFalse(session.isAdministrator());
    }

    /**
     * Verifies that starting a session with a Manager
     */
    @Test
    void startSession_shouldSetManagerRoleCorrectly() {
        SessionState session = SessionState.getInstance();
        User manager = new Manager("U2", "mgr", "1111",
                "Manager", "m@test.com", "222");

        session.startSession(manager);

        assertTrue(session.isLoggedIn());
        assertTrue(session.isManager());
        assertFalse(session.isCashier());
        assertFalse(session.isAdministrator());
    }

    /**
     * Verifies that starting a session with an Administrator
     */
    @Test
    void startSession_shouldSetAdministratorRoleCorrectly() {
        SessionState session = SessionState.getInstance();
        User admin = new Administrator("A1", "admin", "pass",
                "Admin", "a@test.com", "000");

        session.startSession(admin);

        assertTrue(session.isLoggedIn());
        assertTrue(session.isAdministrator());
        assertFalse(session.isCashier());
        assertFalse(session.isManager());
    }

    /**
     * Verifies that the current section of the session
     * can be updated and retrieved correctly.
     */
    @Test
    void setCurrentSection_shouldUpdateSessionSection() {
        SessionState session = SessionState.getInstance();

        session.setCurrentSection("inventory");

        assertEquals("inventory", session.getCurrentSection());
    }

    /**
     * Verifies that ending a session clears all session-related data.
     */
    @Test
    void endSession_shouldClearSessionState() {
        SessionState session = SessionState.getInstance();
        User cashier = new Cashier("U1", "cash", "1234",
                "Cashier", "c@test.com", "111", "IT");

        session.startSession(cashier);
        session.setCurrentSection("main");

        session.endSession();

        assertFalse(session.isLoggedIn());
        assertNull(session.getCurrentUser());
        assertNull(session.getLoginTime());
        assertNull(session.getCurrentSection());
        assertEquals("No active session", session.getSessionInfo());
    }
}
