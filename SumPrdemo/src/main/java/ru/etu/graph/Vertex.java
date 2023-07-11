package ru.etu.graph;

import java.util.Objects;

/**
 * Vertex class
 */
public class Vertex {

    private String data;

    public Vertex(String data) {
        this.data = data;
    }

    public void setData(String data) {
        this.data = data;
    }

    public String getData() {
        return data;
    }

    @Override
    public String toString() {
        return "Vertex{" +
                "element='" + data + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Vertex)) return false;
        Vertex vertex = (Vertex) o;
        return getData().equals(vertex.getData());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getData());
    }
}
