package com.alfascompany.io.equalityScanners;

import java.util.function.BiPredicate;

public interface EqualityCriteria<T> extends BiPredicate<ScannedFile, ScannedFile> {

    // this is a key that does not need to be really unique, but it is used to order files in a map
    T getFastPseudoUniqueKey(final ScannedFile scannedFile);
}
