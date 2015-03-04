package fileEqualityScanner.comparer;

import fileEqualityScanner.ScannedFile;
import utils.Comparison;

public interface FileComparisonCriteria<T> extends Comparison<ScannedFile> {

    T getComparisonValue(final ScannedFile scannedFile);
}
