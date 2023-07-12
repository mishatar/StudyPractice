package ru.etu.graphview.drawing.multithreading;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Some resources for threads
 */
public class ResourceLock {
    /**
     * Determines whose turn now to execute
     */
    public volatile int flag = 0;

    /**
     * Current task number
     */
    public volatile AtomicInteger currentTask = new AtomicInteger(0);

    /**
     * List of task to run
     */
    public final List<MyRunnable> tasks;

    /**
     * Controls if threads show be working
     */
    public volatile boolean working = true;

    /**
     * Determines wait time BEFORE executing task
     */
    public final int waitTime;

    public ResourceLock(List<MyRunnable> tasks, int waitTime) {
        this.tasks = tasks;
        this.waitTime = waitTime;
    }
}
