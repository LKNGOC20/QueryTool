module com.ngoclam.querytool {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.logging;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires org.controlsfx.controls;
    requires org.xerial.sqlitejdbc;


    opens com.ngoclam.querytool to javafx.fxml;
    exports com.ngoclam.querytool;
}