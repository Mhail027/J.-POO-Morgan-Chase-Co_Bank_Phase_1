package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.client.User;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.INVALID_ACCOUNT;

public final class SetAliasCommand implements Command {
    private final Bank bank;
    private final String email;
    private final String alias;
    private final String iban;
    private final int timestamp;

    public SetAliasCommand(@NonNull final Bank bank, @NonNull final String email,
                           @NonNull final String alias, @NonNull final String iban,
                           final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.email = email;
        this.alias = alias;
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
            setAlias();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * A user set an alias to an account.
     */
    private void setAlias() {
        User user = bank.getDatabase().getUser(email);
        if (!bank.getDatabase().hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        user.setAlias(alias, iban);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "setAlias",
                        e.getMessage(),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
