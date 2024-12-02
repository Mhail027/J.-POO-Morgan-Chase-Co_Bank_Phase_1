package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public class Transaction {
    private final int timestamp;
    private final String description;

    public Transaction(final int timestamp, final String description) {
        this.description = description;
        this.timestamp = timestamp;
    }


}
