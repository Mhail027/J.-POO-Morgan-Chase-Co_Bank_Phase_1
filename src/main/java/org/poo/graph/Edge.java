package org.poo.graph;

import lombok.Data;
import lombok.NonNull;

@Data
public class Edge<T> {
    private T src;
    private T dest;
    private double weight;

    public Edge(@NonNull final T src, @NonNull final T dest, final double weight) {
        this.src = src;
        this.dest = dest;
        this.weight = weight;
    }
}
