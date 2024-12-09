package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;

public class CheckCardStatusCommand implements Command{
    private final Bank bank;
    private final String cardNumber;
    private final int timestamp;

    public CheckCardStatusCommand(final Bank bank, final String cardNumber,
                                  final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (cardNumber == null) {
            throw new IllegalArgumentException("card number can't be null");
        }

        this.bank = bank;
        this.cardNumber = cardNumber;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.checkCardStatus(cardNumber, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "checkCardStatus",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
