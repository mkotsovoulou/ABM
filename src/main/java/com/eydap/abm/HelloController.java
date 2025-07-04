package com.eydap.abm;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import java.net.URL;
import java.sql.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML
    private ComboBox<ReportType> reportTypeCombo;
    
    @FXML
    private TextField yearField;
    
    @FXML
    private TextField monthField;
    
    @FXML
    private TextArea messageArea;

    private static final String DB_USER = "sa";
    private static final String DB_PASSWORD = "Sap12345@";

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reportTypeCombo.getItems().addAll(ReportType.values());
        reportTypeCombo.getSelectionModel().selectFirst();
    }

    @FXML
    protected void onRunProcedureClick() {
        // Clear previous messages
        messageArea.clear();
        
        try {
            ReportType selectedReport = reportTypeCombo.getValue();
            if (selectedReport == null) {
                showMessage("Error: Please select a report type");
                return;
            }

            // Input validation
            int year = Integer.parseInt(yearField.getText().trim());
            int month = Integer.parseInt(monthField.getText().trim());
            
            if (month < 1 || month > 12) {
                showMessage("Error: Month must be between 1 and 12");
                return;
            }

            executeProcedure(selectedReport, year, month);
            
        } catch (NumberFormatException e) {
            showMessage("Error: Please enter valid numbers for Year and Month");
        } catch (Exception e) {
            showMessage("Error: " + e.getMessage());
        }
    }

    private void executeProcedure(ReportType reportType, int year, int month) {
        String dbUrl = String.format("jdbc:sqlserver://%s;databaseName=%s;trustServerCertificate=true",
                reportType.getServer(), reportType.getDatabase());
        String callStatement = String.format("{call dbo.%s(?, ?)}", reportType.getProcedure());
        
        try (Connection conn = DriverManager.getConnection(dbUrl, DB_USER, DB_PASSWORD);
             CallableStatement stmt = conn.prepareCall(callStatement)) {
            
            showMessage(String.format("Connecting to database %s on server %s...", 
                    reportType.getDatabase(), reportType.getServer()));
            stmt.setInt(1, year);  // @RefYear
            stmt.setInt(2, month); // @RefMonth
            
            showMessage(String.format("Executing procedure %s for Year: %d, Month: %d",
                    reportType.getProcedure(), year, month));
            stmt.execute();
            showMessage("Procedure executed successfully");
            
        } catch (SQLException e) {
            showMessage("Database error: " + e.getMessage());
        }
    }

    private void showMessage(String message) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
        messageArea.appendText(timestamp + " - " + message + "\n");
    }
}