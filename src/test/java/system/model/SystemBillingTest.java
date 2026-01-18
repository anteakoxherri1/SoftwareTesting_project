package system.model;

import com.electronicstore.App;
import com.electronicstore.model.inventory.Category;
import com.electronicstore.model.inventory.Item;
import com.electronicstore.model.inventory.Supplier;
import com.electronicstore.model.users.Cashier;
import com.electronicstore.model.users.User;
import com.electronicstore.model.utils.FileHandler;
import javafx.scene.control.TableView;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import java.io.File;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * SYSTEM TEST
 * Login (UI) -> New Bill -> Add Item -> Finalize Bill
 */
public class SystemBillingTest extends ApplicationTest {

    private static final String USERNAME = "cashier";
    private static final String PASSWORD = "123";

    @Override
    public void start(Stage stage) {
        App app = new App();
        app.start(stage);
    }

    @BeforeEach
    void setupTestData() throws Exception {

        // -------- USERS --------
        List<User> users;
        try {
            users = FileHandler.readListFromFile("users.dat");
        } catch (Exception e) {
            users = new ArrayList<>();
        }

        if (users.stream().noneMatch(u -> u.getUsername().equals(USERNAME))) {
            users.add(new Cashier(
                    "U1", USERNAME, PASSWORD,
                    "Test Cashier", "c@test.com", "000", "General"
            ));
            FileHandler.saveListToFile(users, "users.dat");
        }

        // -------- ITEMS --------
        List<Item> items;
        try {
            items = FileHandler.readListFromFile("items.dat");
        } catch (Exception e) {
            items = new ArrayList<>();
        }

        if (items.isEmpty()) {
            Category category = new Category("C1", "Electronics", 2, "IT");
            Supplier supplier = new Supplier("S1", "Supplier", "111");

            Item item = new Item(
                    "I1",
                    "Laptop",
                    category,
                    supplier,
                    LocalDate.now(),
                    500,
                    700,
                    10
            );
            items.add(item);
            FileHandler.saveListToFile(items, "items.dat");
        }
    }

    @Test
    void systemTest_CashierCreatesAndFinalizesBill() {

        // -------- LOGIN SCREEN --------
        clickOn(".text-field").write(USERNAME);
        clickOn(".password-field").write(PASSWORD);
        clickOn("Login");

        // -------- MENU --------
        clickOn("New Bill");

        // -------- BILL SCREEN --------
        TableView<?> table = lookup(".table-view").query();
        interact(() -> table.getSelectionModel().select(0));

        // quantity field (nëse ekziston)
        lookup(".text-field").queryAll().stream()
                .skip(1)
                .findFirst()
                .ifPresent(node -> {
                    clickOn(node);
                    write("1");
                });

        clickOn("Add Item");

        // -------- FINALIZE --------
        clickOn("Finalize Bill");

        // -------- VERIFY BACKEND --------
        File billsFile = new File("store_data/bills.dat");
        assertTrue(billsFile.exists(), "bills.dat duhet të ekzistojë");

        File billsDir = new File("store_data/bills");
        File[] exportedBills = billsDir.listFiles((d, name) -> name.endsWith(".txt"));

        assertNotNull(exportedBills);
        assertTrue(exportedBills.length > 0, "Duhet të krijohet file i faturës (.txt)");
    }
}
