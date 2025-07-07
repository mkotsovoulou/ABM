package com.eydap.abm;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.*;
import java.time.YearMonth;

public class HelloController {

    @FXML
    private ComboBox<ReportType> reportTypeCombo;
    @FXML
    private TextField yearField;
    @FXML
    private TextField monthField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private TextArea messageArea;
    @FXML
    private ProgressIndicator progressIndicator;

    @FXML
    public void initialize() {
        reportTypeCombo.getItems().setAll(ReportType.values());
        yearField.setPromptText("e.g., " + YearMonth.now().getYear());
        monthField.setPromptText("e.g., " + YearMonth.now().getMonthValue());
    }

    @FXML
    private void onRunProcedureClick() {
        ReportType selectedReport = reportTypeCombo.getValue();
        String yearText = yearField.getText();
        String monthText = monthField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (selectedReport == null) {
            messageArea.setText("Please select a report type.");
            return;
        }
        if (yearText.isBlank() || monthText.isBlank() || username.isBlank() || password.isBlank()) {
            messageArea.setText("Please fill in all fields, including username and password.");
            return;
        }

        try {
            int year = Integer.parseInt(yearText.trim());
            int month = Integer.parseInt(monthText.trim());

            if (month < 1 || month > 12) {
                messageArea.setText("Month must be between 1 and 12.");
                return;
            }
            
            executeProcedure(selectedReport, year, month, username, password);

        } catch (NumberFormatException e) {
            messageArea.setText("Invalid year or month. Please enter numbers.");
        }
    }
    
    private void executeProcedure(ReportType selectedReport, int year, int month, String username, String password) {
        progressIndicator.setVisible(true);
        // FIX: Changed getProcedureName() to getProcedure()
        messageArea.setText("Executing procedure: " + selectedReport.getProcedure() + "...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = "jdbc:sqlserver://" + selectedReport.getServer() +
                             ";databaseName=" + selectedReport.getDatabase() +
                             ";encrypt=true;trustServerCertificate=true;";

            // FIX: Changed getProcedureName() to getProcedure()
            try (Connection conn = DriverManager.getConnection(url, username, password);
                 CallableStatement cstmt = conn.prepareCall("{call " + selectedReport.getProcedure() + "(?, ?}")) {

                cstmt.setInt(1, year);
                cstmt.setInt(2, month);
             //   cstmt.registerOutParameter(3, Types.VARCHAR);
                cstmt.execute();
                    
                    return cstmt.getString(3);
                }
            }
        };

        task.setOnSucceeded(event -> {
            progressIndicator.setVisible(false);
            messageArea.setText("Procedure executed successfully.\nResult: " + task.getValue());
        });

        task.setOnFailed(event -> {
            progressIndicator.setVisible(false);
            Throwable e = task.getException();
            messageArea.setText("Error executing procedure:\n" + e.getMessage());
        });

        new Thread(task).start();
    }
}