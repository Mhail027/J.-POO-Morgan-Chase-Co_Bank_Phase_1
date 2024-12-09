package org.poo.bank;

import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.account.AccountFactory;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardFactory;
import org.poo.bank.client.User;
import org.poo.bank.currency.CurrencyConvertor;
import org.poo.bank.currency.Exchange;
import org.poo.bank.database.DataBase;
import org.poo.bank.report.Report;
import org.poo.bank.report.SpendingsReport;
import org.poo.bank.transaction.Transaction;
import org.poo.throwable.DeleteOneTimeCard;
import org.poo.utils.Utils;

import java.util.List;

import static org.poo.constants.Constants.*;


@Getter
public class Bank {
    private static Bank instance;

    private DataBase dataBase;
    private CurrencyConvertor currencyConvertor;

    /**
     * @return only instance of this class
     */
    private static Bank getInstance() {
        if (instance == null) {
            instance = new Bank();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param users array of users
     * @param exchanges arrays of exchanges between currencies
     * @return created bank
     */
    public static Bank init(final User[] users, final Exchange[] exchanges)
                                throws IllegalArgumentException {
        if (users == null) {
            throw new IllegalArgumentException("users can't be null");
        } else if (exchanges == null) {
            throw new IllegalArgumentException("exchanges can't be null");
        }

        instance = getInstance();

        instance.dataBase = DataBase.init(users);
        instance.currencyConvertor = CurrencyConvertor.init(exchanges);

        return instance;
    }

    /**
     * A user create a new account at this bank.
     *
     * @param email email of user
     * @param currency currency of account
     * @param interestRate positive number if it's a savings account
     *                     0, if not
     * @param timestamp moment of time at which the command was given
     */
    public void createAccount(final String email, final String currency,
                              final double interestRate, final int timestamp)
                              throws IllegalArgumentException {
        if (!dataBase.hasUser(email)) {
            throw new IllegalArgumentException(INVALID_USER);
        } else if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        } else if (interestRate < 0) {
            throw  new IllegalArgumentException(INVALID_INTEREST_RATE);
        }

        String iban = Utils.generateIBAN();
        while (dataBase.hasAccount(iban)) {
            iban = Utils.generateIBAN();
        }

        User owner = dataBase.getUser(email);
        Account acct = AccountFactory.getAccount(owner, iban, currency,
                                                 interestRate, timestamp);

        dataBase.addAccount(acct);
    }

    /**
     * A user create a new card at this bank. The card is associate it with an account.
     * If the user does  not have access to account, will receive it.
     *
     * @param email email of user
     * @param iban number of account which will be associated with the card
     * @param type CLASSIC_CARD or ONE_TIME_CARD
     * @param timestamp moment of time at which the command was given
     */
    public void createCard(final String email, final String iban,
                           final String type, final int timestamp)
            throws IllegalArgumentException {
        if (!dataBase.hasUser(email)) {
            return;
            //throw new IllegalArgumentException(INVALID_USER);
        } else if (!dataBase.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        } else if (!type.equals(CLASSIC_CARD) && !type.equals(ONE_TIME_CARD)) {
            throw new IllegalArgumentException(INVALID_TYPE_OF_CARD);
        }

        String cardNumber = Utils.generateCardNumber();
        while (dataBase.hasCard(cardNumber)) {
            cardNumber = Utils.generateCardNumber();
        }

        User owner = dataBase.getUser(email);
        Account acct = dataBase.getAccount(iban);
        Card card = CardFactory.getCard(owner, acct, cardNumber, type, timestamp);

        dataBase.addCard(card);
    }

    /**
     * Add funds in an account.
     *
     * @param iban account number
     * @param amount sum of money
     */
    public void addFunds(final String iban, final double amount)
                         throws IllegalArgumentException {
        if (!dataBase.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be a negative number");
        }

        Account acct = dataBase.getAccount(iban);
        acct.addMoney(amount);
    }

    public void setMinimumBalance(final String iban, final double amount)
                                  throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (iban == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        acct.setMinimumBalance(amount);
    }

    public void deleteAccount(final String iban, final String email,
                              final int timestamp) throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (!dataBase.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        User owner = acct.getOwner();
        if (!owner.getEmail().equals(email)) {
            throw new IllegalArgumentException(INVALID_USER);
        }

        if (acct.getBalance() != 0) {
            Transaction transaction  = new Transaction.TransactionBuilder(timestamp)
                                               .description("Account couldn't be deleted - there are funds remaining")
                                               .build();
            acct.addTransaction(transaction);
            owner.addTransaction(transaction);
            throw new IllegalArgumentException(CAN_NOT_DELETE_ACCOUNT);
        }

        dataBase.removeAccount(iban);
    }

    public void deleteCard(final String cardNumber, final int timestamp)
            throws IllegalArgumentException{
        if (!dataBase.hasCard(cardNumber)) {
            return;
           // throw new IllegalArgumentException(INVALID_CARD);
        }

        dataBase.removeCard(cardNumber, timestamp);
    }

    public void payOnline(final String cardNumber, final String email,
                          final Double amount, final String currency,
                          final String commerciant, final String description,
                          final int timestamp) throws IllegalArgumentException {
        Card card = dataBase.getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        } else if (!card.getOwner().getEmail().equals(email)) {
            return;
           // throw new IllegalArgumentException(INVALID_USER);
        } else if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }

