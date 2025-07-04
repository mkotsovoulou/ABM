@echo off
java --add-opens java.base/java.lang=ALL-UNNAMED ^
     --add-opens java.base/java.nio=ALL-UNNAMED ^
     --enable-native-access=javafx.graphics ^
     -jar ABM-1.0-SNAPSHOT.jar
