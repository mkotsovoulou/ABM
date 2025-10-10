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

        Task<String> runProcedureTask = new Task<>() {
            @Override
            protected String call() throws Exception {
                updateMessage("Starting procedure...");
                String procedureName = selectedReport.getProcedure();
                String sql = "{call " + procedureName + "(?, ?, ?)}";
                String resultMessage;

                try (Connection conn = DatabaseConnector.getConnection(server, database, username, password);
                     CallableStatement stmt = conn.prepareCall(sql)) {

                    updateMessage("Executing: " + procedureName + " for Year: " + year + ", Month: " + month);
                    stmt.setInt(1, year);
                    stmt.setInt(2, month);
                    stmt.registerOutParameter(3, Types.NVARCHAR);
                    stmt.execute();
                    resultMessage = stmt.getString(3);

                    if (resultMessage == null || resultMessage.isBlank()) {
                        resultMessage = "Procedure '" + procedureName + "' completed successfully with no return message.";
                    }
                } catch (SQLException e) {
                    updateMessage("Database Error:\n" + e.getMessage());
                    throw e;
                }
                return resultMessage;
            }
        };

        progressIndicator.visibleProperty().bind(runProcedureTask.runningProperty());
        runButton.disableProperty().bind(runProcedureTask.runningProperty());
        messageArea.textProperty().bind(runProcedureTask.messageProperty());

        runProcedureTask.setOnSucceeded(event -> {
            messageArea.textProperty().unbind();
            messageArea.setText(runProcedureTask.getValue());
        });

        runProcedureTask.setOnFailed(event -> {
            messageArea.textProperty().unbind();
        });

        new Thread(runProcedureTask).start();
    }

    /**
     * This method is called when the "Refresh Counts" button is clicked.
     * It was missing, causing the application to crash.
     */
    @FXML
    private void onRunRefreshClick() {
        // TODO: Implement the logic for refreshing counts.
        messageArea.setText("Refresh counts functionality is not yet implemented.");
    }
}