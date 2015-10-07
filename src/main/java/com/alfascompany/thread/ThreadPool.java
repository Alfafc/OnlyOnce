package com.alfascompany.thread;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ThreadPool {

    private static Logger logger = LoggerFactory.getLogger(ThreadPool.class);
    private final int size;
    private int currentThreadsSize;
    private final Object POOL_WAIT_TO_COMPLETE_OBJECT = new Object();

    public ThreadPool(final int size) {

        this.size = size;
    }

    public void processInPool(final Runnable runnable) {

        synchronized (this) {
            if (isPoolEmpty()) {
                try {
                    logger.debug("Nothing to borrow, waiting... " + Thread.currentThread() + " Balance " + currentThreadsSize);
                    wait();
                } catch (final InterruptedException e) {
                    logger.error("Wait method throw an error [" + e.getMessage() + "]");
                    throw new RuntimeException("Wait method throw an error [" + e.getMessage() + "]", e);
                }
            }
            currentThreadsSize++;
        }

        logger.debug("Borrow a new Thread  (currentThreadsSize " + currentThreadsSize + "/" + size + ")");

        new Thread(() -> {

            runnable.run();

            synchronized (ThreadPool.this) {
                currentThreadsSize--;
                ThreadPool.this.notify();
            }

            synchronized (POOL_WAIT_TO_COMPLETE_OBJECT) {
                if (currentThreadsSize == 0) {
                    POOL_WAIT_TO_COMPLETE_OBJECT.notify();
                }
            }
        }).start();
    }

    public boolean isPoolEmpty() {
        return currentThreadsSize >= size;
    }

    public void waitUntilFinish() {

        synchronized (POOL_WAIT_TO_COMPLETE_OBJECT) {
            if (currentThreadsSize > 0) {
                try {
                    POOL_WAIT_TO_COMPLETE_OBJECT.wait();
                } catch (final InterruptedException e) {
                    logger.error("Wait method throw an error [" + e.getMessage() + "]");
                    throw new RuntimeException("Wait method throw an error [" + e.getMessage() + "]", e);
                }
            }
        }
    }
}
