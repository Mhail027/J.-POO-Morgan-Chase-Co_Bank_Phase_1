package org.poo.bank.geneartor;

import org.poo.bank.database.Database;

import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public final class CardNumberGenerator {
    private static CardNumberGenerator instance;

    private final int cardSeed = 2;
    private final int digitBound = 10;
    private final int digitGeneration = 16;

    private Random cardNumberRandom;

    private CardNumberGenerator() {
    }

    /**
     * @return only instance of this class
     */
    private static CardNumberGenerator getInstance() {
        if (instance == null) {
            instance = new CardNumberGenerator();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     */
    public static CardNumberGenerator init() {
        instance = getInstance();
        instance.resetRandom();
        return instance;
    }

    /**
     * @return the card number as String
     */
    public String generateRawCardNumber() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digitGeneration; i++) {
            sb.append(cardNumberRandom.nextInt(digitBound));
        }

        return sb.toString();
    }

    /**
     * @param database database which contains the bank cards
     * @return a card number which is not used already for a card
     */
    public String generateUniqueCardNumber(final Database database) {
        String cardNumber = generateRawCardNumber();
        while (database.hasCard(cardNumber)) {
            cardNumber = generateRawCardNumber();
        }
        return cardNumber;
    }

    /**
     * Resets the seed between runs.
     */
    public void resetRandom() {
        cardNumberRandom = new Random(cardSeed);
    }
}
