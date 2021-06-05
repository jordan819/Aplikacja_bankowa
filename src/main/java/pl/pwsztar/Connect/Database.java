package pl.pwsztar.Connect;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class Database {

    private static Connection connection;

    private final String user = "2021_zaucha_patryk";
    private final String pass = "1234";
    private String url = "jdbc:postgresql://%s:%d/%s";
    public static boolean status;

    public Database() {
        String host = "195.150.230.210";
        String database = "2021_zaucha_patryk";
        int port = 5434;
        this.url = String.format(this.url, host, port, database);
        connect();
        System.out.println("connection status:" + status);
    }

    private void connect() {
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    Class.forName("org.postgresql.Driver");
                    connection = DriverManager.getConnection(url, user, pass);
                    status = true;

                } catch (Exception e) {
                    status = false;
                    System.out.print(e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            this.status = false;
        }
    }

    public static List<CustomerDto> fetchCustomers() throws SQLException {

        try {
            List<String[]> fetchedCustomers = getTableContent("customers");
            List<CustomerDto> customers = new ArrayList<>();
            CustomerDto customer;
            for (String[] row: fetchedCustomers) {
                customer = new CustomerDto(row[0], row[1], row[2], row[3], row[4],
                        row[5], row[6], Boolean.getBoolean(row[7]));
                customers.add(customer);
            }
            return customers;

        } catch (Exception e) {
            status = false;
            System.out.print(e.getMessage());
            e.printStackTrace();
        }

        return null;
    }


    public static List<String[]> getTableContent(String tableName) throws SQLException {
        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getColumns(null, "%", tableName, null);

        // znalezienie nazwy schematu, w którym jest tabela
        rs.next();
        String schemaName = "bank";

        String query = "SELECT * FROM " + schemaName + "." + tableName;
        Statement stmt = connection.createStatement();
        rs = stmt.executeQuery(query);

        List<String> columns = getColumnNames(tableName);


        int width = columns.size();

        String[] line;
        List<String[]> data = new ArrayList<>();

        int i;
        while(rs.next()){
            i = 0;
            line = new String[width];
            for (String item: columns){
                line[i++] = rs.getString(item);
            }
            data.add(line);
        }

        return data;
    }


    public static List<String> getColumnNames(String tableName) throws SQLException {

        DatabaseMetaData md = connection.getMetaData();
        ResultSet rs = md.getColumns(null, null, tableName, "%");
        List<String> columns = new ArrayList<>();
        while (rs.next()) {
            columns.add(rs.getString(4));
        }

        return columns;
    }

    public static boolean addCustomer(Customer customer) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String query = "INSERT INTO bank.customers (first_name, last_name, email, password, " +
                        "id_account, verification_code, is_verified) VALUES ('" +
                        customer.getFirstName() + "', '" +
                        customer.getLastName() + "', '" +
                        customer.getEmail() + "', '" +
                        customer.getPassword() + "', '" +
                        customer.getIdAccount() + "', '" +
                        customer.getVerificationCode() + "', " +
                        customer.isVerified() + ");";
                try {
                    connection.createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void verifyCustomer(CustomerDto customer) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE bank.customers" +
                        "SET is_verified = " + true +
                        "WHERE id_customer = " + customer.getIdCustomer() + ";";
                try {
                    connection.createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void setAccountNo(String email, String accountNo) {

        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                String query = "UPDATE bank.customers " +
                        "SET id_account = '" + accountNo +
                        "' WHERE email = '" + email + "';";
                try {
                    connection.createStatement().execute(query);
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
