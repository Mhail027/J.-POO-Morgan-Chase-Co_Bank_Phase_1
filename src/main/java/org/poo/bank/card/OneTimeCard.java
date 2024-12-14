package org.poo.bank.card;

import org.poo.bank.account.Account;
import org.poo.bank.client.User;
import org.poo.throwable.DeleteOneTimeCard;

public final class OneTimeCard extends Card {
    public OneTimeCard(final User owner, final Account account,
                       final String cardNumber, final int timestamp)
                       throws IllegalArgumentException {
        super(owner, account, cardNumber, timestamp);
    }

    @Override
    public void payOnline(final double amount, final String commerciant,
                          String description, final int timestamp)
                          throws IllegalArgumentException, DeleteOneTimeCard{
        double balanceBefore = account.getBalance();
        super.payOnline(amount, commerciant, description, timestamp);

        if (balanceBefore != account.getBalance()) {
            throw new DeleteOneTimeCard();
        }
    }
}
