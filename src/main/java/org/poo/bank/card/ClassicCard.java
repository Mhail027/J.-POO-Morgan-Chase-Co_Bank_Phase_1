package org.poo.bank.card;

import org.poo.bank.account.Account;
import org.poo.bank.client.User;

public final class ClassicCard extends Card {
    public ClassicCard(final User owner, final Account account,
                       final String cardNumber, final int timestamp)
                       throws IllegalArgumentException {
        super(owner, account, cardNumber, timestamp);
    }
}
