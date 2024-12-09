package org.poo.bank.transaction;

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


    private Transaction (TransactionBuilder builder) {
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
    }

    public static class TransactionBuilder {
        private final int timestamp;
        private String description;
        private String amount;
        private String receiverIBAN;
        private String senderIBAN;
        private String transferType;
        private String account;
        private String card;
        private String cardHolder;
        public String commerciant;
        public String currency;
        public List<String> involvedAccounts;

        public TransactionBuilder(final int timestamp) {
            this.timestamp = timestamp;
        }

        public TransactionBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public TransactionBuilder amount(final String amount) {
            this.amount = amount;
            return this;
        }

        public TransactionBuilder receiverIBAN(final String receiverIBAN) {
            this.receiverIBAN = receiverIBAN;
            return this;
        }

        public TransactionBuilder senderIBAN(final String senderIBAN) {
            this.senderIBAN = senderIBAN;
            return this;
        }

        public TransactionBuilder transferType(final String transferType) {
            this.transferType = transferType;
            return this;
        }

        public TransactionBuilder account(final String account) {
            this.account = account;
            return this;
        }

        public TransactionBuilder card(final String card) {
            this.card = card;
            return this;
        }

        public TransactionBuilder cardHolder(final String cardHolder) {
            this.cardHolder = cardHolder;
            return this;
        }

        public TransactionBuilder commerciant(final String commerciant) {
            this.commerciant = commerciant;
            return this;
        }

        public TransactionBuilder currency(final String currency) {
            this.currency = currency;
            return this;
        }

        public TransactionBuilder involvedAccounts(List<String> involvedAccounts) {
            this.involvedAccounts = involvedAccounts;
            return this;
        }

        public Transaction build() {
            return new Transaction(this);
        }
    }

    public Object getAmount() {
        try{
            return Double.parseDouble(amount);
        } catch (Exception e) {
            return amount;
        }
    }
}
