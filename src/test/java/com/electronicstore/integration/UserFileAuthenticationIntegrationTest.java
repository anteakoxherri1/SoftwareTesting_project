package com.electronicstore.integration;

import com.electronicstore.model.users.User;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.utils.FileHandler;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class UserFileAuthenticationIntegrationTest {

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
        FileHandler.saveListToFile(testFile, users);
        List<User> loadedUsers = FileHandler.readListFromFile(testFile);

        // THEN
        assertNotNull(loadedUsers);
        assertEquals(1, loadedUsers.size());
        assertEquals("testuser", loadedUsers.get(0).getUsername());
    }

@Test
void readUsers_whenFileDoesNotExist_returnsEmptyList() {
   
    String nonExistingFile = "non_existing_users.dat";

   
    List<User> users = FileHandler.readListFromFile(nonExistingFile);

   
    assertNotNull(users, "Returned list should not be null");
    assertTrue(users.isEmpty(), "List should be empty when file does not exist");
}
}