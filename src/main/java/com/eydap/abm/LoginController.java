package com.eydap.abm;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class LoginController implements Initializable {

    @FXML
    private TextField serverField;
    @FXML
    private TextField databaseField;
    @FXML
    private TextField usernameField;
    @FXML
    private PasswordField passwordField;
    @FXML
    private Label versionLabel;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        String version = getClass().getPackage().getImplementationVersion();
        versionLabel.setText("Version: " + (version == null ? "DEV" : version));
    }

    @FXML
    private void onLoginClick() {
        String server = serverField.getText();
        String database = databaseField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // 1. First, test the database connection and close it immediately.
            try (Connection connection = DatabaseConnector.getConnection(server, database, username, password)) {
                System.out.println("Login Successful! Credentials are valid.");
            }

            // 2. If the connection was successful, proceed to load the next scene.
            FXMLLoader loader = new FXMLLoader(getClass().getResource("ABM-view.fxml"));
            Parent root = loader.load();

            // 3. Get the controller of the new screen
            ABMController abmController = loader.getController();

            // 4. Pass the connection details to the ABMController
            abmController.setConnectionDetails(server, database, username, password);

            // 5. Show the new scene
            Stage stage = (Stage) serverField.getScene().getWindow();
            stage.setTitle("ABM Application");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            // This 'catch' is specifically for database connection errors.
            System.err.println("Login Failed: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Could not connect to the database.");
            alert.setContentText("Please check your credentials and network connection.\n\nDetails: " + e.getMessage());
            alert.showAndWait();
        } catch (IOException e) {
            // This 'catch' is for FXML loading and other UI errors.
            System.err.println("Failed to load application view: " + e.getMessage());
            e.printStackTrace(); // Very useful for debugging in the console
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Application Error");
            alert.setHeaderText("Failed to load the main application screen.");
            alert.setContentText("This is usually caused by an error in the FXML file.\n\nDetails: " + e.getMessage());
            alert.showAndWait();
        }
    }
}