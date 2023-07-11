package ru.etu.graph;

/**
 * Exception for invalid vertex. "Invalid" means that it does not belong to needed graph or any other problem.
 */
public class InvalidVertexException extends RuntimeException {

    public InvalidVertexException() {
        super("The vertex is invalid or does not belong to the graph.");
    }

    public InvalidVertexException(String message) {
        super(message);
    }

}
