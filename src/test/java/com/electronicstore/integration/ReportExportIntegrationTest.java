package com.electronicstore.integration;

import com.electronicstore.controller.ReportController;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ReportExportIntegrationTest {

    @Test
    void exportReport_executesSuccessfully() {

        // Arrange
        ReportController reportController = new ReportController();
        String reportContent = "TEST REPORT CONTENT\nTotal Sales: 1000";

        // Act
        boolean exported = reportController.exportReport(
                reportContent,
                "Sales Report"
        );

        // Assert
        assertTrue(exported, "Report export should return true");
    }
}
