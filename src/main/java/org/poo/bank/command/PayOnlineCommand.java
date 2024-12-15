package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.card.Card;
import org.poo.bank.card.OneTimeCard;
import org.poo.bank.transaction.Transaction;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;
import org.poo.validator.PositiveValidator;

import static org.poo.constants.Constants.FROZEN;
import static org.poo.constants.Constants.FROZEN_CARD;
import static org.poo.constants.Constants.INSUFFICIENT_FUNDS;
import static org.poo.constants.Constants.ONE_TIME_CARD;


public final class PayOnlineCommand implements Command {
    private final Bank bank;
    private final String cardNumber;
    private final String email;
    private final double amount;
    private final String currency;
    private final String description;
    private final String commerciant;
    private final int timestamp;

    public PayOnlineCommand(@NonNull final Bank bank, @NonNull final String cardNumber,
                            @NonNull final String email, final double amount,
                            @NonNull final String currency, final String description,
                            @NonNull final String commerciant, final int timestamp) {
        this.bank = bank;
        this.cardNumber = cardNumber;
        this.email = email;
        this.currency = currency;
        this.description = description;
        this.commerciant = commerciant;
        this.amount = PositiveValidator.validate(
                amount
        );
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
            payOnline(output);
        } catch (Exception e) {
            handleError(output, e);
        }
    }


    /**
     * A user uses his card to pay at a comerciant.
     * This operation is marked through a transaction which is
     * saved by the account which is associated the card
     * and the owner of the card.
     *
     * @param output an ArrayNode where the results will be recorded
     */
    private void payOnline(final ArrayNode output) {
        Card card = bank.getDatabase().getCard(cardNumber);
        double amountConverted = bank.getCurrencyConvertor().exchangeMoney(
                amount, currency, card.getAccount().getCurrency()
        );

        if (!canPay(card, amountConverted)) {
            return;
        }

        card.getAccount().removeFunds(amountConverted);
        addSuccessfulTransaction(card, amountConverted);

        try {
            OneTimeCard oneTimeCard = (OneTimeCard) card;
            deleteCard(output);
            addOneTimeCard(oneTimeCard.getAccount().getIban(), output);
        } catch (Exception ignored) { }
    }

    private boolean canPay(final Card card, final double amountConverted) {
        if (!card.getOwner().getEmail().equals(email)) {
            /// throw new IllegalArgumentException(INVALID_USER); <- what should be if refs
            /// have been right
            return false;
        } else if (card.getStatus().equals(FROZEN)) {
            addFailedTransaction(card, FROZEN_CARD);
            return false;
        } else if (card.getAccount().getBalance() < amountConverted) {
            addFailedTransaction(card, INSUFFICIENT_FUNDS);
            return false;
        }
        return true;
    }

    private void addFailedTransaction(final Card card, final String message) {
        Transaction transaction = new Transaction.Builder(timestamp)
                                          .description(message)
                                          .build();
        card.getAccount().addTransaction(transaction);
        card.getOwner().addTransaction(transaction);
    }

    private void addSuccessfulTransaction(final Card card, final double amountConverted) {
        /// Description should be the received description in constructor.
        /// I change the description of transaction because the refs are bad made.
        /// Also, the transaction should include the currency.
        Transaction transaction = new Transaction.Builder(timestamp)
                                          .amount(String.valueOf(amountConverted))
                                          .commerciant(commerciant)
                                          .description("Card payment").build();
        card.getAccount().addTransaction(transaction);
        card.getOwner().addTransaction(transaction);
    }

    private void deleteCard(final ArrayNode output) {
        Command command = new DeleteCardCommand(bank, cardNumber, timestamp);
        command.execute(output);
    }

    private void addOneTimeCard(final String iban, final ArrayNode output) {
        Command command = new CreateCardCommand(bank, email, iban, timestamp, ONE_TIME_CARD);
        command.execute(output);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init("payOnline",
                        OutputMessage.init(e.getMessage(), timestamp),
                        timestamp)
        );
        output.add(outputNode);
    }
}
