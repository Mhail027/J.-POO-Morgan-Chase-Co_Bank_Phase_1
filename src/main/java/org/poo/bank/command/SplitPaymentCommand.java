package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.transaction.Transaction;
import org.poo.bank.transaction.TransactionBuilder;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;
import static org.poo.constants.Constants.INSUFFICIENT_FUNDS_FOR_SPLIT;
import static org.poo.constants.Constants.SPLIT_PAYMENT;

import java.util.List;


public final class SplitPaymentCommand implements Command {
    private final Bank bank;
    private final List<String> ibans;
    private final double amount;
    private final String currency;
    private final int timestamp;

    public SplitPaymentCommand(@NonNull final Bank bank, @NonNull final List<String> ibans,
                               final double amount, @NonNull final String currency,
                               final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.ibans = ibans;
        this.currency = currency;
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
            splitPayment();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * More accounts split a bill.
     * This operation is marked through a transaction which is
     * saved by the accounts and the owner of the accounts.
     */
    private void splitPayment() {
        /// Added .reverses() because the refs are bad made
        List<Account> accounts = bank.getDatabase().getAccounts(ibans).reversed();
        double amountPerAccount = amount / accounts.size();

        if (!everyoneHasEnoughFunds(accounts, amountPerAccount)) {
            return;
        }

        Transaction transaction = createSuccessfulTransaction(amountPerAccount);
        for (Account acct : accounts) {
            double convertedAmount = bank.getCurrencyConvertor().exchangeMoney(
                    amountPerAccount, currency, acct.getCurrency());
            acct.removeFunds(convertedAmount);
            acct.addTransaction(transaction);
        }
    }

    private boolean everyoneHasEnoughFunds(final List<Account> accounts,
                                          final double amountPerAccount) {
        Account problemAcct = null;
        for (Account acct : accounts) {
            double convertedAmount = bank.getCurrencyConvertor().exchangeMoney(
                    amountPerAccount, currency, acct.getCurrency()
            );
            if (acct.getBalance() < convertedAmount) {
                problemAcct = acct;
                break;
            }
        }

        if (problemAcct == null) {
            return true;
        }

        addFailedTransaction(accounts, problemAcct, amountPerAccount);
        return false;
    }

    private void addFailedTransaction(final List<Account> accounts, final Account problemAcct,
                                      final double amountPerAccount) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .amount(String.valueOf(amountPerAccount))
                                          .currency(currency)
                                          .involvedAccounts(ibans)
                                          .error(
                                                  String.format(INSUFFICIENT_FUNDS_FOR_SPLIT,
                                                          problemAcct.getIban())
                                          )
                                          .description(
                                                  String.format(SPLIT_PAYMENT,
                                                          amount, currency)
                                          )
                                          .build();

        for (Account account : accounts) {
            account.addTransaction(transaction);
        }
    }

    private Transaction createSuccessfulTransaction(final double amountPerAccount) {
        return new TransactionBuilder()
                       .timestamp(timestamp)
                       .amount(String.valueOf(amountPerAccount))
                       .currency(currency)
                       .involvedAccounts(ibans)
                       .description(String.format(SPLIT_PAYMENT, amount, currency))
                       .build();
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init("splitPayment",
                        OutputMessage.init(e.getMessage(), timestamp),
                        timestamp)
        );
        output.add(outputNode);
    }
}
