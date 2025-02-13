package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.message.ErrorMessage;
import org.poo.output.message.SuccessMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.HAVE_REMAINED_FUNDS;
import static org.poo.constants.Constants.INVALID_USER;
import static org.poo.constants.Constants.CAN_NOT_DELETE_ACCOUNT;
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
        if (!canDeleteAccount(acct)) {
            return;
        }

        bank.getDatabase().removeAccount(iban);
        addSuccessfulOperation(output);
    }

    private boolean canDeleteAccount(final Account acct) throws IllegalArgumentException {
        if (acct.getBalance() > 0) {
            haveRemainedFunds(acct);
            return false;
        } else if (!acct.getOwner().getEmail().equals(email)) {
            throw new IllegalArgumentException(INVALID_USER);
        }
        return true;
    }
    private void haveRemainedFunds(final Account acct) {
        Transaction transaction  = new TransactionBuilder()
                                           .timestamp(timestamp)
                                           .description(HAVE_REMAINED_FUNDS)
                                           .build();
        acct.addTransaction(transaction);
        throw new IllegalArgumentException(CAN_NOT_DELETE_ACCOUNT);
    }

    private void addSuccessfulOperation(final ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init("deleteAccount",
                        SuccessMessage.init(ACCOUNT_DELETED, timestamp),
                        timestamp)
        );
        output.add(outputNode);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "deleteAccount",
                        ErrorMessage.init(e.getMessage(), timestamp),
                        timestamp
                )
        );
        output.add(outputNode);
    }



}
