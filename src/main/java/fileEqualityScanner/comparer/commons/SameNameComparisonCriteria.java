package fileEqualityScanner.comparer.commons;

import fileEqualityScanner.ScannedFile;
import fileEqualityScanner.comparer.FileComparisonCriteria;

public class SameNameComparisonCriteria implements FileComparisonCriteria<String> {

    @Override
    public boolean compare(final ScannedFile fileItem1, final ScannedFile fileItem2) {

        return fileItem1.name.equals(fileItem2.name);
    }

    @Override
    public String getComparisonValue(final ScannedFile fileItem) {
        return fileItem.name;
    }
}
