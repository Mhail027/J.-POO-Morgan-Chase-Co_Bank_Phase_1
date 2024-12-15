package org.poo.bank.command;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface Command {
    /**
     * Executes the operation and handles potential exceptions.
     *
     * @param output an ArrayNode where the  results and errors details will be recorded
     */
    void execute(ArrayNode output);
}
