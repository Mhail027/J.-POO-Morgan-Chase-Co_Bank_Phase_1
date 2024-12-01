package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public class Transaction {
    private String description;
    private int timestamp;

    public Transaction(final String description, final int timestamp) {
        this.description = description;
        this.timestamp = timestamp;
    }


}
