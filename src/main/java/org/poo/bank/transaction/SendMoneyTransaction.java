package org.poo.bank.transaction;

import lombok.Getter;

@Getter
public final class SendMoneyTransaction extends Transaction{
    private final String amount;
    private final String receiverIBAN;
    private final String senderIBAN;
    private final String transferType;
    
    public SendMoneyTransaction(final int timestamp, String description,
                                final String amount, final String receiverIBAN,
                                final String senderIBAN, final String transferType) {
        super(timestamp, description);
        this.amount = amount;
        this.receiverIBAN = receiverIBAN;
        this.senderIBAN = senderIBAN;
        this.transferType = transferType;
    }
}
