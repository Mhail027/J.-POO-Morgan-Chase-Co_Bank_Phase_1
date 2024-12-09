package org.poo.command;

import com.fasterxml.jackson.databind.node.ArrayNode;

public interface Command {
    void execute(ArrayNode output);
}
