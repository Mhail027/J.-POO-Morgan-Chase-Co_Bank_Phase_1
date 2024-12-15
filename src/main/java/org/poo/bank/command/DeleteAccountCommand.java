package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import org.poo.output.OutputError;
import org.poo.output.OutputSuccess;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.CAN_NOT_DELETE_ACCOUNT;
import static org.poo.constants.Constants.HAVE_REMAINED_FUNDS;
import static org.poo.constants.Constants.ACCOUNT_DELETED;

public class DeleteAccountCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final String email;
    private final int timestamp;

    public DeleteAccountCommand(@NonNull final Bank bank, @NonNull final String iban,
                                @NonNull final String email, final int timestamp) {
        this.bank = bank;
        this.iban = iban;
        this.email = email;
        this.timestamp = (int) PositiveOrZeroValidator.validate(
                timestamp
        );
    }

    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the results or errors details will be recorded
     */
    public void execute(final ArrayNode output) {
        try {
            deleteAccount(output);
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Remove an account from database.
     * @param output an ArrayNode where the result will be recorded
     */
    private void deleteAccount(final ArrayNode output) {
        Account acct = bank.getDatabase().getAccount(iban);
        User owner = acct.getOwner();
        if (acct.getBalance() > 0) {
            haveRemainedFunds(acct, owner);
        }

        bank.getDatabase().removeAccount(iban);
        addSuccessfulOperation(output);
    }

    private void haveRemainedFunds(final Account acct, final User owner) {
        Transaction transaction  = new Transaction.Builder(timestamp)
                                           .description(HAVE_REMAINED_FUNDS)
                                           .build();
        acct.addTransaction(transaction);
        owner.addTransaction(transaction);

        throw new IllegalArgumentException(CAN_NOT_DELETE_ACCOUNT);
    }

    private void addSuccessfulOperation(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init("deleteAccount",
                        OutputSuccess.init(ACCOUNT_DELETED, timestamp),
                        timestamp)
        );
        output.add(outputNode);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "deleteAccount",
                        OutputError.init(e.getMessage(), timestamp),
                        timestamp
                )
        );
        output.add(outputNode);
    }



}
