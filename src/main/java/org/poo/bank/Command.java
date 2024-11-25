package org.poo.bank;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.poo.output.ErrorOutput;
import org.poo.output.SimpleOutput;

import java.util.List;

import static org.poo.bank.Constants.*;

@Data
@NoArgsConstructor
public final class Command {
    private String command;
    private String email;
    private String account;
    private String currency;
    private double amount;
    private double minBalance;
    private String target;
    private String description;
    private String cardNumber;
    private String commerciant;
    private int timestamp;
    private int startTimestamp;
    private int endTimestamp;
    private String receiver;
    private String alias;
    private String accountType;
    private double interestRate;
    private List<String> accounts;

    /**
     * Execute the command.
     *
     * @param dataBase database of bank
     * @return a JsonNode, if we need to print something
     *         null, if not
     */
    public JsonNode execute(final DataBase dataBase) {
        dataBase.incrementTimestamp();

        ObjectMapper objectMapper = new ObjectMapper();
        return switch (command) {
            case "printUsers" -> objectMapper.valueToTree(
                        SimpleOutput.init(command, dataBase.getUsers(), dataBase.getTimestamp()));

            case "addAccount" -> {
                String err = dataBase.createAccount(email, currency, interestRate);
                yield  objectMapper.valueToTree(
                        SimpleOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "createCard" -> {
                String err = dataBase.createCard(account, email, false);
                yield  objectMapper.valueToTree(
                        SimpleOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "createOneTimeCard" -> {
                String err = dataBase.createCard(account, email, true);
                yield  objectMapper.valueToTree(
                        SimpleOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "addFunds" -> {
                String err = dataBase.addFunds(account, amount);
                yield  objectMapper.valueToTree(
                        SimpleOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "deleteAccount" -> {
                String err = dataBase.deleteAccount(account, email);
                yield  objectMapper.valueToTree(
                        ErrorOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "deleteCard" -> {
                String err = dataBase.deleteCard(cardNumber, email);
                yield  objectMapper.valueToTree(
                        ErrorOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "setMinimumBalance" -> {
                String err = dataBase.setMinimumBalance(account, amount);
                yield  objectMapper.valueToTree(
                        ErrorOutput.init(command, err, dataBase.getTimestamp()));
            }

            case "payOnline" -> {
                String err = dataBase.payOnline(cardNumber, amount, currency, email);
                yield  objectMapper.valueToTree(
                        ErrorOutput.init(command, err, dataBase.getTimestamp()));
            }

            default -> objectMapper.valueToTree(
                       ErrorOutput.init(command, INVALID_COMMAND, dataBase.getTimestamp()));
        };
    }
}
