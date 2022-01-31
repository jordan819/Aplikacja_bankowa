package pl.pwsztar;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import pl.pwsztar.Connect.Database;

import java.lang.reflect.Array;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class EmployeePanelController {

    @FXML
    private Label infoDisplay;

    @FXML
    private TableView table;

    @FXML
    private ListView unselectedMailsDisplay;

    @FXML
    private void initialize() throws SQLException {
        // List<String> names = Arrays.asList("kol1", "kol2", "kol3", "kol4", "kol5");
        // List<String[]> content = new ArrayList<>();
        // String[] s1 = {"opis1", "opis2", "opis3"};
        // content.add(s1);
        // content.add(s1);

        List<String> names = Database.getColumnNames("customer");
        List<String[]> content = Database.getTableContent("customer");


        populateTable(names, content);
    }

    public void goToPayIn(ActionEvent actionEvent) {
    }

    public void goToDeactivate(ActionEvent actionEvent) {
    }

    public void goToTakeLoan(ActionEvent actionEvent) {
    }

    private void populateTable(List<String> columnNames, List<String[]> tableContent) {

        ObservableList<String[]> data = FXCollections.observableArrayList();
        data.addAll(tableContent);

        for (int i = 0; i < tableContent.get(0).length; i++) {
            TableColumn tc = new TableColumn(columnNames.get(i));

            final int colNum = i;
            tc.setCellValueFactory((Callback<TableColumn.CellDataFeatures<String[], String>,
                    ObservableValue<String>>) stringCellDataFeatures ->
                    new SimpleStringProperty((stringCellDataFeatures.getValue()[colNum])));

            table.getColumns().add(tc);

        }

        table.setItems(data);
    }
}
