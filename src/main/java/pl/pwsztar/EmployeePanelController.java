package pl.pwsztar;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import pl.pwsztar.Connect.Database;

import java.sql.SQLException;
import java.util.List;
import java.util.Objects;

public class EmployeePanelController {

    @FXML
    private Label infoDisplay;

    @FXML
    private TableView<String[]> table;

    @FXML
    private void initialize() throws SQLException {
        refreshTable();
    }

    @FXML
    private void changeStatus() throws SQLException {
        String[] selectedRow = table.getSelectionModel().getSelectedItem();

        if (Objects.equals(selectedRow[7], "t")) {
            Database.changeAccountStatus(selectedRow[0], false);
            infoDisplay.setText("Konto zostało dezaktywowane pomyślnie");
        } else {
            Database.changeAccountStatus(selectedRow[0], true);
            infoDisplay.setText("Konto zostało aktywowane pomyślnie");
        }
        refreshTable();
        infoDisplay.setVisible(true);
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

    private void refreshTable() throws SQLException {
        List<String> names = Database.getColumnNames("customers");
        List<String[]> content = Database.getTableContent("customers");
        populateTable(names, content);
    }

}
