package utils;

public interface Condition<T> {

    boolean applyTo(final T item);
}
