package org.poo.bank.card;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.poo.bank.client.User;
import java.util.Objects;
import static org.poo.bank.Constants.*;

@Data
public final class Card {
    private User owner;
    private final String cardNumber;
    private String status;
    private final boolean OneTimeCard;

    public Card (final User owner, final String cardNumber, boolean OneTimeCard) {
        this.owner = owner;
        this.cardNumber = cardNumber;
        status = "active";
        this.OneTimeCard = OneTimeCard;
    }

    /**
     * Deactivate the card.
     *
     * @param email email of user which started the process
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String delete(final String email) {
        if (Objects.equals(owner.getEmail(), email)) {
            return INVALID_USER;
        }

        status = "inactive";
        return null;
    }

    @JsonIgnore
    public boolean isOneTimeCard() {
        return OneTimeCard;
    }

    @JsonIgnore
    public User getOwner() {
        return owner;
    }
}
