package org.poo.bank.account;

import lombok.NonNull;
import lombok.Setter;

import org.poo.bank.client.User;
import org.poo.validator.PositiveValidator;

@Setter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(@NonNull final User owner, @NonNull final String iban,
                          @NonNull final String currency, final double interestRate) {
        super(owner, iban, currency);
        type = "savings";
        this.interestRate = PositiveValidator.validate(
                interestRate
        );
    }

    /**
     * Add the interest of the account.
     */
    public void addInterest() {
        balance = balance + balance * interestRate;
    }
}
