package org.poo.bank.account;

import lombok.NonNull;
import org.poo.bank.client.User;

public final class ClassicAccount extends Account {
    public ClassicAccount(@NonNull final User owner, @NonNull final String iban,
                          @NonNull final String currency) {
        super(owner, iban, currency);
        type = "classic";
    }
}
