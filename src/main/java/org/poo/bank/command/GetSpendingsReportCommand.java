package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.report.SpendingsReport;
import org.poo.output.Error;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

import static org.poo.constants.Constants.NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT;

public final class GetSpendingsReportCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final int startTimestamp;
    private final int endTimestamp;
    private final int timestamp;

    public GetSpendingsReportCommand(@NonNull final Bank bank, @NonNull final String iban,
                                     final int startTimestamp, final int endTimestamp,
                                     final int timestamp) throws IllegalArgumentException {
        this.bank = bank;
        this.iban = iban;
        this.startTimestamp = (int) PositiveOrZeroValidator.validate(
                startTimestamp
        );
        this.endTimestamp = (int) PositiveOrZeroValidator.validate(
                endTimestamp
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
            getSpendingsReport(output);
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Get the list of spendings transaction of an account during a period.
     * @param output an Array Node where the report will be recorded
     */
    private void getSpendingsReport(final ArrayNode output) {
        Account acct = bank.getDatabase().getAccount(iban);
        if (acct.getType().equals("savings")) {
            throw new IllegalArgumentException(NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT);
        }

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "spendingsReport",
                        new SpendingsReport(acct, startTimestamp, endTimestamp),
                        timestamp
                )
        );

        output.add(outputNode);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode;

        if (e.getMessage().equals(NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT)) {
            outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "spendingsReport",
                            Error.init(e.getMessage()),
                            timestamp
                    )
            );
        } else {
            outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "spendingsReport",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp
                    )
            );
        }

        output.add(outputNode);
    }
}
