package com.eydap.abm;

import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Types;

public class ABMController {

    @FXML
    private ComboBox<ReportType> reportTypeCombo;
    @FXML
    private TextField yearField;
    @FXML
    private TextField monthField;
    @FXML
    private Button runButton;
    @FXML
    private ProgressIndicator progressIndicator;
    @FXML
    private TextArea messageArea;

    private String server;
    private String database;
    private String username;
    private String password;

    public void setConnectionDetails(String server, String database, String username, String password) {
        this.server = server;
        this.database = database;
        this.username = username;
        this.password = password;
        messageArea.setText("Connected to database: " + database + "\nPlease select a report and click 'Run'.");
    }

    @FXML
    public void initialize() {
        reportTypeCombo.getItems().setAll(ReportType.values());
    }

    @FXML
    private void onRunProcedureClick() {
        ReportType selectedReport = reportTypeCombo.getValue();
        String yearText = yearField.getText();
        String monthText = monthField.getText();

        if (selectedReport == null || yearText.isBlank() || monthText.isBlank()) {
            messageArea.setText("Error: Please select a report type, year, and month.");
            return;
        }

        int year;
        int month;
        try {
            year = Integer.parseInt(yearText);
            month = Integer.parseInt(monthText);
        } catch (NumberFormatException e) {
            messageArea.setText("Error: Year and month must be valid numbers.");
            return;
        }

        // The Task now returns a String (the message from the procedure)
        Task<String> runProcedureTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Starting procedure...");
                String procedureName = selectedReport.getProcedure();
                // Add a '?' for the output parameter
                String sql = "{call " + procedureName + "(?, ?, ?)}";
                String resultMessage;

                try (Connection conn = DatabaseConnector.getConnection(server, database, username, password);
                     CallableStatement stmt = conn.prepareCall(sql)) {

                    updateMessage("Executing: " + procedureName + " for Year: " + year + ", Month: " + month);

                    // 1. Set IN parameters
                    stmt.setInt(1, year);
                    stmt.setInt(2, month);

                    // 2. Register OUT parameter (assuming it's the 3rd one and returns a string)
                    stmt.registerOutParameter(3, Types.NVARCHAR);

                    // 3. Execute the procedure
                    stmt.execute();

                    // 4. Retrieve the OUT parameter value
                    resultMessage = stmt.getString(3);

                    // Provide a default message if the procedure returns null or an empty string
                    if (resultMessage == null || resultMessage.isBlank()) {
                        resultMessage = "Procedure '" + procedureName + "' completed successfully with no return message.";
                    }

                } catch (SQLException e) {
                    updateMessage("Database Error:\n" + e.getMessage());
                    throw e; // Cause the task to fail
                }
                return resultMessage; // Return the final message from the procedure
            }
        };

        progressIndicator.visibleProperty().bind(runProcedureTask.runningProperty());
        runButton.disableProperty().bind(runProcedureTask.runningProperty());
        messageArea.textProperty().bind(runProcedureTask.messageProperty());

        // When the task succeeds, unbind the message property and set the final result.
        runProcedureTask.setOnSucceeded(event -> {
            messageArea.textProperty().unbind();
            messageArea.setText(runProcedureTask.getValue()); // getValue() retrieves the returned string
        });

        runProcedureTask.setOnFailed(event -> {
            messageArea.textProperty().unbind(); // Unbind to show the final error message
        });

        new Thread(runProcedureTask).start();
    }
}