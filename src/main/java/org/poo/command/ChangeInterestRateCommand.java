package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.report.SpendingsReport;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;

public class ChangeInterestRateCommand implements Command{
    private final Bank bank;
    private final String iban;
    private double interestRate;
    private final int timestamp;

    public ChangeInterestRateCommand(final Bank bank, final String iban,
                                     final double interestRate, final int timestamp)
                                     throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        }

        this.bank = bank;
        this.iban = iban;
        this.interestRate = interestRate;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.changeInterestRate(iban, interestRate, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "changeInterestRate",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
