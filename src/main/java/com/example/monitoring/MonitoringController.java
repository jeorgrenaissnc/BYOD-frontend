package com.example.monitoring;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Controller for the Monitoring view.
 * Displays device logs with search, filtering, pagination, and card summaries.
 */
public class MonitoringController implements Initializable {

    // ==================== NAVIGATION BAR ====================
    @FXML private ImageView logoImage;
    @FXML private Button dashboardButton;
    @FXML private Button monitoringButton;
    @FXML private Button registrationButton;
    @FXML private Button reportsButton;
    @FXML private Button accountButton;
    @FXML private Button logoutButton;

    // ==================== STATUS & FILTERS ====================
    @FXML private Label syncStatusLabel;
    @FXML private TextField searchField;
    @FXML private Button allDeviceTypesButton;
    @FXML private Button allStatusButton;
    @FXML private Button datePickerButton;
    @FXML private Button exportLogButton;
    @FXML private Button logEntryButton;

    // ==================== CARD LABELS ====================
    @FXML private Label totalStudentsLabel;
    @FXML private Label totalDevicesLabel;
    @FXML private Label devicesInsideLabel;
    @FXML private Label ingressTodayLabel;
    @FXML private Label egressTodayLabel;

    // ==================== TABLE & COLUMNS ====================
    @FXML private TableView<LogEntry> monitoringTableView;
    @FXML private TableColumn<LogEntry, String> studentNameColumn;
    @FXML private TableColumn<LogEntry, String> studentIdColumn;
    @FXML private TableColumn<LogEntry, String> deviceSerialColumn;
    @FXML private TableColumn<LogEntry, String> statusColumn;
    @FXML private TableColumn<LogEntry, String> lastLogColumn;
    @FXML private TableColumn<LogEntry, Void> actionsColumn;    // History button
    @FXML private TableColumn<LogEntry, Void> editColumn;       // Ingress/Egress toggle

    // ==================== PAGINATION ====================
    @FXML private Label paginationStatusLabel;
    @FXML private Button prevPageButton;
    @FXML private Button page1Button;
    @FXML private Button page2Button;
    @FXML private Button page3Button;
    @FXML private Button ellipsisButton;
    @FXML private Button lastPageButton;
    @FXML private Button nextPageButton;

    // ==================== DATA MODELS ====================
    private ObservableList<LogEntry> allLogEntries = FXCollections.observableArrayList();
    private ObservableList<LogEntry> currentPageData = FXCollections.observableArrayList();
    private int currentPage = 1;
    private final int rowsPerPage = 10;
    private int totalPages = 1;
    private int totalItems = 0;

    // ==================== INITIALIZATION ====================
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        setupSearchListener();
        loadInitialData();           // TODO: Replace with real data load
        updateCardNumbers();         // TODO: Fetch from backend
        updatePagination();

