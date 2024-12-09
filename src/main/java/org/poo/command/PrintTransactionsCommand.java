package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.bank.transaction.Transaction;
import org.poo.output.SimpleOutput;

import java.util.List;

public class PrintTransactionsCommand implements Command {
    private Bank bank;
    private final String email;
    private final int timestamp;

    public PrintTransactionsCommand(final Bank bank, final String email,
                                    final int timestamp) throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        }
        else if (email == null) {
            throw new IllegalArgumentException("email can't be null");
        }

        this.bank = bank;
        this.email = email;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            List<Transaction> transactions = bank.getTransactions(email);

            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "printTransactions",
                            transactions,
                            timestamp
                    )
            );

            output.add(outputNode);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "printTransactions",
                            e.getMessage(),
                            timestamp
                    )
            );

            output.add(outputNode);
        }
    }
}
