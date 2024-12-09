package org.poo.graph;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class DirectedGraph<T> {
    private final Map<T, GraphNode<T>> nodes;  /// key = values of node
    private final BiFunction<Double, Double, Double> operation;

    public DirectedGraph(final BiFunction<Double, Double, Double> operation) {
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
    public void addEdges(final T[] sources, final T[] destinations,
                         final Double[] weights) {
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
    public void addNode(final T value) {
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
    private boolean hasNode(final T value) {
        return nodes.get(value) != null;
    }

    /**
     * Get a node from graph.
     *
     * @param value value of node
     * @return the node corresponding with the value
     */
    private GraphNode<T> getNode(final T value) {
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
        Map<String, Double> edgesWeights = new HashMap<>();
        for (GraphNode<T> node : nodes.values()) {
            for (Edge<T> edge : node.getEdges()) {
                edgesWeights.put(edge.getSrc() + " -> " + edge.getDest(), edge.getWeight());
            }
        }

        return edgesWeights;
    }
 }
