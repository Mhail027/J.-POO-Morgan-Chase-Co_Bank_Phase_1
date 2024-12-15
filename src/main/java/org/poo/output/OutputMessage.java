package org.poo.output;

import lombok.Getter;
import lombok.NonNull;

@Getter
public final class OutputMessage {
    private static OutputMessage instance;

    private Object description;
    private int timestamp;

    private OutputMessage() {
    }

    /**
     * @return only instance of this class
     */
    private static OutputMessage getInstance() {
        if (instance == null) {
            instance = new OutputMessage();
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
    public static OutputMessage init(@NonNull final Object output, final int timestamp) {
        instance = getInstance();
        instance.description = output;
        instance.timestamp = timestamp;
        return instance;
    }
}
