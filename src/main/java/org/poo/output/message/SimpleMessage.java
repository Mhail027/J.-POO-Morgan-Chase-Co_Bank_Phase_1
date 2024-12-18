package org.poo.output.message;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class SimpleMessage {
    private static SimpleMessage instance;

    private Object description;
    private int timestamp;

    private SimpleMessage() {
    }

    /**
     * @return only instance of this class
     */
    private static SimpleMessage getInstance() {
        if (instance == null) {
            instance = new SimpleMessage();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param output output
     * @param timestamp the time from where the output comes
     * @return instance
     */
    public static SimpleMessage init(@NonNull final Object output, final int timestamp) {
        instance = getInstance();
        instance.description = output;
        instance.timestamp = timestamp;
        return instance;
    }
}
