package org.poo.bank.card;

import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;

public final class OneTimeCard extends Card {
    public OneTimeCard(@NonNull final User owner, @NonNull final Account account,
                       @NonNull final String cardNumber) {
        super(owner, account, cardNumber);
    }
}
