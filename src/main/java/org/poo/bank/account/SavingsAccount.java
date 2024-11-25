package org.poo.bank.account;

import lombok.Getter;
import org.poo.bank.client.User;

@Getter
public final class SavingsAccount extends Account{
    private double interestRate;

    public SavingsAccount(final User owner, final String IBAN, final String currency, final double interestRate) {
        super(owner, IBAN, currency);
        type = "SavingsAccount";
        this.interestRate = interestRate;
    }
}
