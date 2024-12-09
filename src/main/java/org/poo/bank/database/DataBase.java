package org.poo.bank.database;

import org.poo.bank.account.Account;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;

import java.util.*;

import static org.poo.constants.Constants.*;


public final class DataBase {
    private static DataBase instance;

    private final Map<String, User> users; // key = email
    private final Map<String, Account> accounts; // key = iban
    private final Map<String, Card> cards; // key = card number

    private DataBase() {
        users = new LinkedHashMap<String, User>();
        accounts = new HashMap<String, Account>();
        cards = new HashMap<String, Card>();
    }

    /**
     * @return only instance of this class
     */
    private static DataBase getInstance() {
        if (instance == null) {
            instance = new DataBase();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param users array of users
     * @return created database
     */
    public static DataBase init(final User[] users) throws IllegalArgumentException {
        if (users == null) {
            throw new IllegalArgumentException("users can't be null");
        }

        instance = getInstance();

        instance.clear();
        instance.addUsers(users);

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
     * @param newUsers array of users
     */
    private void addUsers(final User[] newUsers) {
        for (User user : newUsers) {
            users.put(user.getEmail(), user);
        }
    }

    /**
     * @return a collection with all users from database
     */
    public Collection<User> getUsers() {
        return users.values();
    }

    /**
     * @param email email of someone
     * @return true, if exists a user with this email
     *         false, if not
     */
    public boolean hasUser(final String email) {
        return users.containsKey(email);
    }

    /**
     * @param email email of someone
     * @return details of user with given email
     */
    public User getUser(final String email) {
        return users.get(email);
    }

    /**
     * Add a new account in dataBase.
     *
     * @param acct details of account
     */
    public void addAccount(final Account acct) throws IllegalArgumentException {
        if (acct == null) {
            throw new IllegalArgumentException("acct can't be null");
        }

        accounts.put(acct.getIban(), acct);
    }

    /**
     * Remove an account and all cards associated with it.
     *
     * @param iban number of account
     */
    public void removeAccount(final String iban) throws IllegalArgumentException {
        Account acct = accounts.remove(iban);
        if (acct == null) {
            throw new IllegalArgumentException(INVALID_ACCOUNT);
        }

        removeCards(acct.getCards());

        acct.delete();
    }

    /**
     * @param iban account number
     * @return details of the account with given iban
     */
    public Account getAccount(final String iban) {
        return accounts.get(iban);
    }

    /**
     * @param identifier alias OR iban
     * @param email email of user
     * @return details of account with given identifier
     */
    public Account getAccount(final String identifier, final String email)
                              throws IllegalArgumentException {
        User user = getUser(email);
        if (user == null) {
            throw new IllegalArgumentException(INVALID_USER);
        }

        String iban = user.getAccountByAlias(identifier);
        if (iban == null) {
            iban = identifier;
        }

        return getAccount(iban);
    }

    public List<Account> getAccounts (List<String> ibans)
                                      throws IllegalArgumentException {
        List<Account> accountsList = new LinkedList<>();

        for (String iban : ibans) {
            Account acct = getAccount(iban);
            if (acct == null) {
                throw new IllegalArgumentException(INVALID_ACCOUNT);
            }
            accountsList.add(acct);
        }

        return accountsList;
    }

    /**
     * @param iban account number
     * @return true, if an account with given iban is in dataBase
     *         false, if not
     */
    public boolean hasAccount(final String iban) {
        return accounts.containsKey(iban);
    }

    /**
     * Add a new card in dataBase.
     *
     * @param card details of card
     */
    public void addCard(final Card card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("card can't be null");
        }

        cards.put(card.getCardNumber(), card);
    }

    /**
     * Remove cards from dataBase.
     *
     * @param oldCards array of cards which will be removed
     */
    public void removeCards(final List<Card> oldCards) {
        for (Card card : oldCards) {
            cards.remove(card.getCardNumber());
        }
    }

    /**
     * Remove a card from dataBase.
     *
     * @param cardNumber number of card
     */
    public void removeCard(final String cardNumber, final int timestamp)
                           throws IllegalArgumentException {
        Card card = cards.remove(cardNumber);
        if (card == null) {
            throw new IllegalArgumentException(INVALID_CARD);
        }

        card.delete(timestamp);
    }

    /**
     * @param cardNumber number of card
     * @return true, if a card with the given number is in dataBae
     *         false, if not
     */
    public boolean hasCard(final String cardNumber) {
        return cards.containsKey(cardNumber);
    }

    /**
     * @param cardNumber number of a card
     * @return details of the card with given number
     */
    public Card getCard(final String cardNumber) {
        return cards.get(cardNumber);
    }

}
