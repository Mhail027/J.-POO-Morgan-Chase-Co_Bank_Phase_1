package org.poo.bank;

import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.account.ClassicAccount;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;
import org.poo.bank.transaction.Transaction;
import org.poo.utils.Utils;

import java.util.*;
import static org.poo.bank.Constants.*;

/**
 * Our program is just for one bank, so will implement
 * this class using the pattern Singleton.
 */
public final class DataBase {
    private static DataBase dataBase;

    /// key = email
    private final Map<String, User> users = new LinkedHashMap<String, User>();
    /// key = iban
    private final Map<String, Account> accounts = new HashMap<String, Account>();
    /// key = card number
    private final Map<String, Card> cards = new HashMap<String, Card>();
    /// key =  X -> Y, where X and y are currencies
    private static final Map<String, Double> exchangeRates = new HashMap<String, Double>();

    @Getter  private static int timestamp;

    private DataBase() {
    }

    /**
     * @return only instance of this class
     */
    public static DataBase getInstance() {
        if (dataBase == null) {
            dataBase = new DataBase();
        }
        return dataBase;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param users array of users
     * @return created database
     */
    public static DataBase init(final User[] users, final Exchange[] exchanges) {
        DataBase instance = getInstance();

        instance.clear();
        instance.addUsers(users);
        instance.addExchanges(exchanges);
        timestamp = 0;

        return instance;
    }

    /**
     * Delete all info from database.
     */
    private void clear() {
        users.clear();
        accounts.clear();
        cards.clear();
        exchangeRates.clear();
    }

    /**
     * Add users in database.
     *
     * @param newUsers array of users
     */
    private void addUsers(final User[] newUsers) {
        for (User user : newUsers) {
            users.put(user.getEmail(), user);
        }
    }

    /**
     * Add ALL exchange rates in database.
     *
     * @param exchanges initial exchange rates
     */
    private void addExchanges(final Exchange[] exchanges) {
        /// Add initial exchange rates.
        for (Exchange exchange : exchanges) {
            String currencyConversion = exchange.getFrom() + " -> " + exchange.getTo();
            exchangeRates.put(currencyConversion, exchange.getRate());

            currencyConversion = exchange.getTo() + " -> " + exchange.getFrom();
            exchangeRates.put(currencyConversion, 1 / exchange.getRate());
        }

        Set<String> currencies = new HashSet<String>();
        for (Exchange exchange : exchanges) {
            currencies.add(exchange.getFrom());
            currencies.add(exchange.getTo());
        }

        /// Add all exchanges which can be done using the initial ones.
        /// To do that, we use an adaptation of Floyd Warshall Algorithm.
        /// We want just to discover all pair of nodes between which exist a path.
        String[] currenciesArray =  currencies.toArray(new String[0]);
        for (Object intermediateCurrency : currencies.toArray()) {
            for (Object initialCurrency : currencies.toArray()) {
                for (Object finalCurrency : currencies.toArray()) {
                    String exchange = initialCurrency + " -> " + finalCurrency;
                    if (exchangeRates.get(exchange) != null) {
                        continue;
                    }

                    String firstExchange = initialCurrency + " -> " + intermediateCurrency;
                    Double firstExchangeRate = exchangeRates.get(firstExchange);

                    String secondExchange = intermediateCurrency + " -> " + finalCurrency;
                    Double secondExchangeRate = exchangeRates.get(secondExchange);

                    if (firstExchangeRate != null && secondExchangeRate != null) {
                        double exchangeRate = firstExchangeRate * secondExchangeRate;
                        exchangeRates.put(exchange, exchangeRate);

                        String reverseExchange = finalCurrency + " -> " + initialCurrency;
                        double reverseExchangeRate = 1  / exchangeRate;
                        exchangeRates.put(reverseExchange, reverseExchangeRate);

                    }
                }
            }
        }
    }

    /**
     * Create a new account, associate it with a user and add it in database.
     *
     * @param email email of user
     * @param currency currency of account
     * @param interestRate positive number if it's a savings account
     *                     0, if not
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String createAccount(final String email, final String currency,
                                final double interestRate) {
        User owner = users.get(email);
        if (owner == null) {
            return INVALID_USER;
        }

        String iban = Utils.generateIBAN();
        while (accounts.get(iban) != null) {
            iban = Utils.generateIBAN();
        }

        Account newAccount;
        if (interestRate == 0) {
            newAccount = new ClassicAccount(owner, iban, currency);
        } else {
            newAccount = new SavingsAccount(owner, iban, currency, interestRate);
        }


        String err = assignAccount(newAccount, email);
        if (err != null) {
            return err;
        }

        addAccount(newAccount);
        return null;
    }

    /**
     * Assign an account to a user.
     *
     * @param account details of account
     * @param email user's email
     * @return String with an error, if something bad happens
     *         null, if not
     */
    private String assignAccount(final Account account, final String email) {
        User user = users.get(email);
        if (user == null) {
            return INVALID_USER;
        }

        user.addAccount(account);
        return null;
    }

    /**
     * Add an account in the database.
     *
     * @param account details of account
     */
    private void addAccount(final Account account) {
        accounts.put(account.getIban(), account);
    }

    /**
     * Create a new card for a user and assign it to an account.
     * If the user does not have access to account, will receive it.
     *
     * @param iban bank account number
     * @param email user's email
     * @param isOneTimeCard true, if it can do just one transaction
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String createCard(final String iban, final String email,
                           final boolean isOneTimeCard) {
        User owner = users.get(email);
        if (owner == null) {
            return INVALID_USER;
        }

        Account acct = accounts.get(iban);
        if (acct == null) {
            return INVALID_ACCOUNT;
        }

        String cardNumber = Utils.generateCardNumber();
        while (cards.get(cardNumber) != null) {
            cardNumber = Utils.generateCardNumber();
        }

        Card card = new Card(owner, acct, cardNumber, isOneTimeCard);

        String err = assignCard(card, iban);
        if (err != null) {
            return err;
        }

        err = assignAccount(acct, email);
        if (err != null) {
            return err;
        }

        addCard(card);
        return null;
    }

    /**
     * Assign a card to an account.
     *
     * @param card details of card
     * @param iban account number
     * @return String with an error, if something bad happens
     *         null, if not
     */
    private String assignCard(final Card card, final String iban) {
        Account account = accounts.get(iban);
        if (account == null) {
            return INVALID_ACCOUNT;
        }

        account.addCard(card);
        return null;
    }

    /**
     * Add a new card in database.
     *
     * @param card details of card
     *
     */
    private void addCard(final Card card) {
        cards.put(card.getCardNumber(), card);
    }

    /**
     * Add money in an account.
     *
     * @param iban account number
     * @param amount received sum of money
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String addFunds(final String iban, final double amount) {
        Account acct = accounts.get(iban);
        if (acct == null) {
            return INVALID_ACCOUNT;
        }

        acct.addMoney(amount);
        return null;
    }

    /**
     * Delete an account and the cards associated with it,
     * if the user is the owner of the account.
     *
     * @param iban number of account
     * @param email email of user
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String deleteAccount(final String iban, final String email) {
        Account acct = accounts.get(iban);
        if (acct == null) {
            return INVALID_ACCOUNT;
        }

        String err = acct.delete(email);
        if (err != null) {
            return err;
        }

        for (Card card : acct.getCards()) {
            cards.remove(card.getCardNumber());
        }
        accounts.remove(iban);
        return null;
    }

    /**
     * Delete a card, if the user is the owner of it.
     *
     * @param cardNumber number of card (which is unique)
     * @param email email of user
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String deleteCard(final String cardNumber, final String email) {
        Card card = cards.get(cardNumber);
        if (card == null) {
            return INVALID_CARD;
        }

        String err = card.delete(email);
        if (err != null) {
            return err;
        }

        cards.remove(cardNumber);
        return null;
    }

    /**
     * Set the minimum amount of money which an account must have it.
     *
     * @param iban number of account
     * @param minimumBalance minimum amount of money
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String setMinimumBalance(final String iban, final double minimumBalance) {
        Account acct = accounts.get(iban);
        if (acct == null) {
            return INVALID_ACCOUNT;
        }

        acct.setMinimumBalance(minimumBalance);
        return null;
    }

    /**
     * A user try to do a pay online using a card.
     *
     * @param cardNumber number of card
     * @param amount sum of transaction
     * @param currency currency of money
     * @param email user's email
     * @return String with an error, if something bad happens
     *         null, if not
     */
    public String payOnline(final String cardNumber, final double amount,
                            final String currency, final String email) {
        Card card = cards.get(cardNumber);
        if (card == null) {
            return INVALID_CARD;
        }

        double convertedAmount = exchangeMoney(amount, currency, card.getAccount().getCurrency());
        if (convertedAmount == -1) {
            return INVALID_CURRENCY;
        }

        return card.pay(convertedAmount, email);
    }

    /**
     * Exchange a sum of money from a currency to another one.
     *
     * @param amount initial sum of money
     * @param initialCurrency initial currency of money
     * @param finalCurrency currency which we want it
     * @return sum of money converted in the new currency
     *         -1, if it can not be converted
     */
    public static double exchangeMoney(final double amount, final String initialCurrency,
                                 final String finalCurrency) {
        if (initialCurrency == null || initialCurrency.equals(finalCurrency)) {
            return amount;
        }

        Double exchangeRate = exchangeRates.get(initialCurrency + " -> " + finalCurrency);
        if (exchangeRate == null) {
            return -1;
        }

        return amount * exchangeRate;
    }

    public String sendMoney(final double amount, final String description,
                            final String senderIBAN, final String senderEmail,
                            final String receiver) {
        User user = users.get(senderEmail);
        if (user == null) {
            return INVALID_USER;
        }

        String receiverIBAN = user.getAccountByAlias(receiver);
        if (receiverIBAN == null) {
            receiverIBAN = receiver;
        }

        Account senderAcct = accounts.get(senderIBAN);
        Account receiverAcct = accounts.get(receiverIBAN);
        if (senderAcct == null || receiverAcct == null) {
            return null; // should be INVALID_ACCOUNT
        }

        return senderAcct.sendMoney(amount, description, senderEmail, receiverAcct);
    }

    public String setAlias(final String email, final String alias, final String iban) {
        User user = users.get(email);
        if (user == null) {
            return INVALID_USER;
        }

        Account acct = accounts.get(iban);
        if (acct == null) {
            return INVALID_ACCOUNT;
        }

        user.setAlias(alias, iban);
        return null;
    }

    /**
     *  The current timestamp finished, so we increment it.
     */
    public void incrementTimestamp() {
        timestamp++;
    }

    /**
     * @return a collection with all users from database
     */
    public Collection<User> getUsers() {
        return users.values();
    }

    public List<Transaction> getTransactions(final String email) {
        User user = users.get(email);
        if (user == null) {
            return null;
        }

        return user.getTransactions();
    }
}
