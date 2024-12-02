package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public final class PayTransaction extends Transaction{
    private final double amount;
    private final String commerciant;

    public PayTransaction(final int timestamp, String description,
                          final double amount, final String commerciant) {
        super(timestamp, description);
        this.amount = amount;
        this.commerciant = commerciant;
    }
}
