package org.poo.bank.account;

import org.poo.bank.client.User;

import static org.poo.constants.Constants.INVALID_INTEREST_RATE;
import static org.poo.constants.Constants.INVALID_TYPE_OF_CARD;

public final class AccountFactory {
    private AccountFactory() {
    }

    /**
     * Create an account.
     * @param owner details of owner
     * @param iban  account number
     * @param currency currency of account
     * @param interestRate  positive number, if it's a savings account
     *                      0, if not
     * @param timestamp moment of time at which was given the command
     * @return created account
     */
    public static Account getAccount(final User owner, final String iban,
                                     final String currency, final double interestRate,
                                     final int timestamp) throws IllegalArgumentException {
        if (interestRate == 0) {
            return new ClassicAccount(owner,iban, currency, timestamp);
        } else if (interestRate > 0) {
            return new SavingsAccount(owner, iban, currency, interestRate, timestamp);
        }
        throw new  IllegalArgumentException(INVALID_INTEREST_RATE);
    }
}
