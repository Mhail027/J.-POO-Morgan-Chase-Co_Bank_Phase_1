package org.poo.bank.command;

import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.card.Card;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;

import static org.poo.constants.Constants.DESTROYED_CARD;
import static org.poo.constants.Constants.INVALID_CARD;


public final class DeleteCardCommand implements Command {
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

    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the errors details will be recorded
     */
    public void execute(final ArrayNode output) {
        try {
            deleteCard();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Remove a card from database.
     * This operation is marked through a transaction which is
     * saved by the account which is associated the card
     * and the owner of the card.
     */
    private void deleteCard() {
        Card card = bank.getDatabase().getCard(cardNumber);
        bank.getDatabase().removeCard(cardNumber);
        addSuccessfulTransaction(card);
    }

    private void addSuccessfulTransaction(final Card card) {
        Transaction transaction =  new TransactionBuilder()
                                           .timestamp(timestamp)
                                           .account(card.getAccount().getIban())
                                           .card(cardNumber)
                                           .cardHolder(card.getOwner().getEmail())
                                           .description(DESTROYED_CARD)
                                           .build();
        card.getAccount().addTransaction(transaction);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ///  Should not exist this if. I put it because the refs.
        if (e.getMessage().equals(INVALID_CARD)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
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
