package org.poo.bank;

import lombok.Getter;
import org.poo.bank.account.Account;
import org.poo.bank.account.ClassicAccount;
import org.poo.bank.account.SavingsAccount;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;
import org.poo.utils.Utils;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static org.poo.bank.Constants.*;

/**
 * Our program is just for one bank, so will implement
 * this class using the pattern Singleton.
 */
public final class DataBase {
    private static DataBase dataBase;

    private final Map<String, User> users = new LinkedHashMap<String, User>(); // key = email
    private final Map<String, Account> accounts = new HashMap<String, Account>(); // key = iban
    private final Map<String, Card> cards = new HashMap<String, Card>(); // key = card number
    @Getter
    private int timestamp;

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
    public static DataBase init(User[] users) {
        DataBase instance = getInstance();

        instance.clear();
        instance.addUsers(users);
        instance.timestamp = 0;

        return instance;
    }

    /**
     * Delete all info from database.
     */
    private void clear() {
        users.clear();
        accounts.clear();
        cards.clear();
    }

    /**
     * Add users in database.
     *
     * @param users array of users
     */
    public void addUsers(User[] users) {
        for (User user : users) {
            this.users.put(user.getEmail(), user);
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
    public String createAccount(final String email, final String currency, final double interestRate) {
        User owner = users.get(email);
        if (owner == null) {
            return INVALID_USER;
        }

        /// Find a free iban.
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
    private String assignAccount(Account account, final String email) {
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
    private void addAccount(Account account) {
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

        /// Find a free card number.
        String cardNumber = Utils.generateCardNumber();
        while (cards.get(cardNumber) != null) {
            cardNumber = Utils.generateCardNumber();
        }

        Card card = new Card(owner, cardNumber, isOneTimeCard);

        String err = assignCard(card, iban);
        if (err != null) {
            return err;
        }

        err = assignAccount(accounts.get(iban), email);
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
    private String assignCard(Card card, final String iban) {
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
    private void addCard(Card card) {
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
     * Delete an account (deactivate the account and the cards associated with it),
     * if the user is the owner of it.
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

        accounts.remove(iban);
        return null;
    }

    /**
     * Deactivate a card.
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
}
