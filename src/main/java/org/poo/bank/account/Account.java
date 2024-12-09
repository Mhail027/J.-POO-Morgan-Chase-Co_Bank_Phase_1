package org.poo.bank.account;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.poo.bank.card.Card;
import org.poo.bank.client.User;
import org.poo.bank.currency.CurrencyConvertor;
import org.poo.bank.transaction.Transaction;
import org.poo.exception.InsufficientFundsException;

import java.util.LinkedList;
import java.util.List;

import static org.poo.constants.Constants.*;

@Getter
public abstract class Account {
    protected User owner;
    protected final List<User> allowedUsers;
    protected final List<Card> cards; // key == card number
    protected final List<Transaction> transactions;

    @JsonProperty("IBAN") protected String iban;
    protected String type;

    protected String currency;
    @Setter protected double minimumBalance;
    protected double balance;

    public Account(final User owner, final String iban, final String currency,
                   final int timestamp) throws IllegalArgumentException {
        if (owner == null) {
            throw new IllegalArgumentException("owner can't be null");
        } else if (iban == null) {
            throw new IllegalArgumentException("iban can't be null");
        } else if (currency == null) {
            throw new IllegalArgumentException("currency can't be null");
        }

        this.owner = owner;
        this.iban = iban;
        this.currency = currency;

        minimumBalance = 0;
        balance = 0;

        allowedUsers = new LinkedList<>();
        cards = new LinkedList<>();
        transactions = new LinkedList<>();
        addUser(owner);

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .description(NEW_ACCOUNT).build();
        addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    /**
     * @param amount received sum
     */
    public void addMoney(final double amount) {
        balance += amount;
    }

    /**
     * A new card have access to account.
     * @param card details of the card
     */
    public void addCard(final Card card) throws IllegalArgumentException {
        if (card == null) {
            throw new IllegalArgumentException("card can't be null");
        } else if (hasCard(card.getCardNumber())) {
            throw new IllegalArgumentException("Card has already access to account");
        }

        cards.add(card);
        addUser(card.getOwner());
    }


    /**
     * Verify if a card has access to account.
     * @param cardNumber number of card
     * @return true, if card is associated with the account
     *         false, if not
     */
    public boolean hasCard(final String cardNumber) {
        return cards.stream()
                       .anyMatch(card -> card.getCardNumber().equals(cardNumber));
    }

    /**
     * Revoke the access permission of a card and its user to account.
     * @param cardNumber number of card
     */
    public void removeCard(final String cardNumber) throws IllegalArgumentException {
        User cardOwner = getCard(cardNumber).getOwner();

        cards.removeIf(card -> card.getCardNumber().equals(cardNumber));
        allowedUsers.removeIf(user -> user.getEmail().equals(cardOwner.getEmail()));
    }

    private Card getCard(final String cardNumber) throws IllegalArgumentException {
        for (Card card : cards) {
            if (card.getCardNumber().equals(cardNumber)) {
                return card;
            }
        }
        throw new IllegalArgumentException(INVALID_CARD);
    }

    /**
     * A new user have access to account.
     * @param user details of user
     */
    public void addUser(final User user) throws IllegalArgumentException {
        if (user == null) {
            throw new IllegalArgumentException("user can't be null");
        } else if (hasUser(user.getEmail())) {
            throw  new IllegalArgumentException("User has already access to account");
        }

        allowedUsers.add(user);
        user.addAccount(this);
    }

    /**
     * @param email email of user
     * @return true, if user has access to account
     *         false, if not
     */
    private boolean hasUser(final String email) {
        return allowedUsers.stream()
                       .anyMatch(user -> user.getEmail().equals(email));
    }

    /**
     * No user will have access to this account anymore.
     */
    public void delete() {
        for (User user : allowedUsers) {
            user.removeAccount(iban);
        }
    }

    /**
     * Take out money from account to pay for something.
     * @param amount sum of monet
     */
    public void payOnline(final double amount) throws IllegalArgumentException,
                                                              InsufficientFundsException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (balance < amount) {
            throw new InsufficientFundsException();
        }

        balance -= amount;
    }

