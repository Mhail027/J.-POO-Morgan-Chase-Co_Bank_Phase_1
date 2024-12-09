package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class SetMinimumBalanceCommand implements Command{
    private final Bank bank;
    private final double amount;
    private final String iban;
    private final int timestamp;

    public SetMinimumBalanceCommand(final Bank bank, final double amount,
                                    final String iban, final int timestamp)
                             throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be a negative number");
        } else if (iban == null) {
            throw new IllegalArgumentException("iban can't be null");
        }

        this.bank = bank;
        this.amount = amount;
        this.iban = iban;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.setMinimumBalance(iban, amount);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "setMinimumBalance",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
