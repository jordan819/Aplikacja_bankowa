/**
 * Glowny modul aplikacji
 */
module pl.pwsztar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.mail;
    requires org.apache.commons.lang3;
    requires java.sql;
    requires com.google.gson;
    requires org.postgresql.jdbc;

    opens pl.pwsztar to javafx.fxml;
    exports pl.pwsztar;
}