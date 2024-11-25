package org.poo.output;

import lombok.Getter;

@Getter
public final class ErrorOutput {
    private static ErrorOutput instance;

    private String command;
    private OutputMessage output;
    private int timestamp;

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
     * @param command name of command
     * @param description output
     * @param timestamp receiving time of command
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static ErrorOutput init(final String command, final Object description,
                                   final int timestamp) {
        if (description == null) {
            return null;
        }

        instance = getInstance();

        instance.command = command;
        instance.output = OutputMessage.init(description, timestamp);
        instance.timestamp = timestamp;

        return instance;
    }
}
