package org.poo.output;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class OutputSuccess {
    private static OutputSuccess instance;

    private Object success;
    private int timestamp;

    private OutputSuccess() {
    }

    /**
     * @return only instance of this class
     */
    private static OutputSuccess getInstance() {
        if (instance == null) {
            instance = new OutputSuccess();
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
    public static OutputSuccess init(@NonNull final Object success, final int timestamp) {
        instance = getInstance();
        instance.success = success;
        instance.timestamp = timestamp;
        return instance;
    }
}
