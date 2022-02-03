package pl.pwsztar;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.util.Callback;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.lang.reflect.Type;
import java.util.List;
import java.util.Objects;

public class EmployeePanelController {

    @FXML
    private Label infoDisplay;

    @FXML
    private TableView<String[]> table;

    @FXML
    private void initialize() {
        refreshTable();
    }

    @FXML
    private void changeStatus() {
        String[] selectedRow = table.getSelectionModel().getSelectedItem();

        if (Objects.equals(selectedRow[7], "t")) {
            changeAccountStatus(selectedRow[0], false);

            infoDisplay.setText("Konto zostało dezaktywowane pomyślnie");
        } else {
            changeAccountStatus(selectedRow[0], true);
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

    private void changeAccountStatus(String id, boolean status) {
        try{
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/account/"
                    + id + "/" + status);
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void refreshTable() {
        table.getItems().clear();
        table.getColumns().clear();
        List<String> names = null;
        List<String[]> content = null;
        try {
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/employee/table/content");

            final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
            final HttpEntity entity = response.getEntity();

            final String json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.

            // zamiana Stringa na List<String[]>
            final Gson gson = new Gson();
            final Type type = new TypeToken<List<String[]>>(){}.getType();
            content = gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }

        try {
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpGet request = new HttpGet("http://127.0.0.1:8080/bank/employee/table/headers");

            final HttpResponse response = client.execute(request);  // Otrzymujemy odpowiedz od serwera.
            final HttpEntity entity = response.getEntity();

            final String json = EntityUtils.toString(entity);   // Na tym etapie odczytujemy JSON'a, ale jako String.

            // zamiana Stringa na List<String>
            final Gson gson = new Gson();
            final Type type = new TypeToken<List<String>>(){}.getType();
            names = gson.fromJson(json, type);
        } catch (IOException e) {
            e.printStackTrace();
        }
        populateTable(names, content);
    }

    public void forceLoanPay() {

        String[] selectedRow = table.getSelectionModel().getSelectedItem();

        try{
            final HttpClient client = HttpClientBuilder.create().build();
            final HttpPut request = new HttpPut("http://127.0.0.1:8080/bank/employee/forceLoanPay/"
                                                    + selectedRow[5]);
            client.execute(request);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
