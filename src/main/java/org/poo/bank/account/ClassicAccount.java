package org.poo.bank.account;

import org.poo.bank.client.User;

public final class ClassicAccount extends Account{
    public ClassicAccount(final User owner, final String IBAN, final String currency) {
        super(owner, IBAN, currency);
        type = "classic";
    }
}
