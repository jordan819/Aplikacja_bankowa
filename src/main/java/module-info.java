module pl.pwsztar {
    requires javafx.controls;
    requires javafx.fxml;

    opens pl.pwsztar to javafx.fxml;
    exports pl.pwsztar;
}