package utils;

public interface Transformer<T, TOut> {

    TOut transform(T item);
}