package org.poo.bank.transaction;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;

import java.util.List;

@Getter
@JsonInclude(JsonInclude.Include.NON_NULL)
public final class Transaction {
    private final int timestamp;
    private final String description;
    private final String amount;
    private final String receiverIBAN;
    private final String senderIBAN;
    private final String transferType;
    private final String account;
    private final String card;
    private final String cardHolder;
    private final String commerciant;
    private final String currency;
    private final List<String> involvedAccounts;
    private final String error;


    private Transaction(final Builder builder) {
        timestamp = builder.timestamp;
        description = builder.description;
        amount = builder.amount;
        receiverIBAN = builder.receiverIBAN;
        senderIBAN = builder.senderIBAN;
        transferType = builder.transferType;
        account = builder.account;
        card = builder.card;
        cardHolder = builder.cardHolder;
        commerciant = builder.commerciant;
        currency = builder.currency;
        involvedAccounts = builder.involvedAccounts;
        error = builder.error;
    }

    public static final class Builder {
        private final int timestamp;
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

        public Builder(final int timestamp) {
            this.timestamp = timestamp;
        }

        public Builder description(final String description) {
            this.description = description;
            return this;
        }

        public Builder amount(final String amount) {
            this.amount = amount;
            return this;
        }

        public Builder receiverIBAN(final String receiverIBAN) {
            this.receiverIBAN = receiverIBAN;
            return this;
        }

        public Builder senderIBAN(final String senderIBAN) {
            this.senderIBAN = senderIBAN;
            return this;
        }

        public Builder transferType(final String transferType) {
            this.transferType = transferType;
            return this;
        }

        public Builder account(final String account) {
            this.account = account;
            return this;
        }

        public Builder card(final String card) {
            this.card = card;
            return this;
        }

        public Builder cardHolder(final String cardHolder) {
            this.cardHolder = cardHolder;
            return this;
        }

        public Builder commerciant(final String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        public Builder currency(final String currency) {
            this.currency = currency;
            return this;
        }

        public Builder involvedAccounts(final List<String> involvedAccounts) {
            this.involvedAccounts = involvedAccounts;
            return this;
        }

        public Builder error(final String error) {
            this.error = error;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
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
