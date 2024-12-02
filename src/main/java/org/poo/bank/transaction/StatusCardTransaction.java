package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public final class StatusCardTransaction extends Transaction{
    private final String account;
    private final String card;
    private final String cardHolder;

    public StatusCardTransaction(final int timestamp, final String description,
                                 final String account, final String card,
                                 final String cardHolder) {
        super(timestamp, description);
        this.account = account;
        this.card = card;
        this.cardHolder = cardHolder;
    }
}
