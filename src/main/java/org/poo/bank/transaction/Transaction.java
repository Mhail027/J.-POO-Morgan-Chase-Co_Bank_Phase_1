package org.poo.bank.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;

import java.util.List;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Transaction {
    private int timestamp;
    private String description;
    private String amount;
    private String receiverIBAN;
    private String senderIBAN;
    private String transferType;
    private String account;
    private String card;
    private String cardHolder;
    private String commerciant;
    private String currency;
    private List<String> involvedAccounts;
    private String error;


    Transaction() {
    }

    /**
     * Get amount field as:
     *      String, if saves the currency
     *      Double, if it does not contain the currency
     * @return amount as String or Double
     */
    public Object getAmount() {
        try {
            return Double.parseDouble(amount);
        } catch (Exception e) {
            return amount;
        }
    }

    /**
     * @return numerical part from the field amount
     */
    @JsonIgnore
    public double getAmountAsDouble() {
        if (amount == null) {
            return 0;
        }

        String numericalPart = amount.replaceAll("[^0-9.]", "");
        return Double.parseDouble(numericalPart);
    }
}