        String cardCurrency = card.getAccount().getCurrency();
        double amountConverted = currencyConvertor.exchangeMoney(amount, currency, cardCurrency);

        try {
            card.payOnline(amountConverted, commerciant, description, timestamp);
        } catch (DeleteOneTimeCard ignored) {
            deleteCard(cardNumber, timestamp);
            createCard(card.getOwner().getEmail(), card.getAccount().getIban(),
                    ONE_TIME_CARD, timestamp);
        }
    }

    public void sendMoney(final String senderIban, final String senderEmail,
                          final String receiver, final double amount,
                          final int timestamp, final String description)
                          throws IllegalArgumentException {
        Account senderAccount = dataBase.getAccount(senderIban);
        if (senderAccount == null) {
            return;
        } else if (!senderAccount.getOwner().getEmail().equals(senderEmail)) {
            throw new IllegalArgumentException(INVALID_USER);
        }

        Account receiverAccount = dataBase.getAccount(receiver, senderEmail);
        if (receiverAccount == null) {
            return;
        }

        senderAccount.sendMoney(receiverAccount, amount, currencyConvertor,
                                timestamp, description);
    }

    /**
     * A user set an alias to an account.
     *
     * @param email email of user
     * @param alias "nickname" of account
     * @param iban account number
     */
    public void setAlias(final String email, final String alias, final String iban)
                         throws IllegalArgumentException {
        if (!dataBase.hasUser(email)) {
            throw new IllegalArgumentException(INVALID_USER);
        } else if (!dataBase.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        User user = dataBase.getUser(email);
        user.setAlias(alias, iban);
    }

    public List<Transaction> getTransactions(final String email)
                                             throws IllegalArgumentException {
        User user = dataBase.getUser(email);
        if (user == null) {
            throw new IllegalArgumentException(INVALID_USER);
        }

        return user.getTransactions();
    }

    public Report getReport(final String iban, final int startTimestamp,
                            final int endTimestamp) throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        return new Report(acct, startTimestamp, endTimestamp);
    }

    public SpendingsReport getSpendingsReport(final String iban, final int startTimestamp,
                                             final int endTimestamp)
                                             throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        } else if (acct.getType().equals("savings")) {
            throw new IllegalArgumentException(NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT);
        }

        return new SpendingsReport(acct, startTimestamp, endTimestamp);
    }

    public void checkCardStatus(final String cardNumber, final int timestamp)
                                throws IllegalArgumentException{
        Card card = dataBase.getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        }

        card.checkStatus(timestamp);
    }

    public void splitPayment(final List<String> ibans, final double amount,
                             final String currency, final int timestamp)
                             throws IllegalArgumentException {
        /// Add .reverses() because the refs are bad made
        List<Account> accounts = dataBase.getAccounts(ibans).reversed();

        double amountPerAccount = amount / accounts.size();
        for (Account acct : accounts) {
            if (!acct.haveEnoughMoney(amountPerAccount, currency, currencyConvertor)) {
                Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                                  .amount(String.valueOf(amountPerAccount))
                                                  .currency(currency)
                                                  .involvedAccounts(ibans)
                                                  .error(
                                                   String.format("Account %s has insufficient " +
                                                   "funds for a split payment.", acct.getIban())
                                                   )
                                                  .description(
                                                   String.format("Split payment of %.2f %s",
                                                   amount, currency)
                                                  )
                                                  .build();
                for (Account account : accounts) {
                    account.addTransaction(transaction);
                    account.getOwner().addTransaction(transaction);
                }
                return;
            };
        }

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .amount(String.valueOf(amountPerAccount))
                                          .currency(currency)
                                          .involvedAccounts(ibans)
                                          .description(
                                           String.format("Split payment of %.2f %s",
                                           amount, currency)
                                           )
                                          .build();
        for (Account acct : accounts) {
            acct.splitPayment(amountPerAccount, currency, currencyConvertor, transaction);
        }
    }

    public void changeInterestRate(final String iban, final double interestRate,
                                   final int timestamp)
                                   throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        try {
            ((SavingsAccount) acct).changeInterestRate(interestRate, timestamp);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(MUST_BE_SAVINGS_ACCOUNT);
        }
    }

    public void addInterest(final String iban) throws IllegalArgumentException {
        Account acct = dataBase.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        try {
            ((SavingsAccount) acct).addInterest();
        } catch (ClassCastException e) {
            throw new IllegalArgumentException(MUST_BE_SAVINGS_ACCOUNT);
        }
    }
}
