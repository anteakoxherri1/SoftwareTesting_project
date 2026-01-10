package com.electronicstore.view.screens;

import com.electronicstore.App;
import com.electronicstore.controller.BillingController;
import com.electronicstore.model.sales.Bill;
import com.electronicstore.view.components.SalesChart;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.*;
import javafx.scene.text.Text;
import java.time.LocalDate;

import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class CashierDashboard extends BorderPane {
    private final App app;
    private final BillingController billingController;
    private final SalesChart salesChart;
    private VBox todaysBillsBox;
    private Text billCountText;
    private Text totalSalesText;
    private Text averageTicketText;
    private Text peakTimeText;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm");

    public CashierDashboard(App app) {
        this.app = app;
        this.billingController = new BillingController();
        this.salesChart = new SalesChart();

        getStyleClass().add("dashboard");
        getStylesheets().add(getClass().getResource("/styles/dashboard.css").toExternalForm());
        setPadding(new Insets(20));

        initializeComponents();
        refreshDashboard();

        // Set up auto-refresh timer (every 5 minutes)
        javafx.animation.Timeline timeline = new javafx.animation.Timeline(
                new javafx.animation.KeyFrame(
                        javafx.util.Duration.minutes(5),
                        event -> refreshDashboard()
                )
        );
        timeline.setCycleCount(javafx.animation.Animation.INDEFINITE);
        timeline.play();
    }

    private void initializeComponents() {
        // Top Section - Welcome and Quick Actions
        VBox topSection = createTopSection();
        setTop(topSection);

        // Center Section - Dashboard Cards
        GridPane centerSection = createCenterSection();
        setCenter(centerSection);

        // Right Section - Today's Bills
        VBox rightSection = createRightSection();
        setRight(rightSection);
    }

    private VBox createTopSection() {
        VBox topSection = new VBox(15);
        topSection.getStyleClass().add("dashboard-header");
        topSection.setPadding(new Insets(0, 0, 20, 0));

        // Welcome message with current date
        HBox welcomeBox = new HBox(20);
        welcomeBox.setAlignment(Pos.CENTER_LEFT);

        VBox welcomeTexts = new VBox(5);
        Label welcomeLabel = new Label("Welcome, " + app.getCurrentUser().getName());
        welcomeLabel.getStyleClass().add("dashboard-title");
        Label dateLabel = new Label(LocalDate.now().format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")));
        dateLabel.getStyleClass().add("date-label");
        welcomeTexts.getChildren().addAll(welcomeLabel, dateLabel);

        // Quick action buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_RIGHT);
        actionButtons.setPrefWidth(USE_COMPUTED_SIZE);
        HBox.setHgrow(actionButtons, Priority.ALWAYS);

        Button newBillButton = new Button("New Bill");
        newBillButton.getStyleClass().addAll("button", "button-primary");
        //getClass().getResourceAsStream("/icons/receipt.png") -- resize
        Image image = new Image(getClass().getResourceAsStream("/icons/receipt.png"));
        //resize image
        javafx.scene.image.ImageView imageView = new javafx.scene.image.ImageView(image);
        imageView.setFitHeight(20);
        imageView.setFitWidth(20);
        newBillButton.setGraphic(imageView);
        newBillButton.setOnAction(e -> app.showBillGeneration());

        Button refreshButton = new Button("Refresh");
        refreshButton.getStyleClass().addAll("button", "button-secondary");
        refreshButton.setOnAction(e -> refreshDashboard());

        actionButtons.getChildren().addAll(newBillButton, refreshButton);
        welcomeBox.getChildren().addAll(welcomeTexts, actionButtons);

        topSection.getChildren().add(welcomeBox);
        return topSection;
    }

    private GridPane createCenterSection() {
        GridPane grid = new GridPane();
        grid.setHgap(20);
        grid.setVgap(20);
        grid.setPadding(new Insets(20, 0, 0, 0));

        // Today's Summary Card
        VBox summaryCard = new VBox(15);
        summaryCard.getStyleClass().add("dashboard-card");
        summaryCard.setPadding(new Insets(20));

        Label summaryTitle = new Label("Today's Summary");
        summaryTitle.getStyleClass().add("card-title");

        // Initialize summary texts
        billCountText = createMetricText();
        totalSalesText = createMetricText();
        averageTicketText = createMetricText();
        peakTimeText = createMetricText();


        // Arrange metrics in a grid
        GridPane metricsGrid = new GridPane();
        metricsGrid.setHgap(30);
        metricsGrid.setVgap(15);
        metricsGrid.add(billCountText, 0, 0);
        metricsGrid.add(totalSalesText, 1, 0);
        metricsGrid.add(averageTicketText, 0, 1);
        metricsGrid.add(peakTimeText, 1, 1);

        summaryCard.getChildren().addAll(summaryTitle, metricsGrid);

        // Sales Chart Card
        VBox chartCard = new VBox(10);
        chartCard.getStyleClass().add("dashboard-card");
        chartCard.setPadding(new Insets(20));

        Label chartTitle = new Label("Sales Timeline");
        chartTitle.getStyleClass().add("card-title");
        chartCard.getChildren().addAll(chartTitle, salesChart);

        // Add cards to grid
        grid.add(summaryCard, 0, 0);
        grid.add(chartCard, 0, 1);

        return grid;
    }

    private Text createMetricText() {
        Text text = new Text();
        text.getStyleClass().add("metric-text");
        text.setStyle("-fx-font-size: 14px;");
        return text;
    }

    private VBox createRightSection() {
        VBox rightSection = new VBox(15);
        rightSection.setPrefWidth(300);
        rightSection.getStyleClass().add("dashboard-sidebar");
        rightSection.setPadding(new Insets(0, 0, 0, 20));

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        Label billsTitle = new Label("Today's Bills");
        billsTitle.getStyleClass().add("section-title");

        titleBox.getChildren().add(billsTitle);

        todaysBillsBox = new VBox(10);
        todaysBillsBox.getStyleClass().add("bills-container");

        ScrollPane scrollPane = new ScrollPane(todaysBillsBox);
        scrollPane.setFitToWidth(true);
        scrollPane.getStyleClass().add("bills-scroll");

        rightSection.getChildren().addAll(titleBox, scrollPane);
        return rightSection;
    }

    private void updateSalesChart(List<Bill> bills) {
        if (bills.isEmpty()) {
            salesChart.showNoDataMessage("No sales recorded today");
            return;
        }

        // Group bills by hour for the daily chart
        Map<LocalDate, Double> dailySales = bills.stream()
                .collect(Collectors.groupingBy(
                        Bill::getDate,
                        Collectors.summingDouble(Bill::getTotalAmount)
                ));

        salesChart.showDailySales(dailySales);
        salesChart.setChartTitle("Today's Sales Timeline");
        salesChart.setAxisLabels("Time", "Amount ($)");
    }

    private void refreshDashboard() {
        List<Bill> todaysBills = billingController.getDailyBills();

        // Update today's bills list
        todaysBillsBox.getChildren().clear();
        if (todaysBills.isEmpty()) {
            Label emptyLabel = new Label("No bills generated today");
            emptyLabel.getStyleClass().add("empty-state-label");
            todaysBillsBox.getChildren().add(emptyLabel);
        } else {
            todaysBills.forEach(this::createBillCard);
        }

        // Update summary metrics
        updateSummaryMetrics(todaysBills);

        // Update sales chart
        updateSalesChart(todaysBills);
    }

    private void updateSummaryMetrics(List<Bill> bills) {
        int billCount = bills.size();
        double totalSales = bills.stream()
                .mapToDouble(Bill::getTotalAmount)
                .sum();
        double averageTicket = billCount > 0 ? totalSales / billCount : 0;

        // Calculate peak time
        String peakTime = calculatePeakTime(bills);

        billCountText.setText(String.format("Bills Generated%n%d", billCount));
        totalSalesText.setText(String.format("Total Sales%n$%.2f", totalSales));
        averageTicketText.setText(String.format("Average Ticket%n$%.2f", averageTicket));
        peakTimeText.setText(String.format("Peak Time%n%s", peakTime));

    }

    private String calculatePeakTime(List<Bill> bills) {
        if (bills.isEmpty()) return "N/A";

        // Group bills by hour and find the hour with most sales
        Map<Integer, Double> salesByHour = bills.stream()
                .collect(Collectors.groupingBy(
                        bill -> bill.getDateTime().getHour(),
                        Collectors.summingDouble(Bill::getTotalAmount)
                ));

        return salesByHour.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(entry -> String.format("%02d:00", entry.getKey()))
                .orElse("N/A");
    }

    private void createBillCard(Bill bill) {
        VBox billCard = new VBox(8);
        billCard.getStyleClass().add("bill-card");
        billCard.setPadding(new Insets(15));

        HBox header = new HBox(10);
        header.setAlignment(Pos.CENTER_LEFT);

        Label billNumber = new Label("#" + bill.getBillNumber());
        billNumber.getStyleClass().add("bill-number");

        Label time = new Label(bill.getDateTime().format(timeFormatter));
        time.getStyleClass().add("bill-time");
        HBox.setHgrow(time, Priority.ALWAYS);
        time.setAlignment(Pos.CENTER_RIGHT);

        header.getChildren().addAll(billNumber, time);

        Label amount = new Label(String.format("$%.2f", bill.getTotalAmount()));
        amount.getStyleClass().add("bill-amount");

        Label itemCount = new Label(String.format("%d items", bill.getItems().size()));
        itemCount.getStyleClass().add("bill-items");

        Button viewButton = new Button("View Details");
        viewButton.getStyleClass().addAll("button", "button-small");
        viewButton.setMaxWidth(Double.MAX_VALUE);
        viewButton.setOnAction(e -> showBillDetails(bill));

        billCard.getChildren().addAll(header, amount, itemCount, viewButton);
        todaysBillsBox.getChildren().add(billCard);
    }

    private void showBillDetails(Bill bill) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Bill Details");
        dialog.setHeaderText("Bill #" + bill.getBillNumber());

        DialogPane dialogPane = dialog.getDialogPane();
        dialogPane.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        dialogPane.getStyleClass().add("dialog-pane");

        VBox content = new VBox(15);
        content.setPadding(new Insets(20));

        TextArea billText = new TextArea(bill.generatePrintableFormat());
        billText.setEditable(false);
        billText.setWrapText(true);
        billText.getStyleClass().add("bill-text");
        //make 400px height
        billText.setPrefHeight(400);
        billText.setMinHeight(400);

        content.getChildren().add(billText);

        dialogPane.setContent(content);
        dialogPane.getButtonTypes().addAll(ButtonType.OK);

        Button printButton = new Button("Print");
        printButton.getStyleClass().add("button-primary");
        printButton.setOnAction(e -> {
            // Implement print functionality
        });

        dialogPane.getButtonTypes().add(ButtonType.CLOSE);

        dialog.show();
    }
}