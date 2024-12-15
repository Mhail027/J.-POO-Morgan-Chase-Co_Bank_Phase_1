package org.poo.bank.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import lombok.NonNull;
import org.poo.bank.Bank;
import org.poo.output.SimpleOutput;

public final class PrintUsersCommand implements Command {
    private final Bank bank;
    private final int timestamp;

    public PrintUsersCommand(@NonNull final Bank bank, @NonNull final int timestamp) {
        this.bank = bank;
        this.timestamp = timestamp;
    }

    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the results will be recorded
     */
    public void execute(final ArrayNode output) {
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
