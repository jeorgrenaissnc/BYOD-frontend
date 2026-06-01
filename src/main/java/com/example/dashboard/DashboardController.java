package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.application.Platform;
import javafx.stage.Screen;
import javafx.geometry.Rectangle2D;
import javafx.stage.Stage;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DashboardController {

    // Stats cards
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalDevicesLabel;
    @FXML private Label devicesInsideLabel;
    @FXML private Label ingressTodayLabel;
    @FXML private Label egressTodayLabel;

    // Header
    @FXML private Label dateLabel;
    @FXML private Label syncStatusLabel;

    // Buttons
    @FXML private Button refreshButton;
    @FXML private Button logoutButton;
    @FXML private Button dashboardButton;
    @FXML private Button monitoringButton;
    @FXML private Button registrationButton;
    @FXML private Button reportsButton;
    @FXML private Button accountButton;
    @FXML private Button seeAllButton;
    @FXML private Button scanQrButton;
    @FXML private Button searchButton;
    @FXML private Button ingressButton;
    @FXML private Button egressButton;
    @FXML private Button exportButton;

    // Chart
    @FXML private BarChart<String, Number> activityChart;

    // Log entries
    @FXML private Label logName0, logName1, logName2, logName3, logName4;
    @FXML private Label logId0, logId1, logId2, logId3, logId4;
    @FXML private Label logStatus0, logStatus1, logStatus2, logStatus3, logStatus4;
    @FXML private Label logTime0, logTime1, logTime2, logTime3, logTime4;

    @FXML
    public void initialize() {
        dateLabel.setText(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy").format(LocalDate.now()));
        syncStatusLabel.setText("⏳ Loading...");
        syncStatusLabel.getStyleClass().add("sync-status-connecting");
        loadHardcodedData();

        Platform.runLater(() -> {
            Stage stage = (Stage) dateLabel.getScene().getWindow();
            if (stage != null) {
                // Get primary screen bounds
                Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
                stage.setWidth(screenBounds.getWidth());
                stage.setHeight(screenBounds.getHeight());
                stage.setX(screenBounds.getMinX());
                stage.setY(screenBounds.getMinY());
                stage.setResizable(false);   // Disables maximize/restore button
            }
        });
    }

    private void loadHardcodedData() {
        // TODO: Replace with actual backend call – fetch statistics (total students, total devices, etc.)
        // Expected data source: DashboardService.getStatistics()
        totalStudentsLabel.setText("245");
        totalDevicesLabel.setText("312");
        devicesInsideLabel.setText("187");
        ingressTodayLabel.setText("42");
        egressTodayLabel.setText("38");

        // TODO: Replace with real log data from backend – fetch last 5 entries
        // Expected data source: DashboardService.getRecentLogs()
        // Each entry: student name, ID, status (In/Out), time
        String[][] logs = {
                {"John Michael Santos", "2024-00125", "📥 In", "08:15 AM"},
                {"Maria Clara Gomez",   "2024-00456", "📤 Out", "05:30 PM"},
                {"Jose Rizal Mercado",  "2024-00789", "📥 In", "09:20 AM"},
                {"Andres Bonifacio",    "2024-00321", "📥 In", "10:45 AM"},
                {"Gabriela Silang",     "2024-00888", "📤 Out", "04:00 PM"}
        };
        for (int i = 0; i < 5; i++) {
            getLogName(i).setText(logs[i][0]);
            getLogId(i).setText(logs[i][1]);
            getLogStatus(i).setText(logs[i][2]);
            getLogTime(i).setText(logs[i][3]);
            applyStatusStyle(getLogStatus(i), getLogTime(i), logs[i][2]);
        }

        // TODO: Replace with real chart data from backend – last 7 days ingress/egress counts
        // Expected data source: DashboardService.getChartData()
        String[] days = {"Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun"};
        int[] ingressData = {23, 31, 28, 35, 42, 18, 12};
        int[] egressData  = {20, 27, 25, 30, 38, 15, 10};
        updateChart(days, ingressData, egressData);

        // TODO: After successful data fetch, update sync status accordingly
        // For success: updateSyncStatus("Live")
        // For failure: updateSyncStatus("Offline")
        updateSyncStatus("Live");
    }

    // Helper methods (no TODOs needed – these are UI utilities)
    private Label getLogName(int i) { return switch(i) { case 0 -> logName0; case 1 -> logName1; case 2 -> logName2; case 3 -> logName3; case 4 -> logName4; default -> null; }; }
    private Label getLogId(int i)   { return switch(i) { case 0 -> logId0; case 1 -> logId1; case 2 -> logId2; case 3 -> logId3; case 4 -> logId4; default -> null; }; }
    private Label getLogStatus(int i) { return switch(i) { case 0 -> logStatus0; case 1 -> logStatus1; case 2 -> logStatus2; case 3 -> logStatus3; case 4 -> logStatus4; default -> null; }; }
    private Label getLogTime(int i) { return switch(i) { case 0 -> logTime0; case 1 -> logTime1; case 2 -> logTime2; case 3 -> logTime3; case 4 -> logTime4; default -> null; }; }

    private void applyStatusStyle(Label statusLabel, Label timeLabel, String statusText) {
        statusLabel.getStyleClass().removeAll("log-status-in", "log-status-out");
        timeLabel.getStyleClass().removeAll("log-time-in", "log-time-out");
        if (statusText.contains("In")) {
            statusLabel.getStyleClass().add("log-status-in");
            timeLabel.getStyleClass().add("log-time-in");
        } else if (statusText.contains("Out")) {
            statusLabel.getStyleClass().add("log-status-out");
            timeLabel.getStyleClass().add("log-time-out");
        }
    }

    private void updateChart(String[] days, int[] ingress, int[] egress) {
        XYChart.Series<String, Number> ingressSeries = new XYChart.Series<>();
        ingressSeries.setName("Ingress");
        XYChart.Series<String, Number> egressSeries = new XYChart.Series<>();
        egressSeries.setName("Egress");
        for (int i = 0; i < days.length; i++) {
            ingressSeries.getData().add(new XYChart.Data<>(days[i], ingress[i]));
            egressSeries.getData().add(new XYChart.Data<>(days[i], egress[i]));
        }
        activityChart.getData().clear();
        activityChart.getData().addAll(ingressSeries, egressSeries);
    }

    private void updateSyncStatus(String status) {
        syncStatusLabel.getStyleClass().removeAll("sync-status-live", "sync-status-offline", "sync-status-connecting");
        if ("Live".equalsIgnoreCase(status)) {
            syncStatusLabel.setText("⏺ Live");
            syncStatusLabel.getStyleClass().add("sync-status-live");
        } else if ("Offline".equalsIgnoreCase(status)) {
            syncStatusLabel.setText("⏺ Offline");
            syncStatusLabel.getStyleClass().add("sync-status-offline");
        } else {
            syncStatusLabel.setText("⏳ Loading...");
            syncStatusLabel.getStyleClass().add("sync-status-connecting");
        }
    }

    // Action handlers
    @FXML private void handleRefresh() {
        // TODO: Call backend refresh method (e.g., DashboardService.refreshData()) then reload UI
        loadHardcodedData();
        System.out.println("Refreshed");
    }

    @FXML private void handleLogout() {
        Stage stage = (Stage) logoutButton.getScene().getWindow();
        stage.close();
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/Login.fxml"));
            Parent root = loader.load();
            Stage loginStage = new Stage();
            loginStage.setScene(new Scene(root));
            loginStage.setTitle("BYOD Monitoring System - Login");
            loginStage.setResizable(false);
            loginStage.centerOnScreen();
            loginStage.show();
        } catch (IOException e) { e.printStackTrace(); }
    }

    // TODO: Implement navigation to other views (Monitoring, Registration, Reports, Account)
    @FXML private void handleDashboard() { System.out.println("Dashboard"); }
    @FXML private void handleMonitoring() { System.out.println("Monitoring"); }
    @FXML private void handleRegistration() { System.out.println("Registration"); }
    @FXML private void handleReports() { System.out.println("Reports"); }
    @FXML private void handleAccount() { System.out.println("Account"); }

    // TODO: Implement full log view when "See all" is clicked
    @FXML private void handleSeeAll() { System.out.println("See all"); }

    // TODO: Implement quick action functionalities (QR scan, search, ingress/egress logging, export)
    @FXML private void handleScanQr() { System.out.println("Scan QR"); }
    @FXML private void handleSearch() { System.out.println("Search"); }
    @FXML private void handleIngress() { System.out.println("Ingress"); }
    @FXML private void handleEgress() { System.out.println("Egress"); }
    @FXML private void handleExport() { System.out.println("Export"); }
}