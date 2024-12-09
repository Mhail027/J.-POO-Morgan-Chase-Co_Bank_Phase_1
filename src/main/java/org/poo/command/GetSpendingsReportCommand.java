package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.report.Report;
import org.poo.bank.report.SpendingsReport;
import org.poo.output.Error;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;
import static org.poo.constants.Constants.NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT;

public class GetSpendingsReportCommand implements Command{
    private final Bank bank;
    private final String iban;
    private final int startTimestamp;
    private final int endTimestamp;
    private final int timestamp;

    public GetSpendingsReportCommand(final Bank bank, final String iban,
                                     final int startTimestamp, final int endTimestamp,
                                     final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        }

        this.bank = bank;
        this.iban = iban;
        this.startTimestamp = startTimestamp;
        this.endTimestamp = endTimestamp;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            SpendingsReport report = bank.getSpendingsReport(iban, startTimestamp, endTimestamp);
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "spendingsReport",
                            report,
                            timestamp
                    )
            );

            output.add(outputNode);
        } catch (Exception e) {
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
}
