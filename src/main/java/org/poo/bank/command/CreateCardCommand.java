package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardFactory;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.INVALID_USER;
import static org.poo.constants.Constants.NEW_CARD;


public final class CreateCardCommand implements Command {
    private final Bank bank;
    private final String email;
    private final String iban;
    private final String type;
    private final int timestamp;

    public CreateCardCommand(@NonNull final Bank bank, @NonNull final String email,
                             @NonNull final String iban, final int timestamp,
                             @NonNull final String type) throws IllegalArgumentException {
        this.bank = bank;
        this.email = email;
        this.iban = iban;
        this.type = type;
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
            createCard();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Create a card and add it in database. The card receives access to account.
     * The owner of the card also receives access to account if he does not have.
     * This operation is marked through a transaction which is
     * saved by the account and the owner of the account.
     */
    private void createCard() {
        User owner = bank.getDatabase().getUser(email);
        Account acct = bank.getDatabase().getAccount(iban);
        String cardNumber = bank.getCardNumberGenerator()
                                    .generateUniqueCardNumber(bank.getDatabase());

        Card card = CardFactory.getCard(owner, acct, cardNumber, type);
        bank.getDatabase().addCard(card);
        addSuccessfulTransaction(acct, owner, cardNumber);
    }

    private void addSuccessfulTransaction(final Account acct, final User owner,
                                          final String cardNumber) {
        Transaction transaction =  new TransactionBuilder()
                                           .timestamp(timestamp)
                                           .account(acct.getIban())
                                           .card(cardNumber)
                                           .cardHolder(owner.getEmail())
                                           .description(NEW_CARD)
                                           .build();
        acct.addTransaction(transaction);
        owner.addTransaction(transaction);
    }
    private void handleError(final ArrayNode output, final Exception e) {
        ///  Should not exist this if. I put it because the refs.
        if (e.getMessage().equals(INVALID_USER)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
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
