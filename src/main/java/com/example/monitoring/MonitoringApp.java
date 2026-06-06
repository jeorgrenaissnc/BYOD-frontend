package com.example.monitoring;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;

/**
 * Standalone test application for the Monitoring view.
 * Launches the monitoring screen directly without login.
 * Use this for UI testing and development.
 */
public class MonitoringApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Load the Monitoring FXML
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Monitoring.fxml"));
        Parent root = loader.load();

        // Set up the stage
        primaryStage.setTitle("BYOD Monitoring System - Monitoring View (Test Mode)");
        primaryStage.setScene(new Scene(root));

        // Make it full screen (maximized)
        primaryStage.setMaximized(true);

        // Optional: set initial size to screen bounds if maximized doesn't work on some systems
        Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        primaryStage.setWidth(screenBounds.getWidth());
        primaryStage.setHeight(screenBounds.getHeight());

        primaryStage.show();

        // Optional: print success message
        System.out.println("Monitoring view loaded successfully. (Test mode - no login required)");
    }

    public static void main(String[] args) {
        launch(args);
    }
}