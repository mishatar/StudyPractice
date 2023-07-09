package ru.etu.graphview;

import ru.etu.graphview.base.FXVertex;

import java.util.Collection;
import java.util.Random;

/**
 * This class needs to randomly place vertices (their views) on the plot
 */
public class RandomStrategy {

    /**
     * Randomly places vertices (their views) on the plot
     *
     * @param width      - width of the plot
     * @param height     - width of the plot
     * @param fxVertices - list of vertices (must implement FXVertex)
     */
    public static void place(double width, double height, Collection<? extends FXVertex> fxVertices) {

        Random random = new Random();

        for (var vertex : fxVertices) {
            double x = random.nextDouble() * width;

            if (x < (vertex.getRadius() + 40)) x = (vertex.getRadius() + 40);
            else if (x > width - (vertex.getRadius() + 40)) x = width - (vertex.getRadius() + 40);

            double y = random.nextDouble() * height;

            if (y < (vertex.getRadius() + 40)) y = (vertex.getRadius() + 40);
            else if (y > height - (vertex.getRadius() + 40)) y = height - (vertex.getRadius() + 40);

            vertex.setPosition(x, y);
        }
    }
}
