module pl.pwsztar {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires org.postgresql.jdbc;
    requires java.desktop;
    requires java.mail;

    opens pl.pwsztar to javafx.fxml;
    exports pl.pwsztar;
}