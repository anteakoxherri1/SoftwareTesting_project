package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.ReportController;
import com.electronicstore.controller.UserManagementController;
import com.electronicstore.model.users.User;
import com.electronicstore.view.components.SalesChart;
import com.electronicstore.view.components.AlertDialog;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AdminDashboard extends BorderPane {

    private static final String STYLE_DASHBOARD_CARD = "dashboard-card";
    private static final String STYLE_CARD_TITLE = "card-title";
    private static final String MONEY_FORMAT = "$%.2f";

    private final App app;
    private final UserManagementController userController;
    private final ReportController reportController;
    private final SalesChart salesChart;
    private TableView<User> usersTable;
    private VBox systemAlertsBox;

    public AdminDashboard(App app) {
        this.app = app;
        this.userController = new UserManagementController();
        this.reportController = new ReportController();
        this.salesChart = new SalesChart();

        getStyleClass().add("dashboard");
        setPadding(new Insets(20));

        initializeComponents();
        refreshDashboard();
    }

    private void initializeComponents() {
        // Top Section - Welcome and Quick Actions
        VBox topSection = createTopSection();
        setTop(topSection);

        // Center Section - Dashboard Cards
        GridPane centerSection = createCenterSection();
        setCenter(centerSection);

        // Right Section - System Alerts and Active Users
        VBox rightSection = createRightSection();
        setRight(rightSection);
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        topSection.getStyleClass().add("dashboard-header");

        // Welcome message
        Label welcomeLabel = new Label("System Administrator Dashboard");
        welcomeLabel.getStyleClass().add("dashboard-title");

        // Quick action buttons
        HBox actionButtons = new HBox(10);

        Button userManagementButton = new Button("User Management");
        userManagementButton.getStyleClass().addAll("button", "button-primary");
        userManagementButton.setOnAction(e -> app.showUserManagement());

        Button reportsButton = new Button("System Reports");
        reportsButton.getStyleClass().addAll("button", "button-info");
        reportsButton.setOnAction(e -> showSystemReports());

        actionButtons.getChildren().addAll(
                userManagementButton,
                reportsButton
        );

        // Date range selector with listeners
        HBox dateFilter = new HBox(10);
        dateFilter.setAlignment(Pos.CENTER_LEFT);

        DatePicker startDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDate = new DatePicker(LocalDate.now());

        // Add change listeners to both date pickers
        startDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && endDate.getValue() != null) {
                if (newValue.isAfter(endDate.getValue())) {
                    startDate.setValue(oldValue);
                    AlertDialog.showError("Invalid Date Range", "Start date cannot be after end date");
                } else {
                    updateDashboard(newValue, endDate.getValue());
                }
            }
        });

        endDate.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null && startDate.getValue() != null) {
                if (newValue.isBefore(startDate.getValue())) {
                    endDate.setValue(oldValue);
                    AlertDialog.showError("Invalid Date Range", "End date cannot be before start date");
                } else {
                    updateDashboard(startDate.getValue(), newValue);
                }
            }
        });

        // Remove the Apply button since we now update automatically
        dateFilter.getChildren().addAll(
                new Label("From:"), startDate,
                new Label("To:"), endDate
        );

        topSection.getChildren().addAll(welcomeLabel, actionButtons, dateFilter);
        return topSection;
    }

    private GridPane createCenterSection() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 0, 0, 0));

        // System Overview Card
        VBox overviewCard = createOverviewCard();
        grid.add(overviewCard, 0, 0);

        // Financial Summary Card
        VBox financialCard = createFinancialCard();
        grid.add(financialCard, 1, 0);

        // Sales Trend Chart
        VBox chartCard = new VBox(10);
        chartCard.getStyleClass().add(STYLE_DASHBOARD_CARD);
        chartCard.getChildren().add(salesChart);
        grid.add(chartCard, 0, 1, 2, 1);

        return grid;
    }

    private VBox createOverviewCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add(STYLE_DASHBOARD_CARD);

        Label title = new Label("System Overview");
        title.getStyleClass().add(STYLE_CARD_TITLE);

        GridPane stats = new GridPane();
        stats.setHgap(20);
        stats.setVgap(10);

        // Add stats
        addStat(stats, "Total Users", "0", 0, 0);
        addStat(stats, "Active Sessions", "0", 0, 1);
        addStat(stats, "Total Products", "0", 1, 0);
        addStat(stats, "Active Sectors", "0", 1, 1);

        card.getChildren().addAll(title, stats);
        return card;
    }

    private VBox createFinancialCard() {
        VBox card = new VBox(10);
        card.getStyleClass().add(STYLE_DASHBOARD_CARD);

        Label title = new Label("Financial Summary");
        title.getStyleClass().add(STYLE_CARD_TITLE);

        GridPane stats = new GridPane();
        stats.setHgap(20);
        stats.setVgap(10);

        // Add financial stats
        addStat(stats, "Total Revenue", "$0", 0, 0);
        addStat(stats, "Total Expenses", "$0", 0, 1);
        addStat(stats, "Net Profit", "$0", 1, 0);
        addStat(stats, "Growth Rate", "0%", 1, 1);

        card.getChildren().addAll(title, stats);
        return card;
    }

    private void addStat(GridPane grid, String label, String value, int row, int col) {
        VBox statBox = new VBox(5);
        Label statLabel = new Label(label);
        statLabel.getStyleClass().add("stat-label");
        Text statValue = new Text(value);
        statValue.getStyleClass().add("stat-value");
        statBox.getChildren().addAll(statLabel, statValue);
        grid.add(statBox, col, row);
    }

    private VBox createRightSection() {
        VBox rightSection = new VBox(15);
        rightSection.setPrefWidth(300);
        rightSection.getStyleClass().add("dashboard-sidebar");

        // System Alerts
        Label alertsTitle = new Label("System Alerts");
        alertsTitle.getStyleClass().add(STYLE_CARD_TITLE);

        systemAlertsBox = new VBox(10);
        systemAlertsBox.getStyleClass().add("alerts-container");
        ScrollPane alertsScroll = new ScrollPane(systemAlertsBox);
        alertsScroll.setFitToWidth(true);
        alertsScroll.getStyleClass().add("alerts-scroll");

        // Active Users Table
        Label usersTitle = new Label("Active Users");
        usersTitle.getStyleClass().add(STYLE_CARD_TITLE);

        usersTable = createUsersTable();
        ScrollPane usersScroll = new ScrollPane(usersTable);
        usersScroll.setFitToWidth(true);
        usersScroll.getStyleClass().add("users-scroll");

        rightSection.getChildren().addAll(
                alertsTitle, alertsScroll,
                usersTitle, usersScroll
        );

        return rightSection;
    }

    private TableView<User> createUsersTable() {
        TableView<User> table = new TableView<>();

        TableColumn<User, String> nameCol = new TableColumn<>("Name");
        nameCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getName()));

        TableColumn<User, String> roleCol = new TableColumn<>("Role");
        roleCol.setCellValueFactory(data -> new javafx.beans.property.SimpleStringProperty(
                data.getValue().getClass().getSimpleName()));

        table.getColumns().addAll(nameCol, roleCol);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        return table;
    }

    private void refreshDashboard() {
        updateSystemAlerts();
        updateActiveUsers();
        updateStatistics();
        updateSalesChart();
    }

    private void updateDashboard(LocalDate start, LocalDate end) {
        try {
            Map<String, Double> financialSummary = reportController.getFinancialSummary(start, end);

            updateFinancialCard(financialSummary);

            Map<LocalDate, Double> salesData = reportController.getMonthlySalesReport(
                    start.getYear(), start.getMonthValue());
            salesChart.showDailySales(salesData);

            updateStatistics();

            updateSystemAlerts();

            List<String> recentActivities = reportController.getRecentActivities(start, end);
            displayRecentActivities(recentActivities);
        } catch (Exception e) {
            AlertDialog.showError("Update Error", "Failed to update dashboard: " + e.getMessage());
        }
    }

    private void displayRecentActivities(List<String> recentActivities) {
        StringBuilder activityLog = new StringBuilder();
        for (String activity : recentActivities) {
            activityLog.append(activity).append("\n");
        }
        showReportDialog("Recent Activities", activityLog.toString());
    }

    private void updateFinancialCard(Map<String, Double> summary) {
        // Find all stat values in the financial card and update them
        GridPane statsGrid = (GridPane) getFinancialCard().getChildren().get(1);

        updateStatValue(statsGrid, "Total Revenue",
                String.format(MONEY_FORMAT, summary.getOrDefault("totalRevenue", 0.0)), 0, 0);
        updateStatValue(statsGrid, "Total Expenses",
                String.format(MONEY_FORMAT, summary.getOrDefault("totalCost", 0.0)), 0, 1);
        updateStatValue(statsGrid, "Net Profit",
                String.format(MONEY_FORMAT, summary.getOrDefault("grossProfit", 0.0)), 1, 0);

        // Calculate growth rate
        double previousPeriod = summary.getOrDefault("previousPeriodRevenue", 0.0);
        double currentPeriod = summary.getOrDefault("totalRevenue", 0.0);
        double growthRate = previousPeriod > 0 ?
                ((currentPeriod - previousPeriod) / previousPeriod) * 100 : 0.0;

        updateStatValue(statsGrid, "Growth Rate",
                String.format("%.1f%%", growthRate), 1, 1);
    }

    private void updateStatistics() {
        GridPane statsGrid = (GridPane) getOverviewCard().getChildren().get(1);

        // Get users statistics
        List<User> users = userController.getAllUsers();
        updateStatValue(statsGrid, "Total Users", String.valueOf(users.size()), 0, 0);

        // Get active sessions (assuming this is tracked somewhere)
        int activeSessions = users.size();
        updateStatValue(statsGrid, "Active Sessions", String.valueOf(activeSessions), 0, 1);

        // Get inventory statistics
        int totalProducts = reportController.getTotalProducts();
        updateStatValue(statsGrid, "Total Products", String.valueOf(totalProducts), 1, 0);

        // Get sector statistics
        int activeSectors = reportController.getActiveSectors();
        updateStatValue(statsGrid, "Active Sectors", String.valueOf(activeSectors), 1, 1);
    }

    private void updateSystemAlerts() {
        systemAlertsBox.getChildren().clear();

        // Check for low stock items
        List<String> lowStockAlerts = reportController.getLowStockAlerts();
        lowStockAlerts.forEach(alert -> addAlert("Low Stock", alert, "warning"));
    }

    private void addAlert(String type, String message, String severity) {
        VBox alertBox = new VBox(5);
        alertBox.getStyleClass().addAll("alert", "alert-" + severity);

        Label typeLabel = new Label(type);
        typeLabel.getStyleClass().add("alert-type");

        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);

        alertBox.getChildren().addAll(typeLabel, messageLabel);
        systemAlertsBox.getChildren().add(alertBox);
    }

    private void updateActiveUsers() {
        List<User> users = userController.getAllUsers();
        usersTable.getItems().setAll(users);
    }

    private void updateSalesChart() {
        LocalDate end = LocalDate.now();
        LocalDate start = end.minusMonths(1);

        try {
            Map<LocalDate, Double> currentPeriodSales = reportController.getMonthlySalesReport(
                    end.getYear(), end.getMonthValue());

            Map<LocalDate, Double> previousPeriodSales = reportController.getMonthlySalesReport(
                    start.getYear(), start.getMonthValue());

            Map<String, Double> currentPeriodSalesString = convertMap(currentPeriodSales);
            Map<String, Double> previousPeriodSalesString = convertMap(previousPeriodSales);

            salesChart.compareSales(currentPeriodSalesString, previousPeriodSalesString);

            // Set appropriate labels
            salesChart.setAxisLabels("Date", "Sales Amount ($)");
            salesChart.setChartTitle("Monthly Sales Comparison");

        } catch (Exception e) {
            AlertDialog.showError("Chart Error", "Failed to update sales chart: " + e.getMessage());
        }
    }

    private Map<String, Double> convertMap(Map<LocalDate, Double> sales) {
        Map<String, Double> salesString = new HashMap<>();
        for (Map.Entry<LocalDate, Double> entry : sales.entrySet()) {
            salesString.put(entry.getKey().toString(), entry.getValue());
        }
        return salesString;
    }

    private void showSystemReports() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("System Reports");
        dialog.setHeaderText("Generate System Reports");

        // Create the report type combo box
        ComboBox<String> reportType = new ComboBox<>();
        reportType.getItems().addAll(
                "Sales Report",
                "Inventory Report",
                "Low Stock Report",
                "Profit Report"
        );
        reportType.setValue("Sales Report");

        // Create date pickers
        DatePicker startDate = new DatePicker(LocalDate.now().minusMonths(1));
        DatePicker endDate = new DatePicker(LocalDate.now());

        // Create the layout
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Report Type:"), 0, 0);
        grid.add(reportType, 1, 0);
        grid.add(new Label("Start Date:"), 0, 1);
        grid.add(startDate, 1, 1);
        grid.add(new Label("End Date:"), 0, 2);
        grid.add(endDate, 1, 2);

        // Add buttons
        ButtonType generateButtonType = new ButtonType("Generate", ButtonBar.ButtonData.OK_DONE);
        ButtonType exportButtonType = new ButtonType("Export", ButtonBar.ButtonData.APPLY);
        dialog.getDialogPane().getButtonTypes().addAll(generateButtonType, exportButtonType, ButtonType.CANCEL);

        dialog.getDialogPane().setContent(grid);

        // Handle the button actions
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == generateButtonType || dialogButton == exportButtonType) {
                String report = reportController.generateReport(
                        reportType.getValue(),
                        startDate.getValue(),
                        endDate.getValue()
                );

                if (dialogButton == exportButtonType) {
                    boolean exported = reportController.exportReport(report, reportType.getValue());
                    if (exported) {
                        AlertDialog.showInfo("Export Successful", "Report has been exported successfully");
                    } else {
                        AlertDialog.showError("Export Failed", "Failed to export report");
                    }
                }

                return report;
            }
            return null;
        });

        // Show the dialog and handle the result
        dialog.showAndWait().ifPresent(report -> {
            if (report != null && !report.isEmpty()) {
                showReportDialog(reportType.getValue(), report);
            }
        });
    }

    private void showReportDialog(String title, String content) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle(title);
        dialog.setHeaderText(null);

        TextArea textArea = new TextArea(content);
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefRowCount(20);
        textArea.setPrefColumnCount(50);

        dialog.getDialogPane().setContent(textArea);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void updateStatValue(GridPane grid, String label, String newValue, int row, int col) {
        VBox statBox = (VBox) getNodeFromGridPane(grid, col, row);
        if (statBox != null) {
            Text valueText = (Text) statBox.getChildren().get(1);
            valueText.setText(newValue);
        }
    }

    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        for (Node node : gridPane.getChildren()) {
            if (GridPane.getColumnIndex(node) == col && GridPane.getRowIndex(node) == row) {
                return node;
            }
        }
        return null;
    }

    private VBox getFinancialCard() {
        GridPane centerSection = (GridPane) getCenter();
        return (VBox) centerSection.getChildren().get(1);
    }

    private VBox getOverviewCard() {
        GridPane centerSection = (GridPane) getCenter();
        return (VBox) centerSection.getChildren().get(0);
    }
}
