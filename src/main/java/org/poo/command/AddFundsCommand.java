package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class AddFundsCommand implements Command{
    private final Bank bank;
    private final String iban;
    private final double amount;
    private final int timestamp;

    public AddFundsCommand(final Bank bank, final String iban,
                           final double amount, final int timestamp)
                           throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        }

        this.bank = bank;
        this.iban = iban;
        this.amount = amount;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.addFunds(iban, amount);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "addFunds",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
