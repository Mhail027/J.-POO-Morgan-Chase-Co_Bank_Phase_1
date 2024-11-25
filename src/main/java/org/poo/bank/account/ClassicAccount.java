package org.poo.bank.account;

import org.poo.bank.client.User;

public final class ClassicAccount extends Account {
    public ClassicAccount(final User owner, final String iban, final String currency) {
        super(owner, iban, currency);
        type = "classic";
    }
}
