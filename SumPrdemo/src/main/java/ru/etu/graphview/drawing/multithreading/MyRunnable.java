package ru.etu.graphview.drawing.multithreading;

/**
 * A little extension for runnable interface, so it was possible to interrupt smth
 */
public interface MyRunnable extends Runnable {

    void interrupt();
}
