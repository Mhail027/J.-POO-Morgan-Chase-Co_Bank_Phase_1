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
    protected final List<Transaction> transactions;

    public Account(@NonNull final User owner, @NonNull final String iban,
                   @NonNull final String currency) {
        this.iban = iban;
        this.currency = currency;
        minimumBalance = 0;
        balance = 0;

        this.owner = owner;
        cards = new LinkedList<>();
        transactions = new LinkedList<>();

        owner.addAccount(this);
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
     *
     * @param cardNumber number of card
     */
    public void removeCard(@NonNull final String cardNumber) {
        cards.removeIf(card -> card.getCardNumber().equals(cardNumber));
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
     * Save the info of a transaction.
     *
     * @param transaction details of transaction
     */
    public void addTransaction(final Transaction transaction) {
        transactions.add(transaction);
    }

    /**
     * The owner will not have access to this account anymore.
     */
    public void delete() {
        owner.removeAccount(iban);
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
