package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.message.SimpleMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.INTEREST_RATE_CHANGED;
import static org.poo.constants.Constants.MUST_BE_SAVINGS_ACCOUNT;

public final class ChangeInterestRateCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final double interestRate;
    private final int timestamp;

    public ChangeInterestRateCommand(@NonNull final Bank bank, @NonNull final String iban,
                                     final double interestRate, final int timestamp) {
        this.bank = bank;
        this.iban = iban;
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
            changeInterestRate();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Changes the interestRate of a savings account.
     * This operation is marked through a transaction which is
     * saved by the account and the owner of the account.
     * @throws IllegalArgumentException if the account is not a savings account
     */
    private void changeInterestRate() throws IllegalArgumentException {
        Account acct = bank.getDatabase().getAccount(iban);
        try {
            ((SavingsAccount) acct).setInterestRate(interestRate);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(MUST_BE_SAVINGS_ACCOUNT);
        }
        addSuccessfulTransaction(acct);
    }

    private void addSuccessfulTransaction(final Account acct) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .description(
                                                  String.format(INTEREST_RATE_CHANGED,
                                                          Double.toString(interestRate)))
                                          .build();
        acct.addTransaction(transaction);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "changeInterestRate",
                        SimpleMessage.init(e.getMessage(), timestamp),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
