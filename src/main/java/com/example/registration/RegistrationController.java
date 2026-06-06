package com.example.registration;

import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Controls registration.fxml.
 * CHANGE: wire saveRegistration() to StudentService and DeviceService
 * once DB connection is ready.
 */
public class RegistrationController {

    /* ── Student fields ─────────────────────────────────── */
    @FXML private TextField studentIdField;
    @FXML private TextField fullNameField;
    @FXML private TextField courseField;
    @FXML private TextField yearSectionField;
    @FXML private TextField contactField;

    /* ── Device fields ──────────────────────────────────── */
    @FXML private ComboBox<String> deviceTypeCombo;
    @FXML private TextField        brandField;
    @FXML private TextField        modelField;
    @FXML private TextField        serialField;
    @FXML private TextField        colorField;

    @FXML
    public void initialize() {
        deviceTypeCombo.getItems().addAll(
            "Laptop", "Tablet", "Smartphone", "Desktop", "Other"
        );
        colorField.setText("Black");
    }

    @FXML
    private void handleSave() {
        if (!validate()) return;

        // CHANGE: pass values to StudentService.save() and DeviceService.save()
        showAlert(Alert.AlertType.INFORMATION, "Success",
            "Registration saved for " + fullNameField.getText().trim() + ".");
        handleClear();
    }

    @FXML
    private void handleClear() {
        studentIdField.clear();
        fullNameField.clear();
        courseField.clear();
        yearSectionField.clear();
        contactField.clear();
        deviceTypeCombo.setValue(null);
        brandField.clear();
        modelField.clear();
        serialField.clear();
        colorField.setText("Black");
    }

    private boolean validate() {
        if (studentIdField.getText().isBlank() ||
            fullNameField.getText().isBlank()  ||
            serialField.getText().isBlank()) {
            showAlert(Alert.AlertType.WARNING, "Validation",
                "Student ID, Full Name, and Serial Number are required.");
            return false;
        }
        return true;
    }

    private void showAlert(Alert.AlertType type, String title, String msg) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(msg);
        alert.showAndWait();
    }
}
