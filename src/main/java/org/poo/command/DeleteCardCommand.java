package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

public class DeleteCardCommand implements Command{
    private final Bank bank;
    private final String cardNumber;
    private final int timestamp;

    public DeleteCardCommand(@NonNull final Bank bank, @NonNull final String cardNumber,
                             final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.cardNumber = cardNumber;
        this.timestamp = (int) PositiveOrZeroValidator.validate(
                timestamp
        );
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.deleteCard(cardNumber, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "deleteCard",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }

    public void deleteCard() {
        if (!bank.getDatabase().hasCard(cardNumber)) {
            return;
            // throw new IllegalArgumentException(INVALID_CARD);
        }

        bank.getDatabase().removeCard(cardNumber, timestamp);
    }
}
