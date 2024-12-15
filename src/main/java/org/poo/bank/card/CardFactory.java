package org.poo.bank.card;

import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;

import static org.poo.constants.Constants.CLASSIC_CARD;
import static org.poo.constants.Constants.ONE_TIME_CARD;
import static org.poo.constants.Constants.INVALID_TYPE_OF_CARD;

public final class CardFactory {
    private CardFactory() {
    }

    /**
     * Create a card.
     *
     * @param owner owner of card
     * @param acct account which to will be assigned
     * @param cardNumber number of card
     * @param type CLASSIC_CARD or SAVINGS_CARD
     * @return created card
     * @throws IllegalArgumentException if given type of card does not exit
     */
    public static Card getCard(@NonNull final User owner, @NonNull final Account acct,
                               @NonNull final String cardNumber, final String type) {
        if (type.equals(CLASSIC_CARD)) {
            return new ClassicCard(owner, acct, cardNumber);
        } else if (type.equals(ONE_TIME_CARD)) {
            return new OneTimeCard(owner, acct, cardNumber);
        }
        throw new  IllegalArgumentException(INVALID_TYPE_OF_CARD);
    }
}
