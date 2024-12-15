package org.poo.output;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@Getter
public final class SimpleOutput {
    private static SimpleOutput instance;

    private String command;
    @Setter private Object output;
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
     * @return instance
     */
    public static SimpleOutput init(@NonNull final String command, @NonNull final Object output,
                                    final int timestamp) {
        instance = getInstance();

        instance.command = command;
        instance.output = output;
        instance.timestamp = timestamp;

        return instance;
    }
}
