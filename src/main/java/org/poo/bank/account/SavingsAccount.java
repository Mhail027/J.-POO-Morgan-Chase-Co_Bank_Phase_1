package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Getter;
import org.poo.bank.client.User;

public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final User owner, final String iban,
                          final String currency, final double interestRate) {
        super(owner, iban, currency);
        type = "savings";
        this.interestRate = interestRate;
    }

    @JsonIgnore
    public double getInterestRate() {
        return interestRate;
    }
}
