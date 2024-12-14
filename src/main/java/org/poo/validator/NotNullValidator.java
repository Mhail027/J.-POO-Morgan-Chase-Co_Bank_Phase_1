package org.poo.validator;

public class NotNullValidator {
    private NotNullValidator() {
    }

    public static Object validate(Object o) throws IllegalArgumentException {
        if (o == null) {
            throw new IllegalArgumentException("Object can't be null");
        }
        return o;
    }
}
