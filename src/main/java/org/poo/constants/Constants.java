package org.poo.constants;

public abstract class Constants {
    public static final String CLASSIC_CARD =
            "classic card";
    public static final String ONE_TIME_CARD =
            "one time card";
    public static final String INVALID_TYPE_OF_CARD =
            "Type of card is not valid";
    public static final String NO_EXCHANGE_RATE =
            "Doesn't exit an exchange rate between these 2 currencies";

    public static final String INVALID_COMMAND =
            "Command is not valid";
    public static final String INVALID_CURRENCY =
            "Currency not found";
    public static final String INVALID_USER =
            "User not found";
    public static final String INVALID_INTEREST_RATE =
            "Interest rate can't be negative";
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
    public static final String MUST_BE_SAVINGS_ACCOUNT =
            "This is not a savings account";
    public static final String NO_SAVINGS_ACCOUNT_FOR_SPENDINGS_REPORT =
            "This kind of report is not supported for a saving account";
}
