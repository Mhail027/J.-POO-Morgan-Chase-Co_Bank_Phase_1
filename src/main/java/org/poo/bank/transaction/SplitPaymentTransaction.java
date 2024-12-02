package org.poo.bank.transaction;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class SplitPaymentTransaction extends Transaction{
    private final double amount;
    private final String currency;
    private final List<String> involvedAccounts;
    private final String error;

    public SplitPaymentTransaction(final int timestamp, String description,
                                   final double amount, final String currency,
                                   final List<String> involvedAccounts, final String error) {
        super(timestamp, description);
        this.amount = amount;
        this.currency = currency;
        this.involvedAccounts = involvedAccounts;
        this.error = error;
    }
}
