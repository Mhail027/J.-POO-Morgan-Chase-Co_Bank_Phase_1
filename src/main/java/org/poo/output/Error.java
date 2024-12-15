package org.poo.output;

import lombok.Getter;
import lombok.NonNull;

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
     * @return instance
     */
    public static Error init(@NonNull final Object error) {
        instance = getInstance();
        instance.error = error;
        return instance;
    }
}
