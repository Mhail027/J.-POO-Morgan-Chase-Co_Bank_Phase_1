package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Setter;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;

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

    public void addInterest() {
        balance = balance + balance * interestRate;
    }

    public void changeInterestRate(final double newInterestRate, final int timestamp) {
        interestRate = newInterestRate;

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .description("Interest rate of the account changed to " + interestRate)
                                          .build();
        addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    @JsonIgnore
    public double getInterestRate() {
        return interestRate;
    }
}
