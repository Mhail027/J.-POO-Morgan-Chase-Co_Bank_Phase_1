package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

import static org.poo.constants.Constants.*;

public class CreateCardCommand implements Command{
    private final Bank bank;
    private final String email;
    private final String iban;
    private final String type;
    private final int timestamp;

    public CreateCardCommand(final Bank bank, final String email,
                             final String iban, final int timestamp,
                             final String type) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (email == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("currency can't be null");
        } else if (!type.equals(CLASSIC_CARD) && !type.equals(ONE_TIME_CARD)) {
            throw new IllegalArgumentException(INVALID_TYPE_OF_CARD);
        }

        this.bank = bank;
        this.email = email;
        this.iban = iban;
        this.type = type;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.createCard(email, iban, type, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "createCard",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
