package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;

@Data
public abstract class Card {
    protected User owner;
    protected Account account;
    protected String cardNumber;
    protected String status;

    public Card(@NonNull final User owner, @NonNull final Account account,
                @NonNull final String cardNumber)
            throws IllegalArgumentException {
        this.account = account;
        this.owner = owner;
        this.cardNumber = cardNumber;
        status = "active";

        if (!account.hasCard(cardNumber)) {
            account.addCard(this);
        }
    }

    /**
     * No account will have access to card anymore.
     */
    public void delete() {
        account.removeCard(cardNumber);
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
