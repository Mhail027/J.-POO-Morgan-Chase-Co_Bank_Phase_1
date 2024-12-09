package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class CreateAccountCommand implements Command{
    private final Bank bank;
    private final String email;
    private final String currency;
    private final double interestRate;
    private final int timestamp;

    public CreateAccountCommand(final Bank bank, final String email,
                                final String currency, final double interestRate,
                                final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (email == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }

        this.bank = bank;
        this.email = email;
        this.currency = currency;
        this.interestRate = interestRate;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.createAccount(email, currency, interestRate, timestamp);
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
