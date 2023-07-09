package ru.etu.graphview.base;

import javafx.beans.binding.Bindings;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.value.ObservableDoubleValue;
import javafx.geometry.Point2D;

/**
 * A bunch of mathematical methods used in base classes
 */
public class Utils {

    /**
     * Transforms radians to the degrees. Change its value alongside with angleRad.
     *
     * @param angleRad angle in radians
     * @return transformed angle in degrees
     */
    public static DoubleBinding toDegrees(final ObservableDoubleValue angleRad) {
        return Bindings.createDoubleBinding(() -> Math.toDegrees(angleRad.get()), angleRad);
    }

    /**
     * Gets angle from x-axis to vector from (0,0) to (x, y) in 2D.
     *
     * @param y y coordinate
     * @param x x coordinate
     * @return angle from x-axis to vector from (0,0) to (x, y) in 2D.
     */
    public static DoubleBinding atan2(final ObservableDoubleValue y, final ObservableDoubleValue x) {
        return Bindings.createDoubleBinding(() -> Math.atan2(y.get(), x.get()), y, x);
    }

    /**
     * Rotation of point by some pivot. Based on formula for vector (from (0,0)) rotation <br>
     * x_2 = cos(angle) * x_1 - sin(angle) * y_1 <br>
     * y_2 = sin(angle) * x_1 + cos(angle) * y_1
     *
     * @param point point which to rotate
     * @param pivot point of rotation
     * @param angle angle of rotation
     * @return coordinates for rotated point
     */
    public static Point2D rotate(final Point2D point, final Point2D pivot, double angle) {
        var tempAngle = Math.toRadians(angle);

        double sin = Math.sin(tempAngle);
        double cos = Math.cos(tempAngle);

        // Making vector that starts at (0,0)
        Point2D result = point.subtract(pivot);

        // Rotating it
        Point2D rotatedOrigin = new Point2D(
                result.getX() * cos - result.getY() * sin,
                result.getX() * sin + result.getY() * cos);

        // Making it back
        result = rotatedOrigin.add(pivot);

        return result;
    }
}
