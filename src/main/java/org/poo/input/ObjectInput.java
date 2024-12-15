package org.poo.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.bank.currency.Exchange;
import org.poo.bank.client.User;

@Data
@NoArgsConstructor
public final class ObjectInput {
    private User[] users;
    private Exchange[] exchangeRates;
    private CommandInput[] commands;
}
