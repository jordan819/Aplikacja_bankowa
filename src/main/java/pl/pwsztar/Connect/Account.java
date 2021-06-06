package pl.pwsztar.Connect;

import java.sql.SQLException;
import java.util.List;

public class Account {

    private String accountId;
    private String customerId;
    private double balance;
    private String currency;

    public Account(String accountId) {
        try {
            List<Account> accounts = Database.fetchAccounts();
            for (Account account: accounts) {
                if (account.getAccountId().equals(accountId)) {
                    this.accountId = accountId;
                    this.customerId = account.getCustomerId();
                    this.balance = account.getBalance();
                    this.currency = account.getCurrency();
                    break;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    protected Account(String accountId, String customerId, double balance, String currency) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
    }

    public String getAccountId() {
        return accountId;
    }

    public String getCustomerId() {
        return customerId;
    }

    public double getBalance() {
        return balance;
    }

    public String getCurrency() {
        return currency;
    }
}
