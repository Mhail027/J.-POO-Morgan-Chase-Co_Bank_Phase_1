package org.poo.graph;

import lombok.Getter;
import java.util.LinkedList;
import java.util.List;

@Getter
public class GraphNode<T> {
    private T value;
    private List<Edge<T>> edges;

    public GraphNode(final T value) {
        this.value = value;
        edges = new LinkedList<Edge<T>>();
    }

    /**
     * Add an edge which starts from this node.
     *
     * @param dest   destination of the edge
     * @param weight cost of the edge
     */
    public void addEdge(final T dest, final double weight) {
        Edge<T> edge = findEdge(dest);
        if (edge != null) {
            edge.setWeight(weight);
        } else {
            edges.add(new Edge<T>(this.value, dest, weight));
        }
    }

    /**
     * Verify the existence of an edge which starts from the current node
     *
     * @param dest destination of the dge
     * @return true, if the edge exists
     * false, if not
     */
    public Edge<T> findEdge(final T dest) {
        for (Edge<T> edge : edges) {
            if (edge.getDest().equals(dest)) {
                return edge;
            }
        }
        return null;
    }
}
