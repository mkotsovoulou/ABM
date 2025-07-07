package com.eydap.abm;

import javafx.application.Application;

// Add this import statement
import com.eydap.abm.HelloApplication;

public class Runner {
    public static void main(String[] args) {
        // Use Application.launch() to properly start the JavaFX application
        // This is the standard way and helps avoid module-related class loading issues.
        Application.launch(HelloApplication.class, args);
    }
}