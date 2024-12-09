package org.poo.throwable;
import static org.poo.constants.Constants.INSUFFICIENT_FUNDS;

public class InsufficientFundsException extends Exception{
    public InsufficientFundsException() {
        super(INSUFFICIENT_FUNDS);
    }
}
