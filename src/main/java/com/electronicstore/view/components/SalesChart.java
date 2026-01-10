package com.electronicstore.view.components;

import javafx.geometry.Pos;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import java.time.LocalDate;
import java.util.Map;

public class SalesChart extends VBox {
    private final LineChart<String, Number> lineChart;
    private final BarChart<String, Number> barChart;
    private final CategoryAxis xAxis;
    private final NumberAxis yAxis;
    private final Label noDataLabel;
    private final StackPane chartContainer;

    public SalesChart() {
        // Initialize axes
        xAxis = new CategoryAxis();
        yAxis = new NumberAxis();

        // Style setup
        getStyleClass().add("sales-chart");
        setSpacing(10);

        // Initialize charts
        lineChart = createLineChart();
        barChart = createBarChart();

        // Create no data message
        noDataLabel = createNoDataLabel();

        // Create chart container
        chartContainer = new StackPane();
        chartContainer.getChildren().addAll(lineChart, barChart, noDataLabel);

        // Add chart container to VBox
        getChildren().add(chartContainer);

        // Initial state
        showNoDataMessage("No sales data available");
    }

    private LineChart<String, Number> createLineChart() {
        LineChart<String, Number> chart = new LineChart<>(xAxis, yAxis);
        chart.setTitle("Sales Trend");
        chart.setAnimated(true);
        chart.setCreateSymbols(true);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("sales-line-chart");
        return chart;
    }

    private BarChart<String, Number> createBarChart() {
        BarChart<String, Number> chart = new BarChart<>(xAxis, yAxis);
        chart.setTitle("Sales Comparison");
        chart.setAnimated(true);
        chart.setLegendVisible(true);
        chart.getStyleClass().add("sales-bar-chart");
        chart.setCategoryGap(20);
        chart.setBarGap(4);
        return chart;
    }

    private Label createNoDataLabel() {
        Label label = new Label();
        label.getStyleClass().add("no-data-label");
        label.setAlignment(Pos.CENTER);
        label.setWrapText(true);
        label.setMaxWidth(300);
        return label;
    }

    public void showDailySales(Map<LocalDate, Double> salesData) {
        if (salesData == null || salesData.isEmpty()) {
            showNoDataMessage("No daily sales data available for the selected period");
            return;
        }

        lineChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Daily Sales");

        salesData.forEach((date, amount) ->
                series.getData().add(new XYChart.Data<>(date.toString(), amount))
        );

        showChart(lineChart, series);

        // Add hover effect for data points
        series.getData().forEach(data -> {
            data.getNode().setOnMouseEntered(e ->
                    showTooltip(data, String.format("Date: %s%nAmount: $%.2f",
                            data.getXValue(), data.getYValue())));
            data.getNode().setOnMouseExited(e -> hideTooltip(data));
        });
    }

    public void showMonthlySales(Map<String, Double> salesData) {
        if (salesData == null || salesData.isEmpty()) {
            showNoDataMessage("No monthly sales data available");
            return;
        }

        barChart.getData().clear();
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Sales");

        salesData.forEach((month, amount) ->
                series.getData().add(new XYChart.Data<>(month, amount))
        );

        showChart(barChart, series);
    }

    public void compareSales(Map<String, Double> currentPeriod, Map<String, Double> previousPeriod) {
        boolean currentEmpty = (currentPeriod == null || currentPeriod.isEmpty());
        boolean previousEmpty = (previousPeriod == null || previousPeriod.isEmpty());

        if (currentEmpty && previousEmpty) {
            showNoDataMessage("No sales data available for comparison");
            return;
        }

        barChart.getData().clear();

        if (!currentEmpty) {
            XYChart.Series<String, Number> currentSeries = new XYChart.Series<>();
            currentSeries.setName("Current Period");
            currentPeriod.forEach((date, amount) ->
                    currentSeries.getData().add(new XYChart.Data<>(date, amount))
            );
            barChart.getData().add(currentSeries);
        }

        if (!previousEmpty) {
            XYChart.Series<String, Number> previousSeries = new XYChart.Series<>();
            previousSeries.setName("Previous Period");
            previousPeriod.forEach((date, amount) ->
                    previousSeries.getData().add(new XYChart.Data<>(date, amount))
            );
            barChart.getData().add(previousSeries);
        }

        showChart(barChart, null);
    }

    private void showChart(XYChart<String, Number> chartToShow, XYChart.Series<String, Number> series) {
        // Hide all charts and no data message
        lineChart.setVisible(false);
        barChart.setVisible(false);
        noDataLabel.setVisible(false);

        // Show the selected chart
        chartToShow.setVisible(true);

        if (series != null) {
            chartToShow.getData().add(series);
        }
    }

    public void showNoDataMessage(String message) {
        lineChart.setVisible(false);
        barChart.setVisible(false);

        noDataLabel.setText(message);
        noDataLabel.setVisible(true);
    }

    private void showTooltip(XYChart.Data<String, Number> data, String text) {
        Tooltip tooltip = new Tooltip(text);
        Tooltip.install(data.getNode(), tooltip);
    }

    private void hideTooltip(XYChart.Data<String, Number> data) {
        Tooltip.uninstall(data.getNode(), null);
    }

    public void setChartTitle(String title) {
        lineChart.setTitle(title);
        barChart.setTitle(title);
    }

    public void setAxisLabels(String xLabel, String yLabel) {
        xAxis.setLabel(xLabel);
        yAxis.setLabel(yLabel);
    }
}