package org.poo.bank.geneartor;

import org.poo.bank.database.Database;

import java.util.Random;

@SuppressWarnings("FieldCanBeLocal")
public final class IbanGenerator {
    private static IbanGenerator instance;

    private final int ibanSeed = 1;
    private final int digitBound = 10;
    private final int digitGeneration = 16;
    private final String roStr = "RO";
    private final String pooStr = "POOB";

    private Random ibanRandom;

    private IbanGenerator() {
    }

    /**
     * @return only instance of this class
     */
    private static IbanGenerator getInstance() {
        if (instance == null) {
            instance = new IbanGenerator();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     */
    public static IbanGenerator init() {
        instance = getInstance();
        instance.resetRandom();
        return instance;
    }

    /**
     * @return the IBAN as String
     */
    public String generateRawIBAN() {
        StringBuilder sb = new StringBuilder(roStr);
        for (int i = 0; i < roStr.length(); i++) {
            sb.append(ibanRandom.nextInt(digitBound));
        }

        sb.append(pooStr);
        for (int i = 0; i < digitGeneration; i++) {
            sb.append(ibanRandom.nextInt(digitBound));
        }

        return sb.toString();
    }

    /**
     * @param database database which contains the bank accounts
     * @return an IBAN which is not used already for an account
     */
    public String generateUniqueIBAN(final Database database) {
        String iban = generateRawIBAN();
        while (database.hasAccount(iban)) {
            iban = generateRawIBAN();
        }
        return iban;
    }

    /**
     * Resets the seed between runs.
     */
    public void resetRandom() {
        ibanRandom = new Random(ibanSeed);
    }
}
