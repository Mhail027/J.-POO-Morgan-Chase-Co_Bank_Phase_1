package org.poo.graph;

import lombok.NonNull;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

public class DirectedGraph<T> {
    private final Map<T, GraphNode<T>> nodes;  // key = values of node
    private final BiFunction<Double, Double, Double> operation;

    public DirectedGraph(@NonNull final BiFunction<Double, Double, Double> operation) {
        nodes = new HashMap<>();
        this.operation = operation;
    }

    /**
     * Add new edges in graph.
     *
     * @param sources array with values of starting nodes
     * @param destinations array with values of ending nodes
     * @param weights array with costs of edges
     */
    public void addEdges(@NonNull final T[] sources, @NonNull final T[] destinations,
                         @NonNull final Double[] weights) throws IllegalArgumentException {
        if (sources.length != destinations.length || sources.length != weights.length) {
            throw new IllegalArgumentException("Arrays don't have same number of elements.");
        }

        for (int i = 0; i < sources.length; ++i) {
            addNode(sources[i]);
            addNode(destinations[i]);

            GraphNode<T> src = getNode(sources[i]);
            src.addEdge(destinations[i], weights[i]);
        }
    }

    /**
     * Add new node in graph.
     *
     * @param value value of node
     */
    public void addNode(@NonNull final T value) {
        if (!hasNode(value)) {
            nodes.put(value, new GraphNode<>(value));
        }
    }

    /**
     * Verify the existence of a node in graph.
     *
     * @param value value of node
     * @return true, if the node exists
     *         false, if not
     */
    private boolean hasNode(@NonNull final T value) {
        return nodes.get(value) != null;
    }

    /**
     * Get a node from graph.
     *
     * @param value value of node
     * @return the node corresponding with the value
     */
    private GraphNode<T> getNode(@NonNull final T value) {
        return nodes.get(value);
    }

    /**
     * @return hashmap in which:
     *          the keys are the paths, in form "value start node -> value final node",
     *          the values are the weights of paths using the operation of graph between
     *          costs of edges
     */
    public Map<String, Double> getPathsWeights() {
        Map<String, Double> pathWeights = getEdgesWeights();
        Collection<T> nodesCollection = nodes.keySet();

        for (T src : nodesCollection) {
            for (T dest : nodesCollection) {
                for (T intermediate : nodesCollection) {
                    if (pathWeights.containsKey(src + " -> " + dest)) {
                        continue;
                    }

                    if (pathWeights.containsKey(src + " -> " + intermediate)
                    && pathWeights.containsKey(intermediate + " -> " + dest)) {
                        double weight = operation.apply(
                                pathWeights.get(src + " -> " + intermediate),
                                pathWeights.get(intermediate + " -> " + dest)
                        );
                        pathWeights.put(src + " -> " + dest, weight);
                    }
                }
            }
        }

        return pathWeights;
    }

    /**
     * @return hashmap in which:
     *          the keys are the edges, in form "value start node -> value final node",
     *          the values are the weights of edges
     */
    private Map<String, Double> getEdgesWeights() {
        return nodes.values().stream()
                .flatMap(node -> node.getEdges().stream())
                .collect(Collectors.toMap(
                        edge -> edge.getSrc() + " -> " + edge.getDest(),
                        Edge::getWeight
                        )
                );
    }
 }
