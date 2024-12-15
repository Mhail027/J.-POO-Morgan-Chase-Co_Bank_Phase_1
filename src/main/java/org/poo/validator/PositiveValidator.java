package org.poo.validator;

public final class PositiveValidator {
    private PositiveValidator() {
    }

    /**
     * Verify if a number is positive.
     *
     * @param number number as an object of type Number
     * @return the number as double if it's valid
     * @throws IllegalArgumentException if the number it's not valid
     */
    public static double validate(final Number number) throws IllegalArgumentException {
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
