@echo off
setlocal

:: Set the path to JavaFX SDK lib folder
set JAVAFX_PATH=%%~dp0lib

:: Run the application
java --module-path "%%JAVAFX_PATH%%" echo      --add-modules javafx.controls,javafx.fxml echo      --add-opens java.base/java.lang=ALL-UNNAMED echo      --add-opens java.base/java.nio=ALL-UNNAMED echo      --enable-native-access=javafx.graphics echo      -jar ABM-1.0-SNAPSHOT.jar

endlocal
