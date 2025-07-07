package com.eydap.abm;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.YearMonth;

public class HelloController {

    @FXML
    private ComboBox<ReportType> reportTypeCombo;

    @FXML
    private TextField yearField;

    @FXML
    private TextField monthField;

    @FXML
    private TextArea messageArea;

    /**
     * This method is called by the FXMLLoader when initialization is complete.
     * It populates the ComboBox with values from the ReportType enum.
     */
    @FXML
    public void initialize() {
        // Populate the ComboBox with all the values from the ReportType enum.
        // The ComboBox will automatically use the toString() method for display.
        reportTypeCombo.getItems().setAll(ReportType.values());

        // Set prompt text to guide the user.
        yearField.setPromptText("e.g., " + YearMonth.now().getYear());
        monthField.setPromptText("e.g., " + YearMonth.now().getMonthValue());
    }

    /**
     * Handles the click event for the "Run Procedure" button.
     * It validates user input before proceeding to use the selected report data.
     */
    @FXML
    protected void onRunProcedureClick() {
        ReportType selectedReport = reportTypeCombo.getValue();
        String yearText = yearField.getText();
        String monthText = monthField.getText();

        // 1. Validate that a report type has been selected from the ComboBox.
        if (selectedReport == null) {
            messageArea.setText("Error: Please select a report type.");
            return;
        }

        // 2. Validate that the year and month fields are not blank.
        if (yearText.isBlank() || monthText.isBlank()) {
            messageArea.setText("Error: Year and Month fields cannot be empty.");
            return;
        }

        int year;
        int month;

        // 3. Validate that year and month are valid integers.
        try {
            year = Integer.parseInt(yearText.trim());
            month = Integer.parseInt(monthText.trim());
        } catch (NumberFormatException e) {
            messageArea.setText("Error: Please enter valid numbers for Year and Month.");
            return;
        }

        // If all validations pass, show a message that the process is starting.
        messageArea.setText("Validation successful! Starting procedure...");

        // Run the database logic on a background thread to keep the UI responsive.
        new Thread(() -> executeProcedure(selectedReport, year, month)).start();
    }

    /**
     * Executes the selected stored procedure on a background thread.
     * 
     * Note: For Windows integrated authentication to work, you need to:
     * 1. Download the appropriate sqljdbc_auth.dll file from Microsoft
     * 2. Place it in a directory that's in your Java library path (e.g., JRE/bin directory)
     * 3. Or specify the directory using -Djava.library.path=<path_to_dll_directory> when running the application
     *
     * @param selectedReport The report type containing database and procedure details.
     * @param year           The year parameter for the stored procedure.
     * @param month          The month parameter for the stored procedure.
     */
    private void executeProcedure(ReportType selectedReport, int year, int month) {
        // Using the newer authentication syntax for SQL Server JDBC driver
        String url = "jdbc:sqlserver://" + selectedReport.getServer() + ";databaseName=" + selectedReport.getDatabase() + ";authentication=ActiveDirectoryIntegrated;trustServerCertificate=true";

        try (Connection connection = DriverManager.getConnection(url);
             CallableStatement callableStatement = connection.prepareCall("{call " + selectedReport.getProcedure() + "(?, ?)}")) {

            callableStatement.setInt(1, year);
            callableStatement.setInt(2, month);
            callableStatement.execute();

            // If successful, update the UI on the JavaFX Application Thread.
            Platform.runLater(() -> {
                messageArea.setText("Successfully executed procedure: " + selectedReport.getProcedure() + "\n\n" +
                        "Report: " + selectedReport.getDisplayName() + "\n" +
                        "Server: " + selectedReport.getServer() + "\n" +
                        "Database: " + selectedReport.getDatabase() + "\n" +
                        "For Date: " + month + "/" + year);
            });

        } catch (SQLException e) {
            // If there's an SQL error, update the UI with a detailed error message.
            Platform.runLater(() -> {
                messageArea.setText("SQL Error: Could not connect to the database or execute the procedure.\n\n" +
                        "Details: " + e.getMessage() + "\n\n" +
                        "Please check the following:\n" +
                        "1. The server '" + selectedReport.getServer() + "' is online.\n" +
                        "2. The database '" + selectedReport.getDatabase() + "' exists.\n" +
                        "3. You have the necessary permissions.");
            });
        }
    }
}
