package org.poo.output;

import lombok.Getter;

@Getter
public final class OutputError {
    private static OutputError instance;

    private Object error;
    private int timestamp;

    private OutputError() {
    }

    /**
     * @return only instance of this class
     */
    private static OutputError getInstance() {
        if (instance == null) {
            instance = new OutputError();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param success a message
     * @param timestamp receiving time of command
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static OutputError init(final Object success, final int timestamp) {
        if (success == null) {
            return null;
        }

        instance = getInstance();

        instance.error = success;
        instance.timestamp = timestamp;

        return instance;
    }
}
