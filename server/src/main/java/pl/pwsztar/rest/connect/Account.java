package pl.pwsztar.rest.connect;

import pl.pwsztar.AccountNotFoundException;
import pl.pwsztar.rest.connect.Database;

import java.sql.Date;
import java.util.List;

/**
 * Reprezentuje konto klienta banku.
 * Przechowuje i udostepnia informacje o koncie uzytkownika banku.
 */
public class Account {

    private final String accountId;
    private final String customerId;
    private final double balance;
    private final String currency;
    private final Double loan;
    private final Date loanDate;

    /**
     * Tworzy nowy obiekt, pobierajac dane z bazy.
     *
     * @param accountId numer konta
     * @throws AccountNotFoundException gdy konto nie zostanie znalezione
     */
    public Account(String accountId) throws AccountNotFoundException{
        List<Account> accounts = Database.fetchAccounts();
        for (Account account: accounts) {
            if (account.getAccountId().equals(accountId)) {
                this.accountId = accountId;
                this.customerId = account.getCustomerId();
                this.balance = account.getBalance();
                this.currency = account.getCurrency();
                this.loan = account.getLoan();
                this.loanDate = account.getLoanDate();
                return;
            }
        }
        throw new AccountNotFoundException();
    }

    public Account(String accountId, String customerId, double balance, String currency,
                   Double loan, Date loanDate) {
        this.accountId = accountId;
        this.customerId = customerId;
        this.balance = balance;
        this.currency = currency;
        this.loan = loan;
        this.loanDate = loanDate;
    }

    /**
     * @return numer konta
     */
    public String getAccountId() {
        return accountId;
    }

    /**
     * @return numer klienta
     */
    public String getCustomerId() {
        return customerId;
    }

    /**
     * @return stan konta
     */
    public double getBalance() {
        return balance;
    }

    /**
     * @return waluta konta
     */
    public String getCurrency() {
        return currency;
    }

    /**
     * @return wartosc pozyczki do splacenia
     */
    public Double getLoan() {
        return loan;
    }

    /**
     * @return ostateczna data splacenia pozyczki
     */
    public Date getLoanDate() {
        return loanDate;
    }
}
