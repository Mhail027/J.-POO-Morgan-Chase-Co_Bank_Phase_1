package org.poo.validator;

public final class NotNullValidator {
    private NotNullValidator() {
    }

    /**
     * Verify if an object exist(it is not null).
     *
     * @param o address of object
     * @return the object if it's valid
     * @throws IllegalArgumentException if the object isn't valid
     */
    public static Object validate(final Object o) throws IllegalArgumentException {
        if (o == null) {
            throw new IllegalArgumentException("Object can't be null");
        }
        return o;
    }
}
