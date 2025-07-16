package com.eydap.abm;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class ABMApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoader = new FXMLLoader(ABMApplication.class.getResource("ABM-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 500, 400);  // Increased window size
        stage.setTitle("ABM Process Fact Stats");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}