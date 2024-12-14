package org.poo.bank.currency;

import org.poo.graph.DirectedGraph;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.poo.constants.Constants.NO_EXCHANGE_RATE;

public final class CurrencyConvertor {
    private static CurrencyConvertor instance;

    private Map<String, Double> exchangeRates; // key = "X -> Y", where X and Y are currencies

    private CurrencyConvertor() {
        exchangeRates = new HashMap<>();
    }

    /**
     * @return only instance of this class
     */
    private static CurrencyConvertor getInstance() {
        if (instance == null) {
            instance = new CurrencyConvertor();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param exchanges array with initial exchanges of currencies and their rates
     * @return created currency convertor
     */
    public static CurrencyConvertor init(final Exchange[] exchanges)
                                         throws IllegalArgumentException {
        if (exchanges == null) {
            throw new IllegalArgumentException("exchanges can't be null");
        }

        instance = getInstance();
        instance.addExchanges(exchanges);
        return instance;
    }

    /**
     * Add ALL  possible exchange rates in Database.
     *
     * @param exchanges array with initial exchanges of currencies and their rates
     */
    private void addExchanges(final Exchange[] exchanges) {
        DirectedGraph<String> exchangeRatesGraph = new DirectedGraph<String>((x, y) -> x * y);

        String[] initialCurrencies = Arrays.stream(exchanges).
                                             map(Exchange::getFrom).
                                             toArray(String[]::new);
        String[] finalCurrencies = Arrays.stream(exchanges).
                                           map(Exchange::getTo).
                                           toArray(String[]::new);
        Double[] rates =  Arrays.stream(exchanges).
                                  map(Exchange::getRate).
                                  toArray(Double[]::new);
        exchangeRatesGraph.addEdges(initialCurrencies, finalCurrencies, rates);

        Double[] inverseRates = Arrays.stream(rates).
                                        map(x -> 1 / x).
                                        toArray(Double[] :: new);
        exchangeRatesGraph.addEdges(finalCurrencies, initialCurrencies, inverseRates);

        exchangeRates = exchangeRatesGraph.getPathsWeights();
    }

    /**
     * Exchange a sum of money from a currency to another one.
     *
     * @param amount initial sum of money
     * @param initialCurrency initial currency of money
     * @param finalCurrency currency which we want it
     * @return sum of money converted in the new currency
     */
    public double exchangeMoney(final double amount, final String initialCurrency,
                                       final String finalCurrency)
                                       throws IllegalArgumentException {
        if (amount <  0) {
            throw new IllegalArgumentException("amount can't be negative");
        } else if (initialCurrency == null) {
            throw new IllegalArgumentException("initial currency can't be null");
        } else if (finalCurrency == null) {
            throw new IllegalArgumentException("final currency can't be null");
        }

        if (initialCurrency.equals(finalCurrency)) {
            return amount;
        }

        Double exchangeRate = exchangeRates.get(initialCurrency + " -> " + finalCurrency);
        if (exchangeRate == null) {
            throw new IllegalArgumentException(NO_EXCHANGE_RATE);
        }

        return amount * exchangeRate;
    }

}
