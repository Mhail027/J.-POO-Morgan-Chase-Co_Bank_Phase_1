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

    public static final String NO_TO_SAVINGS_ACCOUNT =
            "Can not transfer money to a savings account";
    public static final String CAN_NOT_DELETE_ACCOUNT =
            "Account couldn't be deleted - see org.poo.transactions for details";
    public static final String TOO_LITTLE_MONEY =
            "Not enough founds";

    public static final String NEW_ACCOUNT =
            "New account created";
    public static final String ACCOUNT_DELETED =
            "Account deleted";

    private Constants() {
    }
}
