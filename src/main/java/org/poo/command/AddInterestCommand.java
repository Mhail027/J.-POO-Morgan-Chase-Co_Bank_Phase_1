package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;

public class AddInterestCommand implements Command{
    private final Bank bank;
    private final String iban;
    private final int timestamp;

    public AddInterestCommand(final Bank bank, final String iban,
                              final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        }

        this.bank = bank;
        this.iban = iban;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.addInterest(iban);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "addInterest",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
