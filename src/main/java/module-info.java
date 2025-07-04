module com.eydap.abm {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.web;
    requires java.sql;  // Add this line for SQL support

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires net.synedra.validatorfx;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.bootstrapfx.core;
    requires eu.hansolo.tilesfx;
    requires com.almasb.fxgl.all;
    requires com.microsoft.sqlserver.jdbc;  // Add this line for SQL Server JDBC driver

    opens com.eydap.abm to javafx.fxml;
    exports com.eydap.abm;
}