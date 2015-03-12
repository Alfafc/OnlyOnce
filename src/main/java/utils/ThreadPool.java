package utils;

import org.omg.PortableServer.SERVANT_RETENTION_POLICY_ID;

public class ThreadPool {

    private final int size;
    private int currentThreadsSize;
    private final Object POOL_WAIT_TO_COMPLETE_OBJECT = new Object();

    public ThreadPool(final int size) {

        this.size = size;
    }

    public void processInPool(final Runnable runnable) {

        synchronized (this) {
            if (currentThreadsSize == size) {
                try {
                    System.err.println("Nothing to borrow, waiting... " + Thread.currentThread() + " Balance " + currentThreadsSize);
                    wait();
                    System.err.println("No more waiting... " + Thread.currentThread() + " Balance " + currentThreadsSize);
                } catch (final InterruptedException e) {
                    System.err.println("Wait method throw an error [" + e.getMessage() + "]");
                }
            }
            currentThreadsSize++;
        }

        System.err.println("Borrow a new Thread  (currentThreadsSize " + currentThreadsSize + "/" + size + ")");
        new Thread(new Runnable() {

            @Override
            public void run() {

                runnable.run();

                System.err.println("Termino " + Thread.currentThread());
                synchronized (ThreadPool.this) {
                    currentThreadsSize--;
                    ThreadPool.this.notify();
                    System.err.println("Notify " + Thread.currentThread() + " Balance " + currentThreadsSize);
                }

                synchronized (POOL_WAIT_TO_COMPLETE_OBJECT) {
                    if (currentThreadsSize == 0) {
                        POOL_WAIT_TO_COMPLETE_OBJECT.notify();
                    }
                }
            }

        }).start();
    }

    public void waitUntilFinish() {

        synchronized (POOL_WAIT_TO_COMPLETE_OBJECT) {
            try {
                POOL_WAIT_TO_COMPLETE_OBJECT.wait();
            } catch (InterruptedException e) {
                System.err.println("Wait method throw an error [" + e.getMessage() + "]");
            }
        }
    }
}
