package org.poo.bank.account;

import lombok.Getter;
import org.poo.bank.client.User;

@Getter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final User owner, final String iban,
                          final String currency, final double interestRate) {
        super(owner, iban, currency);
        type = "SavingsAccount";
        this.interestRate = interestRate;
    }
}