        Platform.runLater(() -> {
            Stage stage = (Stage) monitoringButton.getScene().getWindow();
            if (stage != null) {
                stage.setMaximized(true);
            }
        });
    }

    // ==================== TABLE SETUP ====================
    private void setupTableColumns() {
        studentNameColumn.setCellValueFactory(new PropertyValueFactory<>("studentName"));
        studentIdColumn.setCellValueFactory(new PropertyValueFactory<>("studentId"));
        deviceSerialColumn.setCellValueFactory(new PropertyValueFactory<>("deviceSerial"));
        lastLogColumn.setCellValueFactory(new PropertyValueFactory<>("lastLog"));

        // Status column with custom CSS classes: table-status-in / table-status-out
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        statusColumn.setCellFactory(col -> new TableCell<LogEntry, String>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    getStyleClass().add("table-status");
                } else {
                    setText(status);
                    getStyleClass().add("table-status");
                    if ("Ingress".equalsIgnoreCase(status)) {
                        getStyleClass().add("table-status-in");
                    } else if ("Egress".equalsIgnoreCase(status)) {
                        getStyleClass().add("table-status-out");
                    }
                }
            }
        });

        // Actions column – View History button
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button historyButton = new Button("👁️‍🗨️");
            {
                historyButton.getStyleClass().add("table-action");
                historyButton.setOnAction(e -> {
                    LogEntry entry = getTableView().getItems().get(getIndex());
                    onViewHistory(entry);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : historyButton);
            }
        });

        // Edit column – quick Ingress/Egress toggle with CSS classes table-edit-in / table-edit-out
        editColumn.setCellFactory(param -> new TableCell<>() {
            private final Button ingressButton = new Button("📥 In");
            private final Button egressButton = new Button("📤 Out");
            {
                ingressButton.getStyleClass().add("table-edit");
                egressButton.getStyleClass().add("table-edit");
                ingressButton.setOnAction(e -> {
                    LogEntry entry = getTableView().getItems().get(getIndex());
                    onMarkIngress(entry);
                });
                egressButton.setOnAction(e -> {
                    LogEntry entry = getTableView().getItems().get(getIndex());
                    onMarkEgress(entry);
                });
            }
            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    LogEntry entry = getTableView().getItems().get(getIndex());
                    // Determine which button to show and apply appropriate CSS class
                    if ("Ingress".equalsIgnoreCase(entry.getStatus())) {
                        // Currently Ingress -> show Egress button with table-edit-out style
                        egressButton.getStyleClass().removeAll("table-edit-in", "table-edit-out");
                        egressButton.getStyleClass().add("table-edit-out");
                        setGraphic(egressButton);
                    } else {
                        // Currently Egress -> show Ingress button with table-edit-in style
                        ingressButton.getStyleClass().removeAll("table-edit-in", "table-edit-out");
                        ingressButton.getStyleClass().add("table-edit-in");
                        setGraphic(ingressButton);
                    }
                }
            }
        });
    }

    private void setupSearchListener() {
        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            currentPage = 1;
            filterAndPaginate();
        });
    }

    // ==================== FILTERING & PAGINATION ====================
    private void filterAndPaginate() {
        String searchTerm = searchField.getText().toLowerCase();
        ObservableList<LogEntry> filtered = allLogEntries.filtered(entry ->
                entry.getStudentName().toLowerCase().contains(searchTerm) ||
                        entry.getStudentId().toLowerCase().contains(searchTerm) ||
                        entry.getDeviceSerial().toLowerCase().contains(searchTerm)
        );
        totalItems = filtered.size();
        totalPages = (int) Math.ceil((double) totalItems / rowsPerPage);
        if (totalPages == 0) totalPages = 1;
        if (currentPage > totalPages) currentPage = totalPages;

        int fromIndex = (currentPage - 1) * rowsPerPage;
        int toIndex = Math.min(fromIndex + rowsPerPage, totalItems);
        if (fromIndex < toIndex) {
            currentPageData.setAll(filtered.subList(fromIndex, toIndex));
        } else {
            currentPageData.clear();
        }
        monitoringTableView.setItems(currentPageData);
        updatePaginationStatus();
        updatePageButtons();
    }

    private void updatePagination() {
        filterAndPaginate();
    }

    private void updatePaginationStatus() {
        int start = (currentPage - 1) * rowsPerPage + 1;
        int end = Math.min(currentPage * rowsPerPage, totalItems);
        if (totalItems == 0) start = 0;
        paginationStatusLabel.setText(String.format("Showing %d-%d of %d log entries today", start, end, totalItems));
    }

    private void updatePageButtons() {
        page1Button.setText(String.valueOf(currentPage));
        page2Button.setText(String.valueOf(Math.min(currentPage + 1, totalPages)));
        page3Button.setText(String.valueOf(Math.min(currentPage + 2, totalPages)));
        page1Button.setDisable(currentPage == totalPages);
        page2Button.setDisable(currentPage + 1 > totalPages);
        page3Button.setDisable(currentPage + 2 > totalPages);
        ellipsisButton.setVisible(totalPages > 3 && currentPage + 2 < totalPages);
        lastPageButton.setText(String.valueOf(totalPages));
        lastPageButton.setDisable(currentPage == totalPages);
        prevPageButton.setDisable(currentPage == 1);
        nextPageButton.setDisable(currentPage == totalPages);
    }

    // ==================== NAVIGATION ====================
    private void navigateTo(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Stage stage = (Stage) monitoringButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.setTitle(title);
            stage.setMaximized(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Navigation Error", "Could not load " + fxmlPath);
        }
    }

    // ==================== BUTTON ACTIONS ====================
    @FXML private void onDashboardClick() {
        navigateTo("/fxml/Dashboard.fxml", "Dashboard - BYOD System");
    }

    @FXML private void onMonitoringClick() {
        refreshMonitoringData();
    }

    @FXML private void onRegistrationClick() {
        navigateTo("/fxml/Registration.fxml", "Device Registration - BYOD System");
    }

    @FXML private void onReportsClick() {
        navigateTo("/fxml/Reports.fxml", "Reports - BYOD System");
    }

    @FXML private void onAccountClick() {
        navigateTo("/fxml/Account.fxml", "Account Settings - BYOD System");
    }

    @FXML private void onLogoutClick() {
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
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML private void onSearch() {
        filterAndPaginate();
    }

    @FXML private void onAllDeviceTypesFilter() {
        // TODO: Implement device type filter
        System.out.println("Filter by device type - NOT IMPLEMENTED");
        filterAndPaginate();
    }

    @FXML private void onAllStatusFilter() {
        // TODO: Implement status filter (All, Ingress, Egress)
        System.out.println("Filter by status - NOT IMPLEMENTED");
        filterAndPaginate();
    }

    @FXML private void onDatePicker() {
        // TODO: Date picker dialog
        System.out.println("Date picker - NOT IMPLEMENTED");
    }

    @FXML private void onExportLog() {
        // TODO: Export to CSV/Excel
        System.out.println("Export log - NOT IMPLEMENTED");
    }

    @FXML private void onLogEntry() {
        // TODO: Manual log entry dialog
        System.out.println("Manual log entry - NOT IMPLEMENTED");
    }

    @FXML private void onPrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updatePagination();
        }
    }

    @FXML private void onPage1() {
        int page = Integer.parseInt(page1Button.getText());
        if (page != currentPage) {
            currentPage = page;
            updatePagination();
        }
    }

    @FXML private void onPage2() {
        if (page2Button.isDisabled()) return;
        currentPage = Integer.parseInt(page2Button.getText());
        updatePagination();
    }

    @FXML private void onPage3() {
        if (page3Button.isDisabled()) return;
        currentPage = Integer.parseInt(page3Button.getText());
        updatePagination();
    }

    @FXML private void onEllipsis() {
        currentPage = Math.min(currentPage + 3, totalPages);
        updatePagination();
    }

    @FXML private void onLastPage() {
        currentPage = totalPages;
        updatePagination();
    }

    @FXML private void onNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updatePagination();
        }
    }

    // ==================== TABLE ACTION HANDLERS ====================
    private void onViewHistory(LogEntry entry) {
        // TODO: Show student/device history dialog
        System.out.println("View history for: " + entry);
    }

    private void onMarkIngress(LogEntry entry) {
        // TODO: Update status to Ingress in backend
        System.out.println("Mark Ingress: " + entry);
    }

    private void onMarkEgress(LogEntry entry) {
        // TODO: Update status to Egress in backend
        System.out.println("Mark Egress: " + entry);
    }

    // ==================== DATA LOADING ====================
    private void loadInitialData() {
        // TODO: Replace dummy data with real backend call
        allLogEntries.clear();
        for (int i = 1; i <= 1307; i++) {
            allLogEntries.add(new LogEntry(
                    "Student " + i,
                    "S" + (10000 + i),
                    "SN-ABC-" + i,
                    i % 2 == 0 ? "Ingress" : "Egress",
                    "2025-05-25 08:" + (i % 60)
            ));
        }
    }

    private void refreshMonitoringData() {
        // TODO: Reload from backend
        loadInitialData();
        updateCardNumbers();
        currentPage = 1;
        filterAndPaginate();
    }

    private void updateCardNumbers() {
        // TODO: Fetch real counts from backend
        totalStudentsLabel.setText("1250");
        totalDevicesLabel.setText("1870");
        devicesInsideLabel.setText("342");
        ingressTodayLabel.setText("156");
        egressTodayLabel.setText("98");
        syncStatusLabel.setText("⏺ Live");
        syncStatusLabel.getStyleClass().add("sync-status-live");
    }

    // ==================== HELPER METHODS ====================
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}