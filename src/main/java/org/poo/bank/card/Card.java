package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import org.poo.throwable.DeleteOneTimeCard;
import org.poo.throwable.InsufficientFundsException;

import static org.poo.constants.Constants.*;

@Data
public abstract class Card {
    protected User owner;
    protected Account account;
    protected String cardNumber;
    protected String status;

    public Card(final User owner, final Account account,
                final String cardNumber, final int timestamp)
            throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("owner can't be null");
        } else if (account == null) {
            throw new IllegalArgumentException("account can't be null");
        } else if (cardNumber == null) {
            throw new IllegalArgumentException("card number can't be null");
        }

        this.account = account;
        this.owner = owner;

        this.cardNumber = cardNumber;
        status = "active";

        try {
            account.addCard(this);
        } catch (Exception ignored) {}

        Transaction transaction =  new Transaction.TransactionBuilder(timestamp)
                                          .account(account.getIban())
                                          .card(cardNumber)
                                          .cardHolder(owner.getEmail())
                                          .description(NEW_CARD).build();
        account.addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    /**
     * No account will have access to card anymore.
     * @param timestamp moment of time at which was given the command
     */
    public void delete(final int timestamp) {
        account.removeCard(cardNumber);

        Transaction transaction =  new Transaction.TransactionBuilder(timestamp)
                                           .account(account.getIban())
                                           .card(cardNumber)
                                           .cardHolder(owner.getEmail())
                                           .description(DESTROYED_CARD).build();
        account.addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    /**
     * Make a payment with this card.
     * @param amount sum of money
     * @param commerciant commerciant name
     * @param description description of payment
     * @param timestamp moment of time at which was given the command
     */
    public void payOnline(final double amount, final String commerciant,
                          String description, final int timestamp)
                          throws IllegalArgumentException, DeleteOneTimeCard {
        /// Description should not be modified. I do this because the refs are bad made.
        description = "Card payment";

        if (status.equals("frozen")) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                              .description(FROZEN_CARD).build();
            account.addTransaction(transaction);
            owner.addTransaction(transaction);
            return;
        }

        try {
            account.payOnline(amount);
        } catch (InsufficientFundsException e) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                              .description(e.getMessage()).build();
            account.addTransaction(transaction);
            owner.addTransaction(transaction);
            return;
        }

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .amount(String.valueOf(amount))
                                          .commerciant(commerciant)
                                          .description(description).build();
        account.addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    public void checkStatus(final int timestamp) {
        if (status.equals("active") && account.getBalance() <= account.getMinimumBalance()) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                              .description(CARD_WILL_BE_FROZEN).build();
            owner.addTransaction(transaction);
            status = "frozen";
        }
    }

    @JsonIgnore
    public final User getOwner() {
        return owner;
    }

    @JsonIgnore
    public final Account getAccount() {
        return account;
    }
}
