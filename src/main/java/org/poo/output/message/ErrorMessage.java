package org.poo.output.message;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class ErrorMessage {
    private static ErrorMessage instance;

    private Object error;
    private int timestamp;

    private ErrorMessage() {
    }

    /**
     * @return only instance of this class
     */
    private static ErrorMessage getInstance() {
        if (instance == null) {
            instance = new ErrorMessage();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param success a message
     * @param timestamp receiving time of command
     * @return instance
     */
    public static ErrorMessage init(@NonNull final Object success, final int timestamp) {
        instance = getInstance();
        instance.error = success;
        instance.timestamp = timestamp;
        return instance;
    }
}
