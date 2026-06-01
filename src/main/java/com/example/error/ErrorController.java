package com.example.error;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;

public class ErrorController {

    @FXML
    private Label titleLabel;

    @FXML
    private Label subtitleLabel;

    @FXML
    private Label errorMessageLabel;

    @FXML
    private Label errorDetailLabel;

    @FXML
    private Button okButton;

    private Stage dialogStage;

    @FXML
    private void handleOk() {
        closeDialog();
    }

    private void closeDialog() {
        if (dialogStage != null) {
            dialogStage.close();
        }
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setTitle(String title) {
        if (titleLabel != null) {
            titleLabel.setText(title);
        }
    }

    public void setSubtitle(String subtitle) {
        if (subtitleLabel != null) {
            subtitleLabel.setText(subtitle);
        }
    }

    public void setErrorMessage(String message) {
        if (errorMessageLabel != null) {
            errorMessageLabel.setText(message);
        }
    }

    public void setErrorDetail(String detail) {
        if (errorDetailLabel != null) {
            if (detail == null || detail.isEmpty()) {
                errorDetailLabel.setVisible(false);
                errorDetailLabel.setManaged(false);
            } else {
                errorDetailLabel.setText(detail);
                errorDetailLabel.setVisible(true);
                errorDetailLabel.setManaged(true);
            }
        }
    }
}