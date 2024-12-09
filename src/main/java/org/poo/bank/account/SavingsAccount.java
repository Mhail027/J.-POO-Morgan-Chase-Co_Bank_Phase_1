package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import org.poo.bank.client.User;

@Setter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final User owner, final String iban,
                          final String currency, final double interestRate,
                          final int timestamp)
                          throws IllegalArgumentException {
        super(owner, iban, currency, timestamp);
        type = "savings";
        this.interestRate = interestRate;
    }

    @JsonIgnore
    public double getInterestRate() {
        return interestRate;
    }
}
