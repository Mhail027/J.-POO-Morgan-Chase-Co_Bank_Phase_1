package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.account.AccountFactory;
import org.poo.bank.client.User;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

public class AddAccountCommand implements Command {
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

    public void execute(ArrayNode output) {
        try {
            addAccount();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    private void addAccount() {
        String iban = bank.getIbanGenerator().generateUniqueIBAN(bank.getDatabase());
        User owner = bank.getDatabase().getUser(email);

        Account acct = AccountFactory.getAccount(owner, iban, currency, interestRate,
                timestamp);
        bank.getDatabase().addAccount(acct);
    }

    private void handleError(ArrayNode output, Exception e) {
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
