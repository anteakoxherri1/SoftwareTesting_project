package com.electronicstore.model.users;

import com.electronicstore.model.sales.Bill;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CashierGetTotalSalesTest {

    @Test
    public void noBills_totalIsZero() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );

        cashier.clearBills();
        double total = cashier.getTotalSales();

        assertEquals(0.0, total, 0.0001);
    }

    @Test
    public void oneBillToday_totalEqualsThatBill() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );

        cashier.clearBills();

        Bill bill = new Bill(
                "B1",
                cashier.getId(),
                LocalDateTime.now(),
                150.50
        );

        cashier.addBill(bill);

        assertEquals(150.50, cashier.getTotalSales(), 0.0001);
    }

    @Test
    public void manyBills_onlyTodayAreSummed() {
        Cashier cashier = new Cashier(
                "U1", "c1", "p",
                "Cashier One", "c@x.com", "111", "SectorA"
        );

        cashier.clearBills();

        cashier.addBill(new Bill("B1", cashier.getId(), LocalDateTime.now(), 100));
        cashier.addBill(new Bill("B2", cashier.getId(), LocalDateTime.now(), 200));
        cashier.addBill(new Bill(
                "B3",
                cashier.getId(),
                LocalDate.now().minusDays(1).atTime(10, 0),
                999
        ));

        assertEquals(300.0, cashier.getTotalSales(), 0.0001);
    }
}
