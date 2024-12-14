package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardFactory;
import org.poo.bank.client.User;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.*;

public class CreateCardCommand implements Command{
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

    public void execute(ArrayNode output) {
        try {
            bank.createCard(email, iban, type, timestamp);
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    private void createCard() {
        String cardNumber = bank.getCardNumberGenerator()
                                        .generateUniqueCardNumber(bank.getDatabase());
        User owner = bank.getDatabase().getUser(email);
        Account acct = bank.getDatabase().getAccount(iban);

        Card card = CardFactory.getCard(owner, acct, cardNumber, type, timestamp);
        bank.getDatabase().addCard(card);
    }

    private void handleError(ArrayNode output, Exception e) {
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
