package utils;

import java.util.ArrayList;

public class Pool<T> {

    protected final ArrayList<T> items = new ArrayList<T>();
    protected Func<T> createItemDelegate;
    private int size;

    public Pool(final Func<T> createItemDelegate, final int size) {

        this.createItemDelegate = createItemDelegate;
        this.size = size;
    }

    public T borrowItem() {

        synchronized (this) {

            if (size-- >= 0) {
                System.err.println("Borrow a new element");
                return createItemDelegate.invoke();
            }

            try {
                System.err.println("Nothing to borrow, waiting... " + Thread.currentThread());
                wait();
            } catch (final InterruptedException e) {
                System.err.println("Wait method throw an error [" + e.getMessage() + "]");
            }

            System.err.println("Borrow one existing element");
            return items.remove(0);
        }
    }

    public void returnItem(final T item) {

        synchronized (this) {
            System.err.println("Item returned " + item + " in thread " + Thread.currentThread());
            items.add(item);

            notify();
        }
    }
}
