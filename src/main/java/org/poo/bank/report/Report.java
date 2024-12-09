package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;

import java.util.List;

@Getter
public final class Report {
    private static Report instance;

    @JsonProperty("IBAN") private String iban;
    private double balance;
    private String currency;
    private List<Transaction> transactions;

    private Report() {
    }

    /**
     * @return only instance of this class
     */
    private static Report getInstance() {
        if (instance == null) {
            instance = new Report();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param account details of account
     * @param startTimestamp initial time
     * @param finalTimestamp final time
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static Report init(final Account account, final int startTimestamp, final int finalTimestamp) {
        instance = getInstance();

        instance.iban = account.getIban();
        instance.balance = account.getBalance();
        instance.currency = account.getCurrency();
        instance.transactions = account.getTransactions().stream()
                                        .filter(transaction ->
                                                transaction.getTimestamp() >= startTimestamp &&
                                                transaction.getTimestamp() <= finalTimestamp
                                        ).toList();

        return instance;
    }

}
