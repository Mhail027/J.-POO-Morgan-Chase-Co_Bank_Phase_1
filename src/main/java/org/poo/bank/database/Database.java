package org.poo.bank.database;

import lombok.NonNull;

import org.poo.bank.account.Account;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.List;
import java.util.LinkedList;

import static org.poo.constants.Constants.INVALID_ACCOUNT;
import static org.poo.constants.Constants.INVALID_USER;
import static org.poo.constants.Constants.INVALID_CARD;


public final class Database {
    private static Database instance;

    private final Map<String, User> users; // key = email
    private final Map<String, Account> accounts; // key = iban
    private final Map<String, Card> cards; // key = card number

    private Database() {
        users = new LinkedHashMap<>();
        accounts = new HashMap<>();
        cards = new HashMap<>();
    }

    /**
     * @return only instance of this class
     */
    private static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param users array of users
     * @return created Database
     */
    public static Database init(@NonNull final User[] users) {
        instance = getInstance();

        instance.clear();
        instance.addUsers(users);

        return instance;
    }

    /**
     * Delete all info from Database.
     */
    private void clear() {
        users.clear();
        accounts.clear();
        cards.clear();
    }

    /**
     * Add users in Database.
     *
     * @param newUsers array of users
     */
    private void addUsers(@NonNull final User[] newUsers) {
        for (User user : newUsers) {
            users.put(user.getEmail(), user);
        }
    }

    /**
     * @return a collection with all users from Database
     */
    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * @param email email of someone
     * @return true, if exists a user with this email
     *         false, if not
     */
    public boolean hasUser(@NonNull final String email) {
        return users.containsKey(email);
    }

    /**
     * @param email email of someone
     * @return details of user with given email
     * @throws IllegalArgumentException if no user has the given email
     */
    public User getUser(@NonNull final String email) throws IllegalArgumentException {
        User user = users.get(email);
        if (user == null) {
            throw new IllegalArgumentException(INVALID_USER);
        }
        return user;
    }

    /**
     * Add a new account in Database.
     *
     * @param acct details of account
     */
    public void addAccount(@NonNull final Account acct) throws IllegalArgumentException {
        accounts.put(acct.getIban(), acct);
    }

    /**
     * Remove an account and all cards associated with it.
     *
     * @param iban number of account
     * @throws IllegalArgumentException if no account have the given iban
     */
    public void removeAccount(@NonNull final String iban) throws IllegalArgumentException {
        Account acct = getAccount(iban);
        removeCards(acct.getCards());
        acct.delete();
    }

    /**
     * @param iban account number
     * @return details of the account with given iban
     * @throws IllegalArgumentException if no account has the given iban
     */
    public Account getAccount(@NonNull final String iban) throws IllegalArgumentException {
        Account acct = accounts.get(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }
        return acct;
    }

    /**
     * @param identifier alias OR iban
     * @param email email of user
     * @return details of account with given identifier
     */
    public Account getAccount(@NonNull final String identifier, @NonNull final String email) {
        User user = getUser(email);
        String iban = user.getAccountByAlias(identifier);
        if (iban == null) {
            iban = identifier;
        }
        return getAccount(iban);
    }

    /**
     * Does a list with details of accounts which has the given ibans.
     *
     * @param ibans list with account numbers as Strings
     * @return list with info of accounts
     */
    public List<Account> getAccounts(@NonNull final List<String> ibans) {
        List<Account> accountsList = new LinkedList<>();
        for (String iban : ibans) {
            Account acct = getAccount(iban);
            accountsList.add(acct);
        }
        return accountsList;
    }

    /**
     * @param iban account number
     * @return true, if an account with given iban is in Database
     *         false, if not
     */
    public boolean hasAccount(@NonNull final String iban) {
        return accounts.containsKey(iban);
    }

    /**
     * Add a new card in Database.
     *
     * @param card details of card
     */
    public void addCard(@NonNull final Card card) {
        cards.put(card.getCardNumber(), card);
    }

    /**
     * Remove cards from Database.
     *
     * @param oldCards array of cards which will be removed
     */
    public void removeCards(@NonNull final List<Card> oldCards) {
        for (Card card : oldCards) {
            cards.remove(card.getCardNumber());
        }
    }

    /**
     * Remove a card from Database.
     *
     * @param cardNumber number of card
     * @throws IllegalArgumentException if no card has the given number
     */
    public void removeCard(@NonNull final String cardNumber) throws IllegalArgumentException {
        Card card = cards.remove(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        }
        card.delete();
    }

    /**
     * @param cardNumber number of card
     * @return true, if a card with the given number is in dataBase
     *         false, if not
     */
    public boolean hasCard(@NonNull final String cardNumber) {
        return cards.containsKey(cardNumber);
    }

    /**
     * @param cardNumber number of a card
     * @return details of the card with given number
     * @throws IllegalArgumentException if no card has the given number
     */
    public Card getCard(@NonNull final String cardNumber) throws IllegalArgumentException {
        Card card = cards.get(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        }
        return card;
    }

}
