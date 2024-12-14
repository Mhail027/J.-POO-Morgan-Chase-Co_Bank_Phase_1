package org.poo.bank.card;

import org.poo.bank.account.Account;
import org.poo.bank.account.ClassicAccount;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.client.User;

import static org.poo.constants.Constants.*;

public final class CardFactory {
    private CardFactory() {
    }

    /**
     * Create a card.
     * @param owner owner of card
     * @param acct account which to will be assigned
     * @param cardNumber number of card
     * @param type CLASSIC_CARD or SAVINGS_CARD
     * @param timestamp moment of time at which was given the command
     * @return created card
     */
    public static Card getCard(final User owner, final Account acct,
                               final String cardNumber, final String type,
                               final int timestamp) throws IllegalArgumentException {
        if (type.equals(CLASSIC_CARD)) {
            return new ClassicCard(owner, acct, cardNumber, timestamp);
        } else if (type.equals(ONE_TIME_CARD)) {
            return new OneTimeCard(owner, acct, cardNumber, timestamp);
        }
        throw new  IllegalArgumentException(INVALID_TYPE_OF_CARD);
    }
}
