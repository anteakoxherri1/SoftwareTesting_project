package com.electronicstore.integration.tests;

import com.electronicstore.controller.ReportController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ReportExportIntegrationTest {

    @Test
    void exportReport_executesSuccessfully() {

        // GIVEN
        ReportController reportController = new ReportController();
        String reportContent = "TEST REPORT CONTENT\nTotal Sales: 1000";

        // WHEN
        boolean exported = reportController.exportReport(
                reportContent,
                "Sales Report"
        );

        // THEN
        assertTrue(exported, "Report export should return true");
    }
}
