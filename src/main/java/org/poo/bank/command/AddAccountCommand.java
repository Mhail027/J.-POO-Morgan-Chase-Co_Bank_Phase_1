package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.account.AccountFactory;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.NEW_ACCOUNT;

public final class AddAccountCommand implements Command {
    private final Bank bank;
    private final String email;
    private final String currency;
    private final double interestRate;
    private final int timestamp;

    public AddAccountCommand(@NonNull final Bank bank, @NonNull final String email,
                             @NonNull final String currency, final double interestRate,
                             final int timestamp) {
        this.bank = bank;
        this.email = email;
        this.currency = currency;
        this.interestRate = PositiveOrZeroValidator.validate(
                interestRate
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
            addAccount();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Create and add a new account in database of the bank.
     * This operation is marked through a transaction which is
     * saved by the account and the owner of the account.
     */
    private void addAccount() {
        User owner = bank.getDatabase().getUser(email);
        String iban = bank.getIbanGenerator().generateUniqueIBAN(bank.getDatabase());

        Account acct = AccountFactory.getAccount(owner, iban, currency, interestRate);
        bank.getDatabase().addAccount(acct);
        addSuccessfulTransaction(acct);
    }

    private void addSuccessfulTransaction(final Account acct) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .description(NEW_ACCOUNT)
                                          .build();
        acct.addTransaction(transaction);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "addAccount",
                        e.getMessage(),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