    /**
     * Send money to an account.
     * @param receiver receiver account
     * @param amount sum of money
     * @param currencyConvertor convertor which exchange money
     * @param timestamp moment of time at which was given the command
     * @param description description of transaction
     */
    public void sendMoney(final Account receiver, final double amount,
                          final CurrencyConvertor currencyConvertor, final int timestamp,
                          final String description) throws IllegalArgumentException {
        if (receiver == null) {
            throw new IllegalArgumentException("receiver can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (balance < amount) {
            Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                              .description(INSUFFICIENT_FUNDS).build();
            addTransaction(transaction);
            owner.addTransaction(transaction);
            return;
        } else if (currencyConvertor == null) {
            throw new IllegalArgumentException("currency convertor can't be null");
        }

        balance -= amount;
        receiver.receivesMoney(iban, amount, currency, currencyConvertor, timestamp, description);

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .description(description)
                                          .amount(amount + " " + currency)
                                          .senderIBAN(iban)
                                          .receiverIBAN(receiver.getIban())
                                          .transferType("sent").build();
        addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    /**
     * Receive money from an account.
     * @param senderIban account number of sender
     * @param amount sum of money
     * @param moneyCurrency currency of money
     * @param currencyConvertor convertor which exchange money
     * @param timestamp moment of time at which was given the command of sending money
     * @param description description of transaction
     */
    private void receivesMoney(final String senderIban, final double amount,
                               final String moneyCurrency,
                               final CurrencyConvertor currencyConvertor, final int timestamp,
                               final String description) throws IllegalArgumentException {
        if (senderIban == null) {
            throw new IllegalArgumentException("sender iban can't be null");
        } else if (amount < 0) {
            throw new IllegalArgumentException("amount of money can't be negative");
        } else if (moneyCurrency == null) {
            throw new IllegalArgumentException("currency can't be null");
        } else if (currencyConvertor == null) {
            throw new IllegalArgumentException("currency convertor can't be null");
        }

        double convertedAmount = currencyConvertor.exchangeMoney(amount, moneyCurrency, currency);
        balance += convertedAmount;

        Transaction transaction = new Transaction.TransactionBuilder(timestamp)
                                          .description(description)
                                          .amount(amount + " " + currency)
                                          .senderIBAN(senderIban)
                                          .receiverIBAN(iban)
                                          .transferType("received").build();
        addTransaction(transaction);
        owner.addTransaction(transaction);
    }

    /**
     * Save the info of a transaction.
     * @param transaction details of transaction
     */
    public void addTransaction(Transaction transaction) {
        transactions.add(transaction);
    }

    public boolean haveEnoughMoney(final double amount, final String moneyCurrency,
                                   final CurrencyConvertor currencyConvertor)
                                   throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (moneyCurrency == null) {
            throw new IllegalArgumentException("money currency can't be negative");
        } else if (currencyConvertor == null) {
            throw new IllegalArgumentException("currency convertor can't be null");
        }

        double convertedAmount = currencyConvertor.exchangeMoney(amount, moneyCurrency, currency);
        return balance - convertedAmount >= 0;
    }

    public void splitPayment(final double amount, final String moneyCurrency,
                           final CurrencyConvertor currencyConvertor,
                           final Transaction transaction) throws IllegalArgumentException {
        if (amount < 0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (moneyCurrency == null) {
            throw new IllegalArgumentException("money currency can't be null");
        } else if (currencyConvertor == null) {
            throw new IllegalArgumentException("currency convertor can't be null");
        } else if (transaction == null) {
            throw new IllegalArgumentException("transaction can't be null");
        }

        double convertedAmount = currencyConvertor.exchangeMoney(amount, moneyCurrency, currency);
        balance -= convertedAmount;

        addTransaction(transaction);
        owner.addTransaction(transaction);
    }


    @JsonIgnore
    public final List<User> getAllowedUsers() {
        return allowedUsers;
    }

    @JsonIgnore
    public final User getOwner() {
        return owner;
    }

    @JsonIgnore
    public final double getMinimumBalance() {
        return minimumBalance;
    }

    @JsonIgnore
    public List<Transaction> getTransactions() {
        return transactions;
    }
}
