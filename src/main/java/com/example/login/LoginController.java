package com.example.login;

import com.example.error.ErrorController;
import javafx.application.Platform;
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

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());
    private static final String STYLESHEET_PATH = "/css/stylesheet.css";
    private static final String DASHBOARD_FXML = "/fxml/dashboard.fxml";
    private static final String ERROR_DIALOG_FXML = "/fxml/errorDialog.fxml";

    // Hardcoded credentials – replace with real authentication
    private static final String VALID_USERNAME = "admin";
    private static final String VALID_PASSWORD = "password";

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
        addStylesheetToScene();
        setupEventHandlers();
        setupInputValidation();
        Platform.runLater(() -> usernameField.requestFocus());
    }

    private void addStylesheetToScene() {
        Scene scene = usernameField.getScene();
        if (scene != null) {
            String css = getClass().getResource(STYLESHEET_PATH).toExternalForm();
            if (!scene.getStylesheets().contains(css)) {
                scene.getStylesheets().add(css);
            }
        } else {
            LOGGER.warning("Scene not available for stylesheet injection");
        }
    }

    private void setupEventHandlers() {
        loginButton.setOnAction(event -> handleLogin());
        cancelButton.setOnAction(event -> handleCancel());
        // Pressing Enter in either field triggers login
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
        // Replace with real authentication (database, LDAP, etc.)
        return VALID_USERNAME.equals(username) && VALID_PASSWORD.equals(password);
    }

    private void loginSuccess() {
        clearErrorStyles();
        LOGGER.info("Login successful for user: " + usernameField.getText());

        // Close login window
        Stage loginStage = (Stage) cancelButton.getScene().getWindow();
        loginStage.close();

        openDashboard();
    }

    private void openDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(DASHBOARD_FXML));
            Parent root = loader.load();

            Stage dashboardStage = new Stage();
            dashboardStage.setTitle("BYOD Monitoring System - Dashboard");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource(STYLESHEET_PATH).toExternalForm());
            dashboardStage.setScene(scene);
            dashboardStage.centerOnScreen();

            // ========== DISABLE MAXIMIZED MODE ==========
            dashboardStage.setMaximized(false);
            // Optionally allow resizing but not forced maximized
            dashboardStage.setResizable(true);

            dashboardStage.show();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Failed to load dashboard", e);
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
            FXMLLoader loader = new FXMLLoader(getClass().getResource(ERROR_DIALOG_FXML));
            StackPane root = loader.load();

            errorDialogController = loader.getController();
            errorDialogController.setTitle(title);
            errorDialogController.setSubtitle(subtitle);
            errorDialogController.setErrorMessage(message);
            if (details != null && !details.isBlank()) {
                errorDialogController.setErrorDetail(details);
            }

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
            LOGGER.log(Level.WARNING, "Could not load custom error dialog, using fallback Alert", e);
            Alert fallback = new Alert(Alert.AlertType.ERROR);
            fallback.setTitle(title);
            fallback.setHeaderText(subtitle);
            fallback.setContentText(message + (details != null ? "\nDetails: " + details : ""));
            fallback.showAndWait();
        }

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
        if (stage != null) {
            stage.close();
        }
    }
}