package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputMessage;
import org.poo.output.OutputSuccess;
import org.poo.output.SimpleOutput;

import static org.poo.constants.Constants.ACCOUNT_DELETED;

public class PayOnlineCommand implements Command{
    private final Bank bank;
    private final String cardNumber;
    private final String email;
    private final double amount;
    private final String currency;
    private final String description;
    private final String commerciant;
    private final int timestamp;

    public PayOnlineCommand(final Bank bank, final String cardNumber,
                            final String email, final double amount,
                            final String currency, final String description,
                            final String commerciant, final int timestamp)
                     throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (cardNumber == null) {
            throw new IllegalArgumentException("cardNumber can't be null");
        } else if (email == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (commerciant == null) {
            throw new IllegalArgumentException("commerciant can't be null");
        }

        this.bank = bank;
        this.cardNumber = cardNumber;
        this.email = email;
        this.amount = amount;
        this.currency = currency;
        this.description = description;
        this.commerciant = commerciant;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.payOnline(cardNumber, email, amount, currency, commerciant, description, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init("payOnline",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp)
            );
            output.add(outputNode);
        }
    }
}
