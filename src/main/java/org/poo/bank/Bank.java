package org.poo.bank;

import lombok.Getter;
import lombok.NonNull;
import org.poo.bank.client.User;
import org.poo.bank.currency.CurrencyConvertor;
import org.poo.bank.currency.Exchange;
import org.poo.bank.database.Database;
import org.poo.bank.geneartor.CardNumberGenerator;
import org.poo.bank.geneartor.IbanGenerator;
import org.poo.validator.NotNullValidator;

@Getter
public final class Bank {
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
}
