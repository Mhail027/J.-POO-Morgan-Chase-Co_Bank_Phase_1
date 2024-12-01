package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;
import java.util.Objects;
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
        return null;
    }

    /**
     *  A user tries to pay using this card.
     *
     * @param amount sum of money which must be paid
     * @param email euser's email
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String pay(final double amount, final String email) {
        if (email == null || !email.equals(owner.getEmail())) {
            return INVALID_USER;
        }

        return account.pay(amount);
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
