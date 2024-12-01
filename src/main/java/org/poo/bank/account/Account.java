package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.bank.DataBase;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;
import org.poo.bank.transaction.SendMoneyTransaction;

import java.util.LinkedList;
import java.util.List;

import static org.poo.bank.Constants.*;

@Getter
public abstract class Account {
    protected User owner;
    protected final List<User> allowedUsers = new LinkedList<User>();
    @JsonProperty("IBAN") protected String iban;
    protected double balance = 0;
    protected String currency;
    protected String type;
    @Setter protected double minimumBalance = 0;
    protected final List<Card> cards = new LinkedList<Card>(); // key == card number

    public Account(final User owner, final String iban, final String currency) {
        this.owner = owner;
        allowedUsers.add(owner);
        this.iban = iban;
        this.currency = currency;
    }

    /**
     * Add money in account.
     *
     * @param amount received sum
     */
    public void addMoney(final double amount) {
        balance += amount;
    }

    /**
     * Associate the account with a new card.
     *
     * @param card details of the card
     */
    public void addCard(final Card card) {
        if (card != null && !hasCard(card.getCardNumber())) {
            cards.add(card);
            addUser(card.getOwner());
        }
    }

    /**
     * Verify if a card is associate with the account.
     *
     * @param cardNumber unique number identifier of card
     * @return true, if card is associated with the account
     *         false, in contrary case
     */
    public boolean hasCard(final String cardNumber) {
        if (cardNumber == null) {
            return false;
        }

        for (Card card : cards) {
            if (cardNumber.equals(card.getCardNumber())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Dissociate a card of this account.
     *
     * @param cardNumber number of card
     */
    public void removeCard(final String cardNumber) {
        for (Card card : cards) {
            if (cardNumber.equals(card.getCardNumber())) {
                cards.remove(card);
                return;
            }
        }
    }

    /**
     * A user receives permission to access the account.
     *
     * @param user details of user
     */
    private void addUser(final User user) {
        if (user != null && !hasUser(user.getEmail())) {
            allowedUsers.add(user);
        }
    }

    /**
     * Verify is a user can access the account.
     *
     * @param email email of user
     * @return true, if it has access to account
     *         false, if not
     */
    private boolean hasUser(final String email) {
        if (email == null) {
            return false;
        }

        for (User allowedUser : allowedUsers) {
            if (allowedUser.getEmail().equals(email)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete the account and all the cards which are assigned to it.
     *
     * @param email email of the user which want to delete the account
     * @return String with an error, if something bad happens
     *          null, if not
     */
    public String delete(final String email) {
        if (email == null || !email.equals(owner.getEmail())) {
            return INVALID_USER;
        }
        if (balance != 0) {
            return CAN_NOT_DELETE_ACCOUNT;
        }

        for (User user : allowedUsers) {
            user.removeAccount(iban);
        }
        return null;
    }

    /**
     * Someone take money from the account to pay something.
     *
     * @param amount sum of money
     * @return null
     */
    public String pay(final double amount) {
        if (balance < amount) {
            return null;
        }

        balance -= amount;
        return null;
    }

    public String sendMoney(final double amount, final String description,
                            final String senderEmail, final Account receiver) {
        if (senderEmail == null || !senderEmail.equals(owner.getEmail())) {
            return INVALID_USER;
        }
        if (balance < amount) {
            return null; // should be TOO_LITTLE_MONEY
        }
      //  if (receiver.getType().equals("savings")) {
         //   return null; // should be NO_TO_SAVING_ACCOUNT
        //}

        owner.addTransaction(new SendMoneyTransaction(amount + " " + currency,
                description, receiver.getIban(), iban, DataBase.getTimestamp(), "sent"));
        receiver.getOwner().addTransaction(new SendMoneyTransaction(amount + " " + currency,
                description, receiver.getIban(), iban, DataBase.getTimestamp(), "received"));


        balance -= amount;
        double receivedAmount = DataBase.exchangeMoney(amount, currency, receiver.currency);
        receiver.addMoney(receivedAmount);
        return null;
    }

    @JsonIgnore
    public List<User> getAllowedUsers() {
        return allowedUsers;
    }

    @JsonIgnore
    private User getOwner() {
        return owner;
    }

    @JsonIgnore
    public final double getMinimumBalance() {
        return minimumBalance;
    }
}


