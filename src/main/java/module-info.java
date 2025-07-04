module com.eydap.abm {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires com.microsoft.sqlserver.jdbc;

    opens com.eydap.abm to javafx.fxml;
    exports com.eydap.abm;
}