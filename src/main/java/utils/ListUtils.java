package utils;

import java.util.ArrayList;
import java.util.List;

public class ListUtils {

    public static <T, TOut> List<TOut> transform(final List<T> list, final Transformer<T, TOut> transformer) {

        final List<TOut> newList = new ArrayList<TOut>(list.size());
        for (final T item : list) {
            newList.add(transformer.transform(item));
        }
        return newList;
    }

    public static <T> List<T> filter(final List<T> list, final Condition<T> condition) {

        final List<T> newList = new ArrayList<T>(list.size());
        for (final T item : list) {
            if (condition.applyTo(item)) {
                newList.add(item);
            }
        }
        return newList;
    }
}
