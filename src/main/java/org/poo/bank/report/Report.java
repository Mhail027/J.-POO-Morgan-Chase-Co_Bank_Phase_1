package org.poo.bank.report;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;

import java.util.List;

@Getter
public class Report {
    @JsonProperty("IBAN") protected final String iban;
    protected final double balance;
    protected final String currency;
    protected List<Transaction> transactions;

    public Report (final Account account, final int startTimestamp, final int endTimestamp) {
        iban = account.getIban();
        balance = account.getBalance();
        currency = account.getCurrency();
        transactions = account.getTransactions().stream()
                                        .filter(transaction ->
                                                transaction.getTimestamp() >= startTimestamp &&
                                                transaction.getTimestamp() <= endTimestamp
                                        ).toList();
    }

}
