package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static org.poo.bank.Constants.*;

@Getter
public abstract class Account {
    protected User owner;
    @JsonProperty("IBAN") protected String iban;
    protected double balance = 0;
    protected String currency;
    protected String type;
    protected String status = "active";
    @Setter protected double minimumBalance = 0;
    protected final List<Card> cards = new LinkedList<Card>(); // key == card number

    public Account(final User owner, final String iban, final String currency) {
        this.owner = owner;
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
        if (!hasCard(card.getCardNumber())) {
            cards.add(card);
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
        for (Card card : cards) {
            if (Objects.equals(card.getCardNumber(), cardNumber)) {
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
        if (!Objects.equals(owner.getEmail(), email)) {
            return INVALID_USER;
        }
        if (balance == 0) {
            return BALANCE_MUST_BE_0;
        }

        deleteCards();
        status = "inactive";
        return null;
    }

    /**
     * Delete al the cards assigned with the account.
     */
    public void deleteCards() {
        for (Card card : cards) {
            card.setStatus("inactive");
        }
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

    @JsonIgnore
    private User getOwner() {
        return owner;
    }

    @JsonIgnore
    public final String getStatus() {
        return status;
    }

    @JsonIgnore
    public final double getMinimumBalance() {
        return minimumBalance;
    }
}


