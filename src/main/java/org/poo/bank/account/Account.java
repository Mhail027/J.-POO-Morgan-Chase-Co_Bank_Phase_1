package org.poo.bank.account;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import java.util.List;
import java.util.LinkedList;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import static org.poo.constants.Constants.INVALID_CARD;
import static org.poo.constants.Constants.INVALID_USER;


@Getter
public abstract class Account {
    @JsonProperty("IBAN")
    protected String iban;
    protected String type;
    protected String currency;
    @Setter
    protected double minimumBalance;
    protected double balance;

    protected User owner;
    protected final List<Card> cards;
    protected final List<User> allowedUsers;
    protected final List<Transaction> transactions;

    public Account(@NonNull final User owner, @NonNull final String iban,
                   @NonNull final String currency) {
        this.iban = iban;
        this.currency = currency;
        minimumBalance = 0;
        balance = 0;

        this.owner = owner;
        cards = new LinkedList<>();
        allowedUsers = new LinkedList<>();
        transactions = new LinkedList<>();

        addUser(owner);
    }

    /**
     * @param amount received sum of money
     */
    public void addFunds(final double amount) {
        balance += amount;
    }

    /**
     * @param amount withdrawn sum of money
     */
    public void removeFunds(final double amount) {
        balance -= amount;
    }

    /**
     * A new card with its owner have access to account.
     *
     * @param card details of the card
     * @throws IllegalArgumentException if the card has access already to account
     */
    public void addCard(@NonNull final Card card) throws IllegalArgumentException {
        if (hasCard(card.getCardNumber())) {
            throw new IllegalArgumentException("Card has already access to account");
        }

        cards.add(card);
        if (!hasUser(card.getOwner().getEmail())) {
            allowedUsers.add(card.getOwner());
        }
    }

    /**
     * Verify if a card has access to account.
     *
     * @param cardNumber number of card
     * @return true, if card is associated with the account
     *         false, if not
     */
    public boolean hasCard(@NonNull final String cardNumber) {
        return cards.stream()
                       .anyMatch(card -> card.getCardNumber().equals(cardNumber));
    }

    /**
     * Revoke the access permission of a card to account.
     * Also,revoke the access permission of the owner of the card,
     * if the owner has just one card associated with the account.
     *
     * @param cardNumber number of card
     */
    public void removeCard(@NonNull final String cardNumber) {
        cards.removeIf(card -> card.getCardNumber().equals(cardNumber));

        removeUser(getOwner().getEmail());
        for (Card card : cards) {
            if (!hasUser(card.getOwner().getEmail())) {
                addUser(card.getOwner());
                break;
            }
        }
    }

    /**
     * Get the information of a card which is associated with the account
     *
     * @param cardNumber the number of card
     * @return card's details
     * @throws IllegalArgumentException if the given card is not associated with the account
     */
    private Card getCard(@NonNull final String cardNumber) throws IllegalArgumentException {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        throw new IllegalArgumentException(INVALID_CARD);
    }

    /**
     * A new user has access to account.
     *
     * @param user details of user
     * @throws IllegalArgumentException if the user has access already to account
     */
    public void addUser(@NonNull final User user) throws IllegalArgumentException {
        if (hasUser(user.getEmail())) {
            throw  new IllegalArgumentException("User has already access to account");
        }

        allowedUsers.add(user);
        user.addAccount(this);
    }

    /**
     * Verify if a user has access to account.
     *
     * @param email email of user
     * @return true, if user has access to account
     *         false, if not
     */
    private boolean hasUser(@NonNull final String email) {
        return allowedUsers.stream()
                       .anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * Revoke the access permission of a user to account.
     *
     * @param email email of user
     * @throws IllegalArgumentException if the given user does not have access to account
     */
    private void removeUser(@NonNull final String email) throws IllegalArgumentException {
        if (owner.getEmail().equals(email)) {
            return;
        }

        for (User user : allowedUsers) {
            if (user.getEmail().equals(email)) {
                allowedUsers.remove(user);
                return;
            }
        }
        throw new IllegalArgumentException(INVALID_USER);
    }

    /**
     * Save the info of a transaction.
     *
     * @param transaction details of transaction
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * No user will have access to this account anymore.
     */
    public void delete() {
        for (User user : allowedUsers) {
            user.removeAccount(iban);
        }
    }

    @JsonIgnore
    private List<User> getAllowedUsers() {
        return allowedUsers;
    }

    @JsonIgnore
    public final User getOwner() {
        return owner;
    }

    @JsonIgnore
    public final double getMinimumBalance() {
        return minimumBalance;
    }

    @JsonIgnore
    public final List<Transaction> getTransactions() {
        return transactions;
    }
}
