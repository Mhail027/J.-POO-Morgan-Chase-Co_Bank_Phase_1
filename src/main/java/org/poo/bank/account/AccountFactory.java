package org.poo.bank.account;

import lombok.NonNull;
import org.poo.bank.client.User;

import static org.poo.constants.Constants.INVALID_INTEREST_RATE;

public final class AccountFactory {
    private AccountFactory() {
    }

    /**
     * Create an account.
     *
     * @param owner details of owner
     * @param iban  account number
     * @param currency currency of account
     * @param interestRate  positive number, if it's a savings account
     *                      0, if not
     * @return created account
     * @throws IllegalArgumentException if interest rate is negative
     */
    public static Account getAccount(@NonNull final User owner, @NonNull final String iban,
                                     @NonNull final String currency, final double interestRate)
                                     throws IllegalArgumentException {
        if (interestRate == 0) {
            return new ClassicAccount(owner, iban, currency);
        } else if (interestRate > 0) {
            return new SavingsAccount(owner, iban, currency, interestRate);
        }
        throw new  IllegalArgumentException(INVALID_INTEREST_RATE);
    }
}
