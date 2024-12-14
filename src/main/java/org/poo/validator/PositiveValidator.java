package org.poo.validator;

public class PositiveValidator {
    private PositiveValidator() {
    }

    public static double validate(Number number) throws IllegalArgumentException {
        if (number == null) {
            throw new IllegalArgumentException("Number can't be null");
        }

        double value = number.doubleValue();
        if (value <= 0) {
            throw new IllegalArgumentException(
                    String.format("Number must be positive, currently is %f", value)
            );
        }

        return value;
    }
}
