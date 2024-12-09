package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputError;
import org.poo.output.OutputMessage;
import org.poo.output.OutputSuccess;
import org.poo.output.SimpleOutput;
import static org.poo.constants.Constants.ACCOUNT_DELETED;

public class DeleteAccountCommand implements Command{
    private final Bank bank;
    private final String iban;
    private final String email;
    private final int timestamp;

    public DeleteAccountCommand(final Bank bank, final String iban,
                                final String email, final int timestamp)
                                throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("email can't be null");
        } else if (email == null) {
            throw new IllegalArgumentException("currency can't be null");
        }

        this.bank = bank;
        this.iban = iban;
        this.email = email;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.deleteAccount(iban, email, timestamp);
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init("deleteAccount",
                            OutputSuccess.init(ACCOUNT_DELETED, timestamp),
                            timestamp)
            );
            output.add(outputNode);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init(
                            "deleteAccount",
                            OutputError.init(e.getMessage(), timestamp),
                            timestamp
                    )
            );
            output.add(outputNode);
        }
    }
}
