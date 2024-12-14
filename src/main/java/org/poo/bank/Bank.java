package org.poo.bank;

import lombok.Getter;
import lombok.NonNull;
import org.poo.bank.account.Account;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.card.Card;
import org.poo.bank.card.CardFactory;
import org.poo.bank.client.User;
import org.poo.bank.currency.CurrencyConvertor;
import org.poo.bank.currency.Exchange;
import org.poo.bank.database.Database;
import org.poo.bank.geneartor.CardNumberGenerator;
import org.poo.bank.geneartor.IbanGenerator;
import org.poo.bank.report.Report;
import org.poo.bank.report.SpendingsReport;
import org.poo.bank.transaction.Transaction;
import org.poo.throwable.DeleteOneTimeCard;
import org.poo.validator.NotNullValidator;

import java.security.spec.ECField;
import java.util.List;
import static org.poo.constants.Constants.*;

@Getter
public class Bank {
    private static Bank instance;

    private Database database;
    private CurrencyConvertor currencyConvertor;
    private IbanGenerator ibanGenerator;
    private CardNumberGenerator cardNumberGenerator;

    private Bank() {
    }

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
     * @param exchanges array of exchanges rates between currencies
     * @return created bank
     */
    public static Bank init(@NonNull final User[] users, @NonNull final Exchange[] exchanges) {
        instance = getInstance();

        instance.database = (Database) NotNullValidator.validate(
                Database.init(users)
        );
        instance.currencyConvertor = (CurrencyConvertor) NotNullValidator.validate(
                CurrencyConvertor.init(exchanges)
        );
        instance.ibanGenerator = (IbanGenerator) NotNullValidator.validate(
                IbanGenerator.init()
        );
        instance.cardNumberGenerator = (CardNumberGenerator) NotNullValidator.validate(
                CardNumberGenerator.init()
        );

        return instance;
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
        if (!database.hasUser(email)) {
            return;
            //throw new IllegalArgumentException(INVALID_USER);
        } else if (!database.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        } else if (!type.equals(CLASSIC_CARD) && !type.equals(ONE_TIME_CARD)) {
            throw new IllegalArgumentException(INVALID_TYPE_OF_CARD);
        }

        String cardNumber = cardNumberGenerator.generateUniqueCardNumber(database);

        User owner = database.getUser(email);
        Account acct = database.getAccount(iban);
        Card card = CardFactory.getCard(owner, acct, cardNumber, type, timestamp);

        database.addCard(card);
    }

    public void deleteCard(final String cardNumber, final int timestamp)
            throws IllegalArgumentException{
        if (!database.hasCard(cardNumber)) {
            return;
           // throw new IllegalArgumentException(INVALID_CARD);
        }

        database.removeCard(cardNumber, timestamp);
    }

    public void payOnline(final String cardNumber, final String email,
                          final Double amount, final String currency,
                          final String commerciant, final String description,
                          final int timestamp) throws IllegalArgumentException {
        Card card = database.getCard(cardNumber);
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
        Account senderAccount;
        try {
            senderAccount = database.getAccount(senderIban);
        } catch (Exception ignored) {
            return;
        };

       if (!senderAccount.getOwner().getEmail().equals(senderEmail)) {
            throw new IllegalArgumentException(INVALID_USER);
        }

       Account receiverAccount;
       try {
           receiverAccount = database.getAccount(receiver, senderEmail);
       } catch (Exception ignored) {
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
        if (!database.hasUser(email)) {
            throw new IllegalArgumentException(INVALID_USER);
        } else if (!database.hasAccount(iban)) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        User user = database.getUser(email);
        user.setAlias(alias, iban);
    }

    public List<Transaction> getTransactions(final String email)
                                             throws IllegalArgumentException {
        User user = database.getUser(email);
        if (user == null) {
            throw new IllegalArgumentException(INVALID_USER);
        }

        return user.getTransactions();
    }

    public Report getReport(final String iban, final int startTimestamp,
                            final int endTimestamp) throws IllegalArgumentException {
        Account acct = database.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        return new Report(acct, startTimestamp, endTimestamp);
    }

    public SpendingsReport getSpendingsReport(final String iban, final int startTimestamp,
                                             final int endTimestamp)
                                             throws IllegalArgumentException {
        Account acct = database.getAccount(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        } else if (acct.getType().equals("savings")) {
            throw new IllegalArgumentException(NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT);
        }

        return new SpendingsReport(acct, startTimestamp, endTimestamp);
    }

    public void checkCardStatus(final String cardNumber, final int timestamp)
                                throws IllegalArgumentException{
        Card card = database.getCard(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        }

        card.checkStatus(timestamp);
    }

    public void splitPayment(final List<String> ibans, final double amount,
                             final String currency, final int timestamp)
                             throws IllegalArgumentException {
        /// Add .reverses() because the refs are bad made
        List<Account> accounts = database.getAccounts(ibans).reversed();

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
            }
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
        Account acct = database.getAccount(iban);
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
        Account acct = database.getAccount(iban);
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
