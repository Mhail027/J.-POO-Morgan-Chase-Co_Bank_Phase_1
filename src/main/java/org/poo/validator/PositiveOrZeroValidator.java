package org.poo.validator;

public class PositiveOrZeroValidator {
    private PositiveOrZeroValidator() {
    }

    public static double validate(Number number) throws IllegalArgumentException {
        if (number == null) {
            throw new IllegalArgumentException("Number can't be null");
        }

        double value = number.doubleValue();
        if (value < 0) {
            throw new IllegalArgumentException(
                    String.format("Number must be positive or zero, currently is %f", value)
            );
        }

        return value;
    }
}
