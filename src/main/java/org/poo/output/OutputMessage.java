package org.poo.output;

import lombok.Getter;

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
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static OutputMessage init(final Object output, final int timestamp) {
        if (output == null) {
            return null;
        }

        instance = getInstance();

        instance.description = output;
        instance.timestamp = timestamp;

        return instance;
    }
}
