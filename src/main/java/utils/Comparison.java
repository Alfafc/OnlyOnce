package utils;

import fileEqualityScanner.ScannedFile;

public interface Comparison<T> {

    boolean compare(final T anObject, final T anotherObject);
}
