package com.alfascompany.io.scanners.equality;

import java.io.Serializable;
import java.util.function.BiPredicate;

public interface EqualityCriteria<T> extends BiPredicate<ScannedFile, ScannedFile>, Serializable {

    // this is a key that does not need to be really unique, but it is used to order files in a map
    T getFastPseudoUniqueKey(final ScannedFile scannedFile);
}
