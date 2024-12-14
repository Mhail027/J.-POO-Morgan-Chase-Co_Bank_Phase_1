package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public class PrintUserCommand implements Command{
    private final Bank bank;
    private final int timestamp;

    public PrintUserCommand(@NonNull final Bank bank, @NonNull final int timestamp) {
        this.bank = bank;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode outputNode = objectMapper.valueToTree(
                SimpleOutput.init(
                        "printUsers",
                        bank.getDatabase().getUsers(),
                        timestamp
                )
        );
        output.add(outputNode);
    }


}
