package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.report.Report;
import org.poo.output.SimpleOutput;

public class GetReportCommand implements Command{
    private final Bank bank;
    private final String iban;
    private final int startTimestamp;
    private final int finalTimestamp;
    private final int timestamp;

    public GetReportCommand(final Bank bank, final String iban,
                            final int startTimestamp, final int finalTimestamp,
                            final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        }

        this.bank = bank;
        this.iban = iban;
        this.startTimestamp = startTimestamp;
        this.finalTimestamp = finalTimestamp;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            Report report = bank.getReport(iban, startTimestamp, finalTimestamp);

            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "report",
                            report,
                            timestamp
                    )
            );

            output.add(outputNode);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "report",
                            e.getMessage(),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
