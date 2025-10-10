package com.eydap.abm;

import javafx.application.Application;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Optional;

public class Runner {
    public static void main(String[] args) throws IOException {
        String version = ABMApplication.class.getPackage().getImplementationVersion();
        System.out.println("ABM Running version: " + version);

        // Your update logic can remain here...

        // The critical change is this line:
        ABMApplication.main(args);
    }

    // ... all your other helper methods (getLatestVersion, downloadNewJar, etc.) remain the same
    
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