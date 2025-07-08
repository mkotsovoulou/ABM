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
        executeSelectStatements("sa", "Sap12345@");
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

    private void executeSelectStatements(String username, String password) {
        progressIndicator.setVisible(true);
        messageArea.setText("Ανάκτηση τελευταίου φορτωμένου έτους/μήνα στα ABM...");

        Task<String> task = new Task<>() {
            @Override
            protected String call() throws Exception {
                String url = "jdbc:sqlserver://" + "2022SRV"  +
                        ";databaseName=" +  "dss_dev" +
                        ";encrypt=true;trustServerCertificate=true;";

                StringBuilder result = new StringBuilder();

                try (Connection conn = DriverManager.getConnection(url, username, password)) {
                    // SELECT 1
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs1 = stmt.executeQuery("SELECT max(month_code) FROM fact_stats")) {
                        if (rs1.next()) {
                            result.append("Τελευταίος μηνας που εχει φορτωθεί στο fact_stats: ").append(rs1.getInt(1)).append("\n");
                        }
                    }

                    // SELECT 2
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs2 = stmt.executeQuery("SELECT max(month_code) FROM fact_stats2")) {
                        if (rs2.next()) {
                            result.append("Τελευταίος μηνας που εχει φορτωθεί στο fact_stats2: ").append(rs2.getDate(1)).append("\n");
                        }
                    }

                    // SELECT 3
                    try (Statement stmt = conn.createStatement();
                         ResultSet rs3 = stmt.executeQuery("SELECT max(month_code) FROM fact_stats3")) {
                        if (rs3.next()) {
                            result.append("Τελευταίος μηνας που εχει φορτωθεί στο water report: ").append(rs3.getString(1)).append("\n");
                        }
                    }

                    return result.toString();
                }
            }
        };

        task.setOnSucceeded(e -> {
            progressIndicator.setVisible(false);
            messageArea.setText(task.getValue());
        });

        task.setOnFailed(e -> {
            progressIndicator.setVisible(false);
            messageArea.setText("Error: " + task.getException().getMessage());
        });

        new Thread(task).start();
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
                 CallableStatement cstmt = conn.prepareCall("{call " + selectedReport.getProcedure() + "(?, ?, ?)}")) {

                cstmt.setInt(1, year);
                cstmt.setInt(2, month);
                 cstmt.registerOutParameter(3, Types.VARCHAR);
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