package org.poo.output.message;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class SuccessMessage {
    private static SuccessMessage instance;

    private Object success;
    private int timestamp;

    private SuccessMessage() {
    }

    /**
     * @return only instance of this class
     */
    private static SuccessMessage getInstance() {
        if (instance == null) {
            instance = new SuccessMessage();
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
    public static SuccessMessage init(@NonNull final Object success, final int timestamp) {
        instance = getInstance();
        instance.success = success;
        instance.timestamp = timestamp;
        return instance;
    }
}
