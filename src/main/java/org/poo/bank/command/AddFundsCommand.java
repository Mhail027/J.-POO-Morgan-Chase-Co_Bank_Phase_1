package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

public final class AddFundsCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final double amount;
    private final int timestamp;

    /// Amount should be verified using PositiveValidator. Does not make sense to be 0.
    public AddFundsCommand(@NonNull final Bank bank, @NonNull final String iban,
                           final double amount, final int timestamp) {
        this.bank = bank;
        this.iban = iban;
        this.amount = PositiveOrZeroValidator.validate(
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
            addFunds();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Add money in to an account.
     */
    private void addFunds() {
        Account acct = bank.getDatabase().getAccount(iban);
        acct.addFunds(amount);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "addFunds",
                        e.getMessage(),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
