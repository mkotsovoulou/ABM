package com.eydap.abm;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

// Add this import statement

public class Runner {
    public static void main(String[] args) throws IOException {
        String version = ABMApplication.class.getPackage().getImplementationVersion();
        String latestVersion = getLatestVersion();
        System.out.println("ABM Running version: " + version);
        System.out.println("ABM GitHub version: " + latestVersion);
        if (!version.equals(latestVersion)) {
            // (A) For console app, use prompt below:
            // System.out.println("New version available! Update? (y/n)");
            // if (System.console().readLine().trim().equalsIgnoreCase("y")) { ... }

            // (B) For JavaFX, show a dialog instead:
            javafx.application.Platform.startup(() -> {
                Alert alert = new Alert(Alert.AlertType.CONFIRMATION,
                        "A new version (" + latestVersion + ") is available.\nUpdate now?",
                        ButtonType.YES, ButtonType.NO);
                alert.setTitle("Update Available");
                alert.setHeaderText("Update Available");
                Optional<ButtonType> result = alert.showAndWait();
                if (result.isPresent() && result.get() == ButtonType.YES) {
                    try {
                        String downloadUrl = "https://github.com/mkotsovoulou/ABM/releases/download/v" +
                                latestVersion + "/ABM-" + latestVersion + ".jar";
                        File downloadedJar = downloadNewJar(downloadUrl, latestVersion);
                        launchJar(downloadedJar);
                    } catch (Exception e) {
                        Alert fail = new Alert(Alert.AlertType.ERROR, "Update failed: " + e.getMessage());
                        fail.showAndWait();
                        System.exit(1);
                    }
                } else {
                    // Start the app as normal if the user says NO
                    Application.launch(ABMApplication.class, args);
                }
            });
        } else {
            Application.launch(ABMApplication.class, args);
        }
    }

    public static String getLatestVersion() throws IOException {
        // URL to raw text file in your GitHub repo (e.g., https://raw.githubusercontent.com/youruser/yourrepo/main/latest-version.txt)
        URL url = new URL("https://raw.githubusercontent.com/mkotsovoulou/ABM-VersionControl/main/latest-version.txt");
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()))) {
            return reader.readLine().trim();
        }
    }


    public static File downloadNewJar(String downloadUrl, String newVersion) throws IOException {
        File tempJar = new File("ABM-" + newVersion + ".jar");
        try (InputStream in = new URL(downloadUrl).openStream()) {
            Files.copy(in, tempJar.toPath(), StandardCopyOption.REPLACE_EXISTING);
        }
        return tempJar;
    }

    public static void launchJar(File jarFile) throws IOException {
        String javaBin = System.getProperty("java.home") + File.separator + "bin" + File.separator + "java";
        ProcessBuilder builder = new ProcessBuilder(javaBin, "-jar", jarFile.getAbsolutePath());
        builder.start();
        System.exit(0); // Exit current app
    }

}