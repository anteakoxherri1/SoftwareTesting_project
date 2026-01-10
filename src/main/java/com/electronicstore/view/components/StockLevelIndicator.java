package com.electronicstore.view.components;

import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class StockLevelIndicator extends HBox {
    private final Circle indicator;
    private final Label stockLabel;

    public StockLevelIndicator() {
        setSpacing(5);

        indicator = new Circle(5);
        stockLabel = new Label();

        getChildren().addAll(indicator, stockLabel);
    }

    public void updateStockLevel(int currentStock, int minStock) {
        if (currentStock <= minStock) {
            indicator.setFill(Color.RED);
            stockLabel.setText("Low Stock: " + currentStock);
            stockLabel.setTextFill(Color.RED);
        } else if (currentStock <= minStock * 2) {
            indicator.setFill(Color.ORANGE);
            stockLabel.setText("Stock: " + currentStock);
            stockLabel.setTextFill(Color.ORANGE);
        } else {
            indicator.setFill(Color.GREEN);
            stockLabel.setText("Stock: " + currentStock);
            stockLabel.setTextFill(Color.GREEN);
        }
    }
}