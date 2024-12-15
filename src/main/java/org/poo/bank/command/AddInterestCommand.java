package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.account.SavingsAccount;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.MUST_BE_SAVINGS_ACCOUNT;

public final class AddInterestCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final int timestamp;

    public AddInterestCommand(@NonNull final Bank bank, @NonNull final String iban,
                              final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.iban = iban;
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
            addInterest();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * A savings account receives his interest.
     * @throws IllegalAccessError if the account is not a savings account
     */
    private void addInterest() throws IllegalArgumentException {
        Account acct = bank.getDatabase().getAccount(iban);

        try {
            ((SavingsAccount) acct).addInterest();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(MUST_BE_SAVINGS_ACCOUNT);
        }
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
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
