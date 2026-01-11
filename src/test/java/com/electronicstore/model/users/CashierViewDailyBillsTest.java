package com.electronicstore.model.users;

import com.electronicstore.model.sales.Bill;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CashierViewDailyBillsTest {

    @Test
    public void hasBillsForToday_returnsTodayBills() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );
        cashier.clearBills();

        Bill today1 = new Bill("B1", cashier.getId(), LocalDateTime.now(), 100);
        Bill today2 = new Bill("B2", cashier.getId(), LocalDateTime.now(), 200);

        cashier.addBill(today1);
        cashier.addBill(today2);

        List<Bill> result = cashier.viewDailyBills();

        assertEquals(2, result.size());
        assertTrue(result.contains(today1));
        assertTrue(result.contains(today2));
    }

    @Test
    public void hasOnlyOldBills_returnsEmptyList() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );
        cashier.clearBills();

        Bill yesterday = new Bill(
                "B3",
                cashier.getId(),
                LocalDate.now().minusDays(1).atTime(10, 0),
                999
        );

        cashier.addBill(yesterday);

        List<Bill> result = cashier.viewDailyBills();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }

    @Test
    public void noBills_returnsEmptyList() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );
        cashier.clearBills();

        List<Bill> result = cashier.viewDailyBills();

        assertNotNull(result);
        assertTrue(result.isEmpty());
    }
}
