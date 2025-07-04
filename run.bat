@echo off
setlocal

:: Set the path to your JavaFX SDK lib folder
set JAVAFX_PATH=C:\path\to\javafx-sdk-17.0.6\lib

:: Run the application
java --module-path "%JAVAFX_PATH%" ^
     --add-modules javafx.controls,javafx.fxml ^
     --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/java.nio=ALL-UNNAMED ^
     --enable-native-access=javafx.graphics ^
     -jar target/ABM-1.0-SNAPSHOT.jar

endlocal

rem DOWNLOAD javafx Version: 17.0. https://gluonhq.com/products/javafx/ and copy lib to 
rem ABM-dist/
rem├── lib/
rem│   └── (copy JavaFX SDK lib contents here)
rem├── ABM-1.0-SNAPSHOT.jar
rem└── run.bat

