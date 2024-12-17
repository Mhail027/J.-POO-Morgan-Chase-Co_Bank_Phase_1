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

import static org.poo.constants.Constants.INSUFFICIENT_FUNDS;
import static org.poo.constants.Constants.INVALID_ACCOUNT;
import static org.poo.constants.Constants.INVALID_USER;


public final class SendMoneyCommand implements Command {
    private final Bank bank;
    private final String senderIban;
    private final String senderEmail;
    private final String receiver;
    private  String receiverIban;
    private final double amount;
    private final String description;
    private final int timestamp;

    public SendMoneyCommand(@NonNull final Bank bank, @NonNull final String senderIban,
                            @NonNull final String senderEmail, @NonNull final String receiver,
                            final double amount, @NonNull final String description,
                            final int timestamp) {
        this.bank = bank;
        this.senderIban = senderIban;
        this.senderEmail = senderEmail;
        this.receiver = receiver;
        this.description = description;
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
            sendMoney();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Someone send money from one of his account to another account.
     * This operation is marked through a transaction which is
     * saved by the account and the owner of the account.
     */
    private void sendMoney() {
        Account senderAccount = bank.getDatabase().getAccount(senderIban);
        Account receiverAccount = bank.getDatabase().getAccount(receiver, senderEmail);
        receiverIban = receiverAccount.getIban();

        if (!canSendMoney(senderAccount)) {
            return;
        }

        senderAccount.removeFunds(amount);
        addSuccessfulTransaction(senderAccount, "sent", amount);

        double amountConverted = bank.getCurrencyConvertor().exchangeMoney(
                amount, senderAccount.getCurrency(), receiverAccount.getCurrency()
        );
        receiverAccount.addFunds(amountConverted);
        addSuccessfulTransaction(receiverAccount, "received", amountConverted);
    }

    private boolean canSendMoney(final Account sender) {
        if (!sender.getOwner().getEmail().equals(senderEmail)) {
            throw new IllegalArgumentException(INVALID_USER);
        } else if (sender.getBalance() < amount) {
            hasInsufficientFunds(sender);
            return false;
        }
        return true;
    }

    private void hasInsufficientFunds(final Account sender) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .description(INSUFFICIENT_FUNDS)
                                          .build();
        sender.addTransaction(transaction);
    }

    private void addSuccessfulTransaction(final Account account, final String type,
                                          final double amountPerAccount) {
        Transaction transaction = new TransactionBuilder()
                                          .timestamp(timestamp)
                                          .description(description)
                                          .amount(amountPerAccount + " " + account.getCurrency())
                                          .senderIBAN(senderIban)
                                          .receiverIBAN(receiverIban)
                                          .transferType(type)
                                          .build();
        account.addTransaction(transaction);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ///  Should not exist this if. I put it because the refs.
        if (e.getMessage().equals(INVALID_ACCOUNT)) {
            return;
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init("sendMoney",
                        OutputMessage.init(e.getMessage(), timestamp),
                        timestamp)
        );
        output.add(outputNode);
    }
}
