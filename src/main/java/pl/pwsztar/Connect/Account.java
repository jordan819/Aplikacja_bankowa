package pl.pwsztar.Connect;

import pl.pwsztar.AccountNotFoundException;

import java.util.List;

public class Account {

    private final String accountId;
    private final String customerId;
    private final double balance;
    private final String currency;

    public Account(String accountId) throws AccountNotFoundException{
        List<Account> accounts = Database.fetchAccounts();
        for (Account account: accounts) {
            if (account.getAccountId().equals(accountId)) {
                this.accountId = accountId;
                this.customerId = account.getCustomerId();
                this.balance = account.getBalance();
                this.currency = account.getCurrency();
                return;
            }
        }
        throw new AccountNotFoundException();
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
