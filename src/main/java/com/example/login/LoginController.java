package com.example.login;

import com.example.error.ErrorController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.application.Platform;
import java.io.IOException;

public class LoginController {

    @FXML private ImageView logoImage;
    @FXML private Label appTitleLabel;
    @FXML private Label subtitleLabel;
    @FXML private Label usernameLabel;
    @FXML private TextField usernameField;
    @FXML private Label passwordLabel;
    @FXML private PasswordField passwordField;
    @FXML private Button loginButton;
    @FXML private Button cancelButton;

    private ErrorController errorDialogController;

    @FXML
    public void initialize() {
        setupEventHandlers();
        setupInputValidation();
        Platform.runLater(() -> usernameField.requestFocus());
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        cancelButton.setOnAction(event -> handleCancel());
        passwordField.setOnAction(event -> handleLogin());
        usernameField.setOnAction(event -> handleLogin());
    }

    private void setupInputValidation() {
        usernameField.textProperty().addListener((obs, old, newVal) ->
                usernameField.getStyleClass().remove("error-field"));
        passwordField.textProperty().addListener((obs, old, newVal) ->
                passwordField.getStyleClass().remove("error-field"));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText().trim();
        String password = passwordField.getText();

        if (username.isEmpty()) {
            showErrorDialog("Validation Error", "Invalid Input",
                    "Please enter username.", null, usernameField);
            return;
        }

        if (password.isEmpty()) {
            showErrorDialog("Validation Error", "Invalid Input",
                    "Please enter password.", null, passwordField);
            return;
        }

        if (authenticate(username, password)) {
            loginSuccess();
        } else {
            showErrorDialog("Authentication Failed", "Login Error",
                    "Invalid username or password. Please try again.",
                    null, null);
        }
    }

    private boolean authenticate(String username, String password) {
        return "admin".equals(username) && "password".equals(password);
    }

    private void loginSuccess() {
        clearErrorStyles();
        System.out.println("Login successful! Username: " + usernameField.getText());

        Stage loginStage = (Stage) cancelButton.getScene().getWindow();
        loginStage.close();

        openDashboard();
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/dashboard.fxml"));
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("BYOD Monitoring System - Dashboard");
            dashboardStage.setScene(new Scene(root));
            dashboardStage.getScene().getStylesheets().add(
                    getClass().getResource("/css/stylesheet.css").toExternalForm()
            );
            dashboardStage.centerOnScreen();
            dashboardStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showErrorDialog("Loading Error", "Failed to Load Dashboard",
                    "Unable to load the main dashboard. Please check the application setup.",
                    e.getMessage(), null);
        }
    }

    private void showErrorDialog(String title, String subtitle,
                                 String message, String details,
                                 Control erroredControl) {
        clearErrorStyles();

        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/fxml/errorDialog.fxml"));
            StackPane root = loader.load();

            errorDialogController = loader.getController();
            errorDialogController.setTitle(title);
            errorDialogController.setSubtitle(subtitle);
            errorDialogController.setErrorMessage(message);
            errorDialogController.setErrorDetail(details);

            Stage dialogStage = new Stage();
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            dialogStage.setTitle(title);

            Scene scene = new Scene(root);
            scene.setFill(null);
            dialogStage.setScene(scene);
            errorDialogController.setDialogStage(dialogStage);

            dialogStage.showAndWait();

        } catch (IOException e) {
            e.printStackTrace();
            Alert fallback = new Alert(Alert.AlertType.ERROR);
            fallback.setTitle(title);
            fallback.setHeaderText(subtitle);
            fallback.setContentText(message);
            fallback.showAndWait();
        }

        // Highlight the problematic field
        if (erroredControl != null) {
            erroredControl.getStyleClass().add("error-field");
            erroredControl.requestFocus();
        }
    }

    private void clearErrorStyles() {
        usernameField.getStyleClass().remove("error-field");
        passwordField.getStyleClass().remove("error-field");
    }

    @FXML
    private void handleCancel() {
        usernameField.clear();
        passwordField.clear();
        closeWindow();
    }

    private void closeWindow() {
        Stage stage = (Stage) cancelButton.getScene().getWindow();
        stage.close();
    }
}