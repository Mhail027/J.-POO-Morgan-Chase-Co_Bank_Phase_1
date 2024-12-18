package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.card.Card;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.message.SimpleMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.CARD_WILL_BE_FROZEN;
import static org.poo.constants.Constants.FROZEN;

public final class CheckCardStatusCommand implements Command {
    private final Bank bank;
    private final String cardNumber;
    private final int timestamp;

    public CheckCardStatusCommand(@NonNull final Bank bank, @NonNull final String cardNumber,
                                  final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.cardNumber = cardNumber;
        this.timestamp = (int) PositiveOrZeroValidator.validate(
                timestamp
        );
    }

    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the errors details will be recorded
     */
    public void execute(final ArrayNode output) {
        try {
            checkCardStatus();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Verify if we need to change the status of the card.
     */
    private void checkCardStatus() {
        Card card = bank.getDatabase().getCard(cardNumber);

        if (card.getStatus().equals("active")
                    && card.getAccount().getBalance() <= card.getAccount().getMinimumBalance()) {
            cardBecomesFrozen(card);
        }
    }

    private void cardBecomesFrozen(final Card card) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .description(CARD_WILL_BE_FROZEN)
                                          .build();
        card.getOwner().addTransaction(transaction);
        card.setStatus(FROZEN);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "checkCardStatus",
                        SimpleMessage.init(e.getMessage(), timestamp),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
