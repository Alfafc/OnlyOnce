package com.alfascompany.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.function.Supplier;

public class Pool<T> {

    private static Logger logger = LoggerFactory.getLogger(Pool.class);
    protected final ArrayList<T> items = new ArrayList<T>();
    protected Supplier<T> createItemDelegate;
    private int size;

    public Pool(final Supplier<T> createItemDelegate, final int size) {

        this.createItemDelegate = createItemDelegate;
        this.size = size;
    }

    public T borrowItem() {

        synchronized (this) {

            if (size-- >= 0) {
                logger.debug("Borrow a new element");
                return createItemDelegate.get();
            }

            try {
                logger.debug("Nothing to borrow, waiting... " + Thread.currentThread());
                wait();
            } catch (final InterruptedException e) {
                logger.error("Wait method throw an error [" + e.getMessage() + "]", e);
            }

            logger.debug("Borrow one existing element");
            return items.remove(0);
        }
    }

    public void returnItem(final T item) {

        synchronized (this) {
            logger.debug("Item returned " + item + " in thread " + Thread.currentThread());
            items.add(item);

            notify();
        }
    }
}
