package org.poo.command;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import org.poo.bank.Bank;
import org.poo.output.OutputMessage;
import org.poo.output.SimpleOutput;

public class SendMoneyCommand implements Command{
    private final Bank bank;
    private final String senderIban;
    private final String senderEmail;
    private final String receiver;
    private final double amount;
    private final String description;
    private final int timestamp;

    public SendMoneyCommand(final Bank bank, final String senderIban,
                            final String senderEmail, final String receiver,
                            final double amount, final String description,
                            final int timestamp)
                            throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        } else if (senderIban == null) {
            throw new IllegalArgumentException("sender iban can't be null");
        } else if (senderEmail == null) {
            throw new IllegalArgumentException("sender email can't be null");
        } else if (receiver == null) {
            throw new IllegalArgumentException("receiver can't be null");
        } else if (amount < 0) {
        throw new IllegalArgumentException("amount can't be negative");
        }

        this.bank = bank;
        this.senderIban = senderIban;
        this.senderEmail = senderEmail;
        this.receiver = receiver;
        this.amount = amount;
        this.description = description;
        this.timestamp = timestamp;
    }

    public void execute(ArrayNode output) {
        ObjectMapper objectMapper = new ObjectMapper();

        try {
            bank.sendMoney(senderIban, senderEmail, receiver, amount, timestamp, description);
        } catch (Exception e) {
            JsonNode outputNode = objectMapper.valueToTree(
                    SimpleOutput.init("sendMoney",
                            OutputMessage.init(e.getMessage(), timestamp),
                            timestamp)
            );
            output.add(outputNode);
        }
    }
}
