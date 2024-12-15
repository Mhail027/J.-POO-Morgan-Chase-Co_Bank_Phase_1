package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.client.User;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

public final class PrintTransactionsCommand implements Command {
    private Bank bank;
    private final String email;
    private final int timestamp;

    public PrintTransactionsCommand(@NonNull final Bank bank, @NonNull final String email,
                                    final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.email = email;
        this.timestamp = (int) PositiveOrZeroValidator.validate(
                timestamp
        );
    }

    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the result or errors details will be recorded
     */
    public void execute(final ArrayNode output) {
        try {
            printTransactions(output);
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Print the transactions of a user in output.
     *
     * @param output an ArrayNode where the result will be recorde
     */
    private void printTransactions(final ArrayNode output) {
        User user = bank.getDatabase().getUser(email);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "printTransactions",
                        user.getTransactions(),
                        timestamp
                )
        );
        output.add(outputNode);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "printTransactions",
                        e.getMessage(),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
