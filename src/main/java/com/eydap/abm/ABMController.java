package com.eydap.abm;

import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

public class ABMController {

    @FXML
    private ComboBox<ReportType> reportTypeCombo;
    @FXML
    private TextField yearField;
    @FXML
    private TextField monthField;
    @FXML
    private TextArea messageArea;
    @FXML
    private ProgressIndicator progressIndicator;

    private String server;
    private String database;
    private String username;
    private String password;

    public void setConnectionDetails(String server, String database, String username, String password) {
        this.server = server;
        this.database = database;
        this.username = username;
        this.password = password;

        // You might want to display the connection info somewhere, or just use it internally.
        messageArea.setText("Connected to server: " + server + " and database: " + database);
    }
    
    @FXML
    public void initialize() {
        reportTypeCombo.getItems().setAll(ReportType.values());
    }

    @FXML
    private void onRunProcedureClick() {
        ReportType selectedReport = reportTypeCombo.getValue();
        String year = yearField.getText();
        String month = monthField.getText();

        if (selectedReport == null || year.isEmpty() || month.isEmpty()) {
            messageArea.setText("Please select a report type and provide a year and month.");
            return;
        }

        // Here you would use the stored connection details to run the report procedure.
        String message = "Running procedure: " + selectedReport.getProcedure() +
                " on server: " + server +
                " for database: " + database +
                " with user: " + username +
                " for year/month: " + year + "/" + month;
        messageArea.setText(message);
        
        // ... Code to execute the stored procedure ...
    }

    @FXML
    private void onRunRefreshClick() {
        // ... implementation for refreshing counts ...
        messageArea.setText("Refreshing counts...");
    }
}