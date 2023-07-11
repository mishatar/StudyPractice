package ru.etu.graph;

/**
 * Exception for invalid edge. "Invalid" means that it does not belong to needed graph or any other problem.
 */
public class InvalidEdgeException extends RuntimeException {

    public InvalidEdgeException() {
        super("The edge is invalid or does not belong to the graph.");
    }

    public InvalidEdgeException(String message) {
        super(message);
    }
}
