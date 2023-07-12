package ru.etu.graphview.drawing.multithreading;

/**
 * Second thread which implements step by step executing with waiting between.
 */
public class ThreadB extends Thread {
    final ResourceLock lock;

    public ThreadB(ResourceLock lock) {
        this.lock = lock;
    }

    @Override
    public void run() {
        try {
            synchronized (lock) {
                while (lock.working) {
                    // Wait for out turn
                    while (lock.flag != 1) {
                        lock.wait();
                    }

                    // Wait some time
                    Thread.sleep(lock.waitTime);
                    if (!lock.working) { // If was ordered to stop while waiting
                        break;
                    }

                    // Run Task
                    lock.tasks.get(lock.currentTask.get()).run();

                    // Determine to get new task and say next thread to work or stop
                    if (lock.currentTask.get() + 1 < lock.tasks.size()) {
                        lock.currentTask.incrementAndGet();
                        lock.flag = 0;
                        lock.notifyAll();
                    } else {
                        lock.currentTask.incrementAndGet();
                        lock.flag = 0;
                        lock.working = false;
                        lock.notifyAll();
                    }
                }
            }
        } catch (Exception ignored) {
            if (lock.currentTask.get() < lock.tasks.size())
                lock.tasks.get(lock.currentTask.get()).interrupt();
        }
    }
}
