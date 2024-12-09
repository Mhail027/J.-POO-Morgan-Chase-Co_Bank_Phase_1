package org.poo.command;

import org.poo.bank.Bank;
import org.poo.input.CommandInput;

import static org.poo.constants.Constants.CLASSIC_CARD;
import static org.poo.constants.Constants.ONE_TIME_CARD;

public class CommandFactory {
    private CommandFactory(){
    }

    public static Command getCommand(final CommandInput commandInput, final Bank bank)
                              throws IllegalArgumentException {
        if (bank == null) {
            throw new IllegalArgumentException("bank can't be null");
        }

        return switch (commandInput.getCommand()) {
            case "printUsers"
                    -> new PrintUserCommand(bank, commandInput.getTimestamp());
            case "printTransactions"
                    -> new PrintTransactionsCommand(bank, commandInput.getEmail(),
                    commandInput.getTimestamp());

            case "addAccount"
                    -> new CreateAccountCommand(bank, commandInput.getEmail(),
                    commandInput.getCurrency(), commandInput.getInterestRate(),
                    commandInput.getTimestamp());
            case "createCard"
                    -> new CreateCardCommand(bank, commandInput.getEmail(),
                    commandInput.getAccount(), commandInput.getTimestamp(), CLASSIC_CARD);
            case "createOneTimeCard"
                    -> new CreateCardCommand(bank, commandInput.getEmail(),
                    commandInput.getAccount(), commandInput.getTimestamp(), ONE_TIME_CARD);

            case "deleteAccount"
                    -> new DeleteAccountCommand(bank, commandInput.getAccount(),
                    commandInput.getEmail(), commandInput.getTimestamp());
            case "deleteCard"
                    -> new DeleteCardCommand(bank, commandInput.getCardNumber(),
                    commandInput.getTimestamp());

            case "addFunds"
                    -> new AddFundsCommand(bank, commandInput.getAccount(),
                    commandInput.getAmount(), commandInput.getTimestamp());
            case "setMinimumBalance"
                -> new SetMinimumBalanceCommand(bank, commandInput.getAmount(),
                    commandInput.getAccount(), commandInput.getTimestamp());
            case "payOnline"
                    -> new PayOnlineCommand(bank, commandInput.getCardNumber(),
                    commandInput.getEmail(), commandInput.getAmount(),
                    commandInput.getCurrency(), commandInput.getDescription(),
                    commandInput.getCommerciant(), commandInput.getTimestamp());
            case "sendMoney"
                    -> new SendMoneyCommand(bank, commandInput.getAccount(),
                    commandInput.getEmail(), commandInput.getReceiver(),
                    commandInput.getAmount(), commandInput.getDescription(),
                    commandInput.getTimestamp());
            case "splitPayment"
                    -> new SplitPaymentCommand(bank, commandInput.getAccounts(),
                    commandInput.getAmount(), commandInput.getCurrency(),
                    commandInput.getTimestamp());

            case "setAlias"
                    ->new SetAliasCommand(bank, commandInput.getEmail(),
                    commandInput.getAlias(), commandInput.getAccount(),
                    commandInput.getTimestamp());
            case "checkCardStatus"
                    -> new CheckCardStatusCommand(bank, commandInput.getCardNumber(),
                    commandInput.getTimestamp());
            case "report"
                        -> new GetReportCommand(bank, commandInput.getAccount(),
                    commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                    commandInput.getTimestamp());
            case "spendingsReport"
                        -> new GetSpendingsReportCommand(bank, commandInput.getAccount(),
                        commandInput.getStartTimestamp(), commandInput.getEndTimestamp(),
                        commandInput.getTimestamp());

            case "changeInterestRate"
                        -> new ChangeInterestRateCommand(bank, commandInput.getAccount(),
                        commandInput.getInterestRate(), commandInput.getTimestamp());
            case "addInterest"
                    -> new AddInterestCommand(bank, commandInput.getAccount(),
                    commandInput.getTimestamp());
            default
                    -> throw new IllegalArgumentException("Command type is not valid");
        };
    }
}
