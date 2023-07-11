package ru.etu.graphview.drawing;

public enum ViewMode {
    NORMAL, // Only vertex drag
    VERTEX_PLACEMENT, // Vertex drag + vertex creation
    EDGE_PLACEMENT, // Edge creation
    VERTEX_CHOOSE // Choosing 2 vertices for algorithm
}