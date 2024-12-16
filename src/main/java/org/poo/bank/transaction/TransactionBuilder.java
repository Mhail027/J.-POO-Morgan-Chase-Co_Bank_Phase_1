package org.poo.bank.transaction;

import java.util.List;

public final class TransactionBuilder {
    private final Transaction transaction;

    public TransactionBuilder() {
        transaction = new Transaction();
    }

    /**
     * Set timestamp field in transaction.
     */
    public TransactionBuilder timestamp(final int timestamp) {
        transaction.setTimestamp(timestamp);
        return this;
    }

    /**
     * Set description field in transaction.
     */
    public TransactionBuilder description(final String description) {
        transaction.setDescription(description);
        return this;
    }

    /**
     * Set amount field in transaction.
     */
    public TransactionBuilder amount(final String amount) {
        transaction.setAmount(amount);
        return this;
    }

    /**
     * Set receiverIBAN field in transaction.
     */
    public TransactionBuilder receiverIBAN(final String receiverIBAN) {
        transaction.setReceiverIBAN(receiverIBAN);
        return this;
    }

    /**
     * Set senderIBAN field in transaction.
     */
    public TransactionBuilder senderIBAN(final String senderIBAN) {
        transaction.setSenderIBAN(senderIBAN);
        return this;
    }

    /**
     * Set transferType field in transaction.
     */
    public TransactionBuilder transferType(final String transferType) {
        transaction.setTransferType(transferType);
        return this;
    }

    /**
     * Set account field in transaction.
     */
    public TransactionBuilder account(final String account) {
        transaction.setAccount(account);
        return this;
    }

    /**
     * Set card field in transaction.
     */
    public TransactionBuilder card(final String card) {
        transaction.setCard(card);
        return this;
    }

    /**
     * Set cardHolder field in transaction.
     */
    public TransactionBuilder cardHolder(final String cardHolder) {
        transaction.setCardHolder(cardHolder);
        return this;
    }

    /**
     * Set commerciant field in transaction.
     */
    public TransactionBuilder commerciant(final String commerciant) {
        transaction.setCommerciant(commerciant);
        return this;
    }

    /**
     * Set currency field in transaction.
     */
    public TransactionBuilder currency(final String currency) {
        transaction.setCurrency(currency);
        return this;
    }

    /**
     * Set involvedAccounts field in transaction.
     */
    public TransactionBuilder involvedAccounts(final List<String> involvedAccounts) {
        transaction.setInvolvedAccounts(involvedAccounts);
        return this;
    }

    /**
     * Set error field in transaction.
     */
    public TransactionBuilder error(final String error) {
        transaction.setError(error);
        return this;
    }

    /**
     * @return the transaction which was built
     */
    public Transaction build() {
        return transaction;
    }

}
