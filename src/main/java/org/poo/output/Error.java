package org.poo.output;

import lombok.Getter;

@Getter
public final class Error {
    private static Error instance;

    private Object error;

    private Error() {
    }

    /**
     * @return only instance of this class
     */
    private static Error getInstance() {
        if (instance == null) {
            instance = new Error();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param error a message
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static Error init(final Object error) {
        if (error == null) {
            return null;
        }

        instance = getInstance();

        instance.error = error;

        return instance;
    }
}
