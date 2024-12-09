package org.poo.bank.account;

import org.poo.bank.client.User;

public final class ClassicAccount extends Account {
    public ClassicAccount(final User owner, final String iban,
                          final String currency, int timestamp) throws IllegalArgumentException {
        super(owner, iban, currency, timestamp);
        type = "classic";
    }
}
