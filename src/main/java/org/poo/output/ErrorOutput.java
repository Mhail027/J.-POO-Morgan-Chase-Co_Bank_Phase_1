package org.poo.output;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ErrorOutput {
    private static ErrorOutput instance;

    private Object error;

    private ErrorOutput() {
    }

    /**
     * @return only instance of this class
     */
    private static ErrorOutput getInstance() {
        if (instance == null) {
            instance = new ErrorOutput();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param error a message
     * @return instance
     */
    public static ErrorOutput init(@NonNull final Object error) {
        instance = getInstance();
        instance.error = error;
        return instance;
    }
}
