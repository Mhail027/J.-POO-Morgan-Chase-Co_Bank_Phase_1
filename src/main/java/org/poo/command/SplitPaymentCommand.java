package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;

import java.util.List;

public class SplitPaymentCommand implements Command{
    private final Bank bank;
    private final List<String> ibans;
    private final double amount;
    private final String currency;
    private final int timestamp;

    public SplitPaymentCommand(final Bank bank, final List<String> ibans,
                               final double amount, final String currency,
                               final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (ibans == null) {
            throw new IllegalArgumentException("ibans can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }

        this.bank = bank;
        this.ibans = ibans;
        this.amount = amount;
        this.currency = currency;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.splitPayment(ibans, amount, currency, timestamp);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init("payOnline",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp)
            );
            output.add(outputNode);
        }
    }
}
