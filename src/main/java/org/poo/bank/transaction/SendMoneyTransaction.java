package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public final class SendMoneyTransaction extends Transaction{
    private String amount;
    private String receiverIBAN;
    private String senderIBAN;
    private String transferType;
    
    public SendMoneyTransaction(final String amount, String description,
                                final String receiverIBAN, final String senderIBAN,
                                int timestamp, final String transferType) {
        super(description, timestamp);
        this.amount = amount;
        this.receiverIBAN = receiverIBAN;
        this.senderIBAN = senderIBAN;
        this.transferType = transferType;
    }
}
