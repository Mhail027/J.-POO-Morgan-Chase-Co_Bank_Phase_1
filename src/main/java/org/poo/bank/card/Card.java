package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.poo.bank.DataBase;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;
import org.poo.bank.transaction.PayTransaction;
import org.poo.bank.transaction.StatusCardTransaction;
import org.poo.bank.transaction.Transaction;

import static org.poo.bank.Constants.*;

@Data
public final class Card {
    private User owner;
    private Account account;
    private final String cardNumber;
    private String status;
    private final boolean oneTimeCard;

    public Card(final User owner, final Account account,
                 final String cardNumber, final boolean oneTimeCard) {
        this.account = account;
        this.owner = owner;
        this.cardNumber = cardNumber;
        status = "active";
        this.oneTimeCard = oneTimeCard;
    }

    /**
     * Deactivate the card.
     *
     * @param email email of user which started the process
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String delete(final String email) {
        if (email == null  || !email.equals(owner.getEmail())) {
            return INVALID_USER;
        }

        account.removeCard(cardNumber);
        owner.addTransaction(
                new StatusCardTransaction(DataBase.getTimestamp(),
                DESTROYED_CARD, account.getIban(), cardNumber, owner.getEmail())
        );
        return null;
    }

    /**
     *  A user tries to pay using this card.
     *
     * @param amount sum of money which must be paid
     * @param email user's email
     * @param description string with supplementary details about payment
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String pay(final double amount, final String email,
                      final String description, final String commerciant) {
        if (email == null || !email.equals(owner.getEmail())) {
            return INVALID_USER;
        }

        if (status.equals("frozen")) {
            owner.addTransaction(
                    new Transaction(DataBase.getTimestamp(), FROZEN_CARD)
            );
            return null;
        }
        if (status.equals("blocked")) {
            owner.addTransaction(
                    new Transaction(DataBase.getTimestamp(), BLOCKED_CARD)
            );
            return null;
        }

        String err = account.pay(amount);

        Transaction transaction;
        if (err == null) {
            // Third field should be the received description
            transaction = new PayTransaction(DataBase.getTimestamp(), "Card payment",
                    amount, commerciant);

            if (oneTimeCard) {
                status = "blocked";
            }
        } else  {
            transaction = new Transaction(DataBase.getTimestamp(), err);
        }
        account.addTransaction(transaction);
        owner.addTransaction(transaction);

        return null;
    }


    public void checkStatus() {
        String newStatus;
        if (account.getBalance() <= account.getMinimumBalance()) {
            newStatus = "frozen";
        } else {
            newStatus = "active";
        }

        String message = null;
        if (status.equals("active") && newStatus.equals("frozen"))
            message  = CARD_WILL_BE_FROZEN;

        if (message != null) {
            owner.addTransaction(
                    new Transaction(DataBase.getTimestamp(), message)
            );
            status = newStatus;
        }
    }

    @JsonIgnore
    public boolean isOneTimeCard() {
        return oneTimeCard;
    }

    @JsonIgnore
    public User getOwner() {
        return owner;
    }

    @JsonIgnore
    public Account getAccount() {
        return account;
    }
}
