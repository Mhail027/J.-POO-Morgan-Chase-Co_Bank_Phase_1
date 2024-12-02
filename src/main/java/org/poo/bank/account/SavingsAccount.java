package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.Setter;
import org.poo.bank.client.User;

@Setter
public final class SavingsAccount extends Account {
    private double interestRate;

    public SavingsAccount(final User owner, final String iban,
                          final String currency, final double interestRate) {
        super(owner, iban, currency);
        type = "savings";
        this.interestRate = interestRate;
    }

    public void addInterestRate() {
        balance = balance + balance * interestRate;
    }

    @JsonIgnore
    public double getInterestRate() {
        return interestRate;
    }
}
