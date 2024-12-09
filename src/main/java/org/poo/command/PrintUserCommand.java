package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class PrintUserCommand implements Command{
    private final Bank bank;
    private final int timestamp;

    public PrintUserCommand(final Bank bank, final int timestamp)
                            throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        }

        this.bank = bank;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "printUsers",
                        bank.getDataBase().getUsers(),
                        timestamp
                )
        );

        output.add(outputNode);
    }
}
