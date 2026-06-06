package com.example.reports;

import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.time.LocalDate;

/**
 * Controls reports.fxml.
 * CHANGE: wire filter to MovementService.filter(query, date).
 */
public class ReportsController {

    /* ── Filter bar ─────────────────────────────────────── */
    @FXML private TextField  searchField;
    @FXML private DatePicker datePicker;

    /* ── Stat labels ────────────────────────────────────── */
    @FXML private Label statLog;
    @FXML private Label statIn;
    @FXML private Label statOut;
    @FXML private Label statAct;

    /* ── Movement log table ─────────────────────────────── */
    @FXML private TableView<LogRow>          logTable;
    @FXML private TableColumn<LogRow,String> colLogId;
    @FXML private TableColumn<LogRow,String> colStudentName;
    @FXML private TableColumn<LogRow,String> colSerial;
    @FXML private TableColumn<LogRow,String> colAction;
    @FXML private TableColumn<LogRow,String> colDateTime;
    @FXML private TableColumn<LogRow,String> colGuard;

    @FXML
    public void initialize() {
        setupTable();
        searchField.setOnAction(e -> handleFilter());
        loadStats();
    }

    private void setupTable() {
        colLogId.setCellValueFactory(c       -> c.getValue().logIdProp());
        colStudentName.setCellValueFactory(c -> c.getValue().studentNameProp());
        colSerial.setCellValueFactory(c      -> c.getValue().serialProp());
        colAction.setCellValueFactory(c      -> c.getValue().actionProp());
        colDateTime.setCellValueFactory(c    -> c.getValue().dateTimeProp());
        colGuard.setCellValueFactory(c       -> c.getValue().guardProp());

        // Color action column
        colAction.setCellFactory(col -> new TableCell<>() {
            @Override protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) { setText(null); setStyle(""); return; }
                setText(item);
                setStyle("Ingress".equals(item)
                    ? "-fx-text-fill:#1a6b1a;-fx-font-weight:700;"
                    : "-fx-text-fill:#7a1a1a;-fx-font-weight:700;");
            }
        });
        logTable.setPlaceholder(new Label("No movement logs found."));
        // CHANGE: MovementService.filter(null, null) → logTable.setItems(...)
    }

    @FXML private void handleFilter() {
        String    q    = searchField.getText().trim();
        LocalDate date = datePicker.getValue();
        // CHANGE: MovementService.filter(q, date) → logTable.setItems(...)
        loadStats();
    }

    @FXML private void handleClear() {
        searchField.clear();
        datePicker.setValue(null);
        // CHANGE: reload all logs
        loadStats();
    }

    private void loadStats() {
        // CHANGE: pull from MovementService / DeviceService
        if (statLog != null) statLog.setText("0");
        if (statIn  != null) statIn.setText("0");
        if (statOut != null) statOut.setText("0");
        if (statAct != null) statAct.setText("0");
    }

    /* ── Inner row model ────────────────────────────────── */
    public static class LogRow {
        private final javafx.beans.property.SimpleStringProperty
            logId, studentName, serial, action, dateTime, guard;

        public LogRow(String logId, String studentName, String serial,
                      String action, String dateTime, String guard) {
            this.logId       = new javafx.beans.property.SimpleStringProperty(logId);
            this.studentName = new javafx.beans.property.SimpleStringProperty(studentName);
            this.serial      = new javafx.beans.property.SimpleStringProperty(serial);
            this.action      = new javafx.beans.property.SimpleStringProperty(action);
            this.dateTime    = new javafx.beans.property.SimpleStringProperty(dateTime);
            this.guard       = new javafx.beans.property.SimpleStringProperty(guard);
        }
        public javafx.beans.property.SimpleStringProperty logIdProp()       { return logId; }
        public javafx.beans.property.SimpleStringProperty studentNameProp() { return studentName; }
        public javafx.beans.property.SimpleStringProperty serialProp()      { return serial; }
        public javafx.beans.property.SimpleStringProperty actionProp()      { return action; }
        public javafx.beans.property.SimpleStringProperty dateTimeProp()    { return dateTime; }
        public javafx.beans.property.SimpleStringProperty guardProp()       { return guard; }
    }
}
