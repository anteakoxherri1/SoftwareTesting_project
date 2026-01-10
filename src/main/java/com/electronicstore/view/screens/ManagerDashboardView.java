package com.electronicstore.view.screens;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import com.electronicstore.view.components.*;

public class ManagerDashboardView extends BorderPane {
    private Label welcomeLabel;
    private DatePicker startDate;
    private DatePicker endDate;
    private SalesChart salesChart;
    private VBox inventoryCard;
    private VBox alertsBox;
    private Button newItemBtn;
    private Button manageInventoryBtn;
    private Button manageSuppliersBtn;
    private Button manageCategoriesBtn;
    private Button viewReportsBtn;
    private Button applyFilterBtn;
    private Button generateReportBtn;
    private Button exportDataBtn;
    private Label totalItemsLabel;
    private Label totalValueLabel;
    private Label categoriesLabel;
    private Label suppliersLabel;
    private ListView<String> recentActivitiesList;

    public ManagerDashboardView() {
        getStyleClass().add("dashboard");

        setupTopSection();
        setupCenterSection();
        setupRightSection();
    }

    private void setupTopSection() {
        VBox topSection = new VBox(15);
        topSection.getStyleClass().add("dashboard-header");
        topSection.setPadding(new Insets(20, 20, 10, 20));

        // Welcome Label
        welcomeLabel = new Label();
        welcomeLabel.getStyleClass().add("dashboard-title");

        // Action Buttons
        HBox actionButtons = new HBox(10);
        actionButtons.setAlignment(Pos.CENTER_LEFT);

        newItemBtn = createButton("New Item", "button-primary");
        manageInventoryBtn = createButton("Manage Inventory", "button-primary");
        manageSuppliersBtn = createButton("Manage Suppliers", "button-secondary");
        manageCategoriesBtn = createButton("Manage Categories", "button-secondary");
        viewReportsBtn = createButton("View Reports", "button-info");

        actionButtons.getChildren().addAll(
                newItemBtn, manageInventoryBtn, manageSuppliersBtn,
                manageCategoriesBtn, viewReportsBtn
        );

        // Date Filter
        HBox dateFilter = new HBox(10);
        dateFilter.setAlignment(Pos.CENTER_LEFT);

        startDate = new DatePicker();
        endDate = new DatePicker();
        applyFilterBtn = new Button("Apply");

        dateFilter.getChildren().addAll(
                new Label("From:"), startDate,
                new Label("To:"), endDate,
                applyFilterBtn
        );

        topSection.getChildren().addAll(welcomeLabel, actionButtons, dateFilter);
        setTop(topSection);
    }

    private void setupCenterSection() {
        GridPane centerGrid = new GridPane();
        centerGrid.setHgap(20);
        centerGrid.setVgap(20);
        centerGrid.setPadding(new Insets(20));

        // Sales Performance Card
        VBox salesCard = new VBox();
        salesCard.getStyleClass().add("dashboard-card");
        Label salesTitle = new Label("Sales Performance");
        salesTitle.getStyleClass().add("card-title");
        salesChart = new SalesChart();
        salesCard.getChildren().addAll(salesTitle, salesChart);
        GridPane.setConstraints(salesCard, 0, 0);

        // Inventory Stats Card
        inventoryCard = new VBox();
        inventoryCard.getStyleClass().add("dashboard-card");
        Label inventoryTitle = new Label("Inventory Statistics");
        inventoryTitle.getStyleClass().add("card-title");

        VBox inventoryStats = new VBox(10);
        totalItemsLabel = new Label();
        totalValueLabel = new Label();
        categoriesLabel = new Label();
        suppliersLabel = new Label();

        inventoryStats.getChildren().addAll(
                totalItemsLabel, totalValueLabel,
                categoriesLabel, suppliersLabel
        );

        inventoryCard.getChildren().addAll(inventoryTitle, inventoryStats);
        GridPane.setConstraints(inventoryCard, 1, 0);

        // Quick Actions Card
        VBox quickActionsCard = new VBox();
        quickActionsCard.getStyleClass().add("dashboard-card");
        Label quickActionsTitle = new Label("Quick Actions");
        quickActionsTitle.getStyleClass().add("card-title");

        FlowPane actionFlow = new FlowPane(10, 10);
        generateReportBtn = createButton("Generate Report", "button-secondary");
        exportDataBtn = createButton("Export Data", "button-secondary");
        actionFlow.getChildren().addAll(generateReportBtn, exportDataBtn);

        quickActionsCard.getChildren().addAll(quickActionsTitle, actionFlow);
        GridPane.setConstraints(quickActionsCard, 0, 1);

        // Recent Activities Card
        VBox activitiesCard = new VBox();
        activitiesCard.getStyleClass().add("dashboard-card");
        Label activitiesTitle = new Label("Recent Activities");
        activitiesTitle.getStyleClass().add("card-title");

        recentActivitiesList = new ListView<>();
        VBox.setVgrow(recentActivitiesList, Priority.ALWAYS);

        activitiesCard.getChildren().addAll(activitiesTitle, recentActivitiesList);
        GridPane.setConstraints(activitiesCard, 1, 1);

        centerGrid.getChildren().addAll(
                salesCard, inventoryCard,
                quickActionsCard, activitiesCard
        );
        setCenter(centerGrid);
    }

    private void setupRightSection() {
        VBox rightSection = new VBox();
        rightSection.getStyleClass().add("dashboard-sidebar");
        rightSection.setPrefWidth(300);
        rightSection.setPadding(new Insets(20));

        Label alertsTitle = new Label("Stock Alerts");
        alertsTitle.getStyleClass().add("card-title");

        alertsBox = new VBox(10);
        alertsBox.getStyleClass().add("alerts-container");

        ScrollPane alertsScroll = new ScrollPane(alertsBox);
        alertsScroll.setFitToWidth(true);
        alertsScroll.getStyleClass().add("alerts-scroll");

        rightSection.getChildren().addAll(alertsTitle, alertsScroll);
        setRight(rightSection);
    }

    private Button createButton(String text, String... styleClasses) {
        Button button = new Button(text);
        button.getStyleClass().add("button");
        button.getStyleClass().addAll(styleClasses);
        return button;
    }

    // Getter methods for all controls that need to be accessed by the controller
    public Label getWelcomeLabel() { return welcomeLabel; }
    public DatePicker getStartDate() { return startDate; }
    public DatePicker getEndDate() { return endDate; }
    public SalesChart getSalesChart() { return salesChart; }
    public VBox getInventoryCard() { return inventoryCard; }
    public VBox getAlertsBox() { return alertsBox; }
    public Label getTotalItemsLabel() { return totalItemsLabel; }
    public Label getTotalValueLabel() { return totalValueLabel; }
    public Label getCategoriesLabel() { return categoriesLabel; }
    public Label getSuppliersLabel() { return suppliersLabel; }
    public Button getNewItemBtn() { return newItemBtn; }
    public Button getManageInventoryBtn() { return manageInventoryBtn; }
    public Button getManageSuppliersBtn() { return manageSuppliersBtn; }
    public Button getManageCategoriesBtn() { return manageCategoriesBtn; }
    public Button getViewReportsBtn() { return viewReportsBtn; }
    public Button getApplyFilterBtn() { return applyFilterBtn; }
    public Button getGenerateReportBtn() { return generateReportBtn; }
    public Button getExportDataBtn() { return exportDataBtn; }
    public ListView<String> getRecentActivitiesList() { return recentActivitiesList; }
}