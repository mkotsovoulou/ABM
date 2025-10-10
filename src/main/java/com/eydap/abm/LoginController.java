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
    private Label versionLabel; // The new label from the FXML

    /**
     * This method is called by the FXMLLoader when initialization is complete.
     */
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Get the version from the JAR's manifest file
        String version = getClass().getPackage().getImplementationVersion();

        // If running from IDE, version will be null. Show a placeholder.
        if (version == null) {
            versionLabel.setText("Version: DEV");
        } else {
            versionLabel.setText("Version: " + version);
        }
    }

    @FXML
    private void onLoginClick() throws IOException {
        String server = serverField.getText();
        String database = databaseField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            Connection connection = DatabaseConnector.getConnection(server, database, username, password);
            System.out.println("Login Successful!");
            connection.close();

            FXMLLoader loader = new FXMLLoader(getClass().getResource("ABM-view.fxml"));
            Parent root = loader.load();

            Stage stage = (Stage) serverField.getScene().getWindow();
            stage.setTitle("ABM Application");
            stage.setScene(new Scene(root));
            stage.show();

        } catch (SQLException e) {
            System.err.println("Login Failed: " + e.getMessage());
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Login Failed");
            alert.setHeaderText("Could not connect to the database.");
            alert.setContentText("Please check your server, database, username, and password.");
            alert.showAndWait();
        }
    }
}