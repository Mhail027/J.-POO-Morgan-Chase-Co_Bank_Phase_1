package org.poo.graph;

import lombok.Getter;
import lombok.NonNull;

import java.util.LinkedList;
import java.util.List;

@Getter
public class GraphNode<T> {
    private final T value;
    private final List<Edge<T>> edges;

    public GraphNode(@NonNull final T value) {
        this.value = value;
        edges = new LinkedList<>();
    }

    /**
     * Add an edge which starts from this node.
     *
     * @param dest   destination of the edge
     * @param weight cost of the edge
     */
    public void addEdge(@NonNull final T dest, @NonNull final double weight) {
        Edge<T> edge = findEdge(dest);

        if (edge != null) {
            edge.setWeight(weight);
        } else {
            edges.add(new Edge<>(this.value, dest, weight));
        }
    }

    /**
     * Verify the existence of an edge which starts from the current node
     *
     * @param dest destination of the dge
     * @return true, if the edge exists
     * false, if not
     */
    public Edge<T> findEdge(@NonNull final T dest) {
        return edges.stream()
                       .filter(edge -> edge.getDest().equals(dest))
                       .findFirst().orElse(null);
    }
}
