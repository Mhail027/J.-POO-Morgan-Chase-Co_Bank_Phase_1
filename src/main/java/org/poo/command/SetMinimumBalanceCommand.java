package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.INVALID_ACCOUNT;

public class SetMinimumBalanceCommand implements Command{
    private final Bank bank;
    private final double amount;
    private final String iban;
    private final int timestamp;

    public SetMinimumBalanceCommand(@NonNull final Bank bank, final double amount,
                                    @NonNull final String iban, final int timestamp) {
        this.bank = bank;
        this.iban = iban;

        this.amount = PositiveOrZeroValidator.validate(
                amount
        );
        this.timestamp = (int) PositiveOrZeroValidator.validate(
                timestamp
        );
    }

    public void execute(ArrayNode output) {
        try {
            setMinimumBalance();
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    private void setMinimumBalance() {
        Account acct = bank.getDatabase().getAccount(iban);
        acct.setMinimumBalance(amount);
    }

    private void handleError(ArrayNode output, Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "setMinimumBalance",
                        e.getMessage(),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
