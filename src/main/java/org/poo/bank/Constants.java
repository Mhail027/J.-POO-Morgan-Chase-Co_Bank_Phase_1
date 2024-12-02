package org.poo.bank;

public final class Constants {
    public static final String INVALID_COMMAND =
            "Command is not valid";
    public static final String INVALID_CURRENCY =
            "Currency not found";
    public static final String INVALID_USER =
            "User not found";
    public static final String INVALID_ACCOUNT =
            "Account not found";
    public static final String INVALID_CARD =
            "Card not found";
    public static final String CAN_NOT_DELETE_ACCOUNT =
            "Account couldn't be deleted - see org.poo.transactions for details";
    public static final String INSUFFICIENT_FUNDS =
            "Insufficient funds";

    public static final String NEW_ACCOUNT =
            "New account created";
    public static final String ACCOUNT_DELETED =
            "Account deleted";
    public static final String NEW_CARD =
            "New card created";
    public static final String DESTROYED_CARD =
            "The card has been destroyed";
    public static final String CARD_WILL_BE_FROZEN =
            "You have reached the minimum amount of funds, the card will be frozen";
    public static final String FROZEN_CARD =
            "The card is frozen";
    public static final String BLOCKED_CARD =
            "The card is frozen";
    public static final String NO_SAVINGS_ACCOUNT =
            "This is not a savings account";

    private Constants() {
    }
}
