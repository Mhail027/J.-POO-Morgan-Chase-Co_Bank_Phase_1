package org.poo.output;

import lombok.Getter;

@Getter
public final class SimpleOutput {
    private static SimpleOutput instance;

    private String command;
    private Object output;
    private int timestamp;

    private SimpleOutput() {
    }

    /**
     * @return only instance of this class
     */
    private static SimpleOutput getInstance() {
        if (instance == null) {
            instance = new SimpleOutput();
        }
        return instance;
    }

    /**
     * Alternative to constructor, without to break Singleton pattern.
     *
     * @param command name of command
     * @param output output
     * @param timestamp receiving time of command
     * @return null, if output is null
     *         the created simple output, in contrary case
     */
    public static SimpleOutput init(final String command, final Object output,
                                    final int timestamp) {
        if (output == null) {
            return null;
        }

        instance = getInstance();

        instance.command = command;
        instance.output = output;
        instance.timestamp = timestamp;

        return instance;
    }
}
