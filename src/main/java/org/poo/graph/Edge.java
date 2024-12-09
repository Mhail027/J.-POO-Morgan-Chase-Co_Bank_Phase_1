package org.poo.graph;

import lombok.Data;

@Data
public class Edge<T> {
    private T src;
    private T dest;
    private double weight;

    public Edge(final T src, final T dest, final double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}
