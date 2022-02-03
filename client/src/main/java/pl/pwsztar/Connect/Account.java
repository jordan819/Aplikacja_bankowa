package pl.pwsztar.Connect;

import java.sql.Date;

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

    protected Account(String accountId, String customerId, double balance, String currency,
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
