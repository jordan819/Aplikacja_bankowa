package pl.pwsztar.rest.connect;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.*;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * Umozliwia operacje na podlaczonej bazie danych.
 * Pozwala odczytywac, dodawac, oraz modyfikowac dane, znajdujace sie w zdefiniowanej na stale bazie.
 */
public abstract class Database {

    private static final Logger LOGGER = LoggerFactory.getLogger(Database.class);

    private static Connection connection;

    private static final String user = "2021_zaucha_patryk";
    private static final String pass = "1234";
    private static String url = "jdbc:postgresql://%s:%d/%s";

    /**
     * Status polaczenia z baza, true jesli jest nawiazane.
     */
    public static boolean status;

    /**
     * Pozwala na nawiazanie polaczenia z baza danych.
     */
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
                LOGGER.error(e.getMessage());
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
        LOGGER.info("Stan połączenia z bazą: {}", status);
    }

    /**
     * Pobiera dane o klientach.
     *
     * @return liste klientow
     * @throws SQLException gdy polaczenie zostanie odrzucone
     */
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
            LOGGER.error(e.getMessage());
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
        ResultSet rs = md.getColumns(null, "bank", tableName, "%");
        List<String> columns = new ArrayList<>();
        while (rs.next()) {
            columns.add(rs.getString(4));
        }

        return columns;
    }

    /**
     * Dodaje dane o nowym uzytkowniku do bazy danych.
     *
     * @param customer uzytkownik, ktory zostanie dodany
     */
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

    /**
     * Ustawia w bazie danych konto podanego uzytkownika jak zweryfikowane.
     *
     * @param customer uzytkownik, ktorego konto ma zostac zweryfikowane
     */
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

    /**
     * Ustawia nowy numer konta dla uzytkownika poslugujacego sie podanym adresem email.
     *
     * @param email adres email uzytkownika
     * @param accountNo nowy numer konta
     */
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

    /**
     * Dodaje nowe konto bankowe uzytkownika.
     *
     * @param accountNo nowy numer konta
     * @param customerId nowy numer klienta
     */
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

    /**
     * Modyfikuje aktualny stan konta.
     *
     * @param accountNo numer konta ktore ma zostac zmodyfikowane
     * @param value wartosc, o jaka stan konta zostanie zmieniony
     */
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

    /**
     * Modyfikuje aktualny stan konta z uwzglednieniem jego waluty, oraz podanej jako argument.
     *
     * @param accountNo numer konta ktore ma zostac zmodyfikowane
     * @param value wartosc, o jaka stan konta zostanie zmieniony
     * @param currency waluta jaka zostaje wplacana
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
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

    /**
     * Pobiera liste kont bankowych.
     *
     * @return lista kont
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
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
            LOGGER.error(e.getMessage());
            e.printStackTrace();
        }

        throw new AccountNotFoundException();
    }

    /**
     * Modyfikuje walute, w jakiej przechowywane sa pieniadze na koncie.
     *
     * @param accountNo numer konta ktore ma zostac zmodyfikowane
     * @param value wartosc, o jaka stan konta zostanie zmieniony
     * @param currency waluta jaka zostaje wplacana
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
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

    /**
     * Tworzy informacje o nowej pozyczce.
     *
     * @param accountNo numer konta ktore ma zostac zmodyfikowane
     * @param loanValue wartosc pozyczki
     * @param duration czas trwania pozyczki
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
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

    /**
     * Modyfikuje kwote pozyczki, jaka pozostala do splacenia.
     *
     * @param accountId numer konta, o ktorym informacja zostaje zmieniona
     * @param amount wartosc, o jaka zostaje zmeiniona wartosc pozyczki
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
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

    /**
     * Dezaktywuje konto uzytkownika.
     *
     * @param accountId numer konta, ktore ma zostac zdezaktywowane
     */
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

    /**
     * Waliduje dane logowania pracownika.
     *
     * @param login 3-znakowy login pracownika
     * @param password co najmniej 8-znakowe hasło pracownika
     * @return true, kiedy dane są poprawne
     */
    public static boolean validateEmployee(String login, String password) {
        try{
            String query = "SELECT * FROM bank.employee WHERE login = '" + login + "' AND pass = '" + password + "'";
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void changeAccountStatus(String id, boolean value) {
        try {
            String query = "UPDATE bank.customers SET is_verified = " + value + " WHERE id_customer = " + id;
            connection.createStatement().execute(query);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static Account fetchAccount(String id) throws AccountNotFoundException {
        try{
            String query = "SELECT * FROM bank.accounts WHERE id_account = " + id;
            Statement stmt = connection.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            rs.next();
            String id_customer = rs.getString("id_customer");
            double balance = rs.getDouble("balance");
            String currency = rs.getString("currency");
            double loan = rs.getDouble("loan");
            Date loan_date = rs.getDate("loan_date");
            return new Account(id, id_customer, balance, currency, loan, loan_date);

        } catch (SQLException e) {
            e.printStackTrace();
        }
        throw new AccountNotFoundException();
    }

}
