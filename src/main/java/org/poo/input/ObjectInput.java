package org.poo.input;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.bank.Command;
import org.poo.bank.Exchange;
import org.poo.bank.client.Commerciant;
import org.poo.bank.client.User;

@Data
@NoArgsConstructor
public final class ObjectInput {
    private User[] users;
    private Exchange[] exchangeRates;
    private Command[] commands;
    private Commerciant[] commerciants;
}
