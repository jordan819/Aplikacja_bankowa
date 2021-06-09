package pl.pwsztar.Connect;

import pl.pwsztar.AccountNotFoundException;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public abstract class Database {

    private static Connection connection;

    private static final String user = "2021_zaucha_patryk";
    private static final String pass = "1234";
    private static String url = "jdbc:postgresql://%s:%d/%s";
    public static boolean status;

    public static void makeConnection() {
        String host = "195.150.230.210";
        String database = "2021_zaucha_patryk";
        int port = 5434;
        url = String.format(url, host, port, database);

        Thread thread = new Thread(() -> {
            try {
                Class.forName("org.postgresql.Driver");
                connection = DriverManager.getConnection(url, user, pass);
                status = true;

            } catch (Exception e) {
                status = false;
                System.out.print(e.getMessage());
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
            status = false;
        }
        System.out.println("connection status:" + status);
    }

    public static List<CustomerDto> fetchCustomers() throws SQLException {

        boolean isVerified;

        try {
            List<String[]> fetchedCustomers = getTableContent("customers");
            List<CustomerDto> customers = new ArrayList<>();
            CustomerDto customer;
            for (String[] row: fetchedCustomers) {

                isVerified = row[7].equals("t");

                customer = new CustomerDto(row[0], row[1], row[2], row[3], row[4],
                        row[5], row[6], isVerified);
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
        String schemaName = "bank";

        String query = "SELECT * FROM " + schemaName + "." + tableName;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery(query);

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

    public static void addCustomer(Customer customer) {

        Thread thread = new Thread(() -> {
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
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void verifyCustomer(CustomerDto customer) {

        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.customers " +
                    "SET is_verified = " + true +
                    " WHERE id_customer = " + customer.getIdCustomer() + ";";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
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

        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.customers " +
                    "SET id_account = '" + accountNo +
                    "' WHERE email = '" + email + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addAccount(String accountNo, String customerId) {

        Thread thread = new Thread(() -> {
            String query = "INSERT INTO bank.accounts (id_account, id_customer, balance, currency) VALUES ('" +
                    accountNo + "', " +
                    customerId + ", " +
                    0 + ", 'PLN'" +  ");";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateAccountBalance(String accountNo, String value) {

        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.accounts " +
                    "SET balance = balance + " + value +
                    " WHERE id_account = '" + accountNo + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public static void updateAccountBalance(String accountNo, String value, String currency)
            throws AccountNotFoundException {

        List<Account> accounts = fetchAccounts();
        String targetCurrency = null;

        for (Account account: accounts) {
            if (account.getAccountId().equals(accountNo)) {
                targetCurrency = account.getCurrency();
                break;
            }
        }

        if (targetCurrency == null)
            throw new AccountNotFoundException();

        value = String.valueOf(Money.exchange(Double.parseDouble(value), currency, targetCurrency));


        String finalValue = value;
        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.accounts " +
                    "SET balance = balance + " + finalValue +
                    " WHERE id_account = '" + accountNo + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static List<Account> fetchAccounts() throws AccountNotFoundException {

        try {
            List<String[]> fetchedAccounts = getTableContent("accounts");
            List<Account> accounts = new ArrayList<>();
            Account account;
            for (String[] row: fetchedAccounts) {

                Double loan;
                if (row[4] == null)
                    loan = null;
                else
                    loan = Double.parseDouble(row[4]);

                Date date;
                if(row[5] == null)
                    date = null;
                else
                    date = Date.valueOf(row[5]);

                account = new Account(row[0], row[1], Double.parseDouble(row[2]), row[3],
                        loan, date);
                accounts.add(account);
            }

            return accounts;

        } catch (SQLException e) {
            status = false;
            System.out.print(e.getMessage());
            e.printStackTrace();
        }

        throw new AccountNotFoundException();
    }

    public static void updateAccountCurrency(String accountNo, String currency, double value)
            throws AccountNotFoundException {

        Account account = new Account(accountNo);
        Double actualLoan = account.getLoan();
        Double newLoan = null;
        if (actualLoan != null)
            newLoan = Money.exchange(actualLoan, account.getCurrency(), currency);

        Double finalNewLoan = newLoan;
        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.accounts " +
                    "SET balance = " + value + ", currency = '" + currency + "', loan = " + finalNewLoan +
                    " WHERE id_account = '" + accountNo + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void createLoanInformation(String accountNo, double loanValue, int duration)
            throws AccountNotFoundException {
        new Account(accountNo);

        Calendar c = Calendar.getInstance();
        c.add(Calendar.MONTH, duration);
        long dateAsMilliseconds = c.getTimeInMillis();
        Date date = new Date(dateAsMilliseconds);

        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.accounts " +
                    "SET loan_date = '" + date + "', loan = " + loanValue +
                    " WHERE id_account = '" + accountNo + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void updateLoanInformation(String accountId, Double amount) throws AccountNotFoundException {
        Account account = new Account(accountId);
        Thread thread = new Thread(() -> {
            String query;
            if (account.getLoan().equals(amount))
                query = "UPDATE bank.accounts " +
                        "SET loan = null " + ", loan_date = null" +
                        " WHERE id_account = '" + accountId + "';";
            else
            query = "UPDATE bank.accounts " +
                    "SET loan = loan - " + amount +
                    " WHERE id_account = '" + accountId + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        });
        thread.start();
        try {
            thread.join();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void deactivateAccount(String accountId) {
        Thread thread = new Thread(() -> {
            String query = "UPDATE bank.customers " +
                    "SET verification_code = null, is_verified = false " +
                    "WHERE id_account = '" + accountId + "';";
            try {
                connection.createStatement().execute(query);
            } catch (SQLException e) {
                e.printStackTrace();
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
