package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class SetAliasCommand implements Command{
    private final Bank bank;
    private final String email;
    private final String alias;
    private final String iban;
    private final int timestamp;

    public SetAliasCommand(final Bank bank, final String email,
                           final String alias, final String iban,
                           final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (email == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (alias == null) {
            throw new IllegalArgumentException("currency can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("iban can't be null");
        }

        this.bank = bank;
        this.email = email;
        this.alias = alias;
        this.iban = iban;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.setAlias(email, alias, iban);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "addAccount",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
