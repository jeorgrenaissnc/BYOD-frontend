package com.example.dashboard;

import javafx.fxml.FXML;
import javafx.scene.control.Label;

public class DashboardController {

    @FXML
    private Label welcomeLabel;

    @FXML
    public void initialize() {
        // Any initialization logic for the dashboard
        System.out.println("Dashboard loaded successfully");
    }
}