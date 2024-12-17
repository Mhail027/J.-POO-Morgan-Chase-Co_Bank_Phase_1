package org.poo.bank.card;

import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;

public final class ClassicCard extends Card {
    public ClassicCard(@NonNull final User owner, @NonNull final Account account,
                       @NonNull final String cardNumber) {
        super(owner, account, cardNumber);
    }
}
