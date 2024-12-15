package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.bank.account.Account;
import org.poo.bank.report.Report;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import org.poo.validator.PositiveOrZeroValidator;

public final class GetReportCommand implements Command {
    private final Bank bank;
    private final String iban;
    private final int startTimestamp;
    private final int endTimestamp;
    private final int timestamp;

    public GetReportCommand(@NonNull final Bank bank, @NonNull final String iban,
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
     * @param output an ArrayNode where the results or errors details will be recorded
     */
    public void execute(final ArrayNode output) {
        try {
            getReport(output);
        } catch (Exception e) {
            handleError(output, e);
        }
    }

    /**
     * Get the list of transaction of an account.
     * @param output an Array Node where the report will be recorded
     */
    private void getReport(final ArrayNode output) {
        Account acct = bank.getDatabase().getAccount(iban);

        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "report",
                        new Report(acct, startTimestamp, endTimestamp),
                        timestamp
                )
        );

        output.add(outputNode);
    }

    private void handleError(final ArrayNode output, final Exception e) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "report",
                        OutputMessage.init(e.getMessage(), timestamp),
                        timestamp
                )
        );
        output.add(outputNode);
    }
}
