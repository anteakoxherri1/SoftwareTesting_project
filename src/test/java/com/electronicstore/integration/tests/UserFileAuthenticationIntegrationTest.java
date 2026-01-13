package com.electronicstore.integration.tests;

import com.electronicstore.model.users.User;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserFileAuthenticationIntegrationTest {

    @Test
    void saveAndReadUsers_successfully() {
        // GIVEN
        String testFile = "test_users.dat";
        List<User> users = new ArrayList<>();

        User user = new Cashier(
                "U-TEST-1",
                "testuser",
                "password",
                "Test User",
                "test@mail.com",
                "123456789",
                "Sales"
        );

        users.add(user);

        // WHEN
        assertDoesNotThrow(() -> FileHandler.saveListToFile(users, testFile));

        List<User> loadedUsers = assertDoesNotThrow(() ->
                FileHandler.readListFromFile(testFile)
        );

        // THEN
        assertNotNull(loadedUsers);
        assertEquals(1, loadedUsers.size());
        assertEquals("testuser", loadedUsers.get(0).getUsername());
    }

    @Test
    void readUsers_whenFileDoesNotExist_returnsEmptyList() {
        // GIVEN
        String nonExistingFile = "non_existing_users.dat";

        // WHEN
        List<User> users = new ArrayList<>();
        try {
            users = FileHandler.readListFromFile(nonExistingFile);
        } catch (Exception e) {
            // expected → file does not exist
        }

        // THEN
        assertNotNull(users);
        assertTrue(users.isEmpty());
    }
}







