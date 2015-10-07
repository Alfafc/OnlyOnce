package com.alfascompany.io.equalityScanners.criterias;

import com.alfascompany.io.equalityScanners.EqualityCriteria;
import com.alfascompany.io.equalityScanners.ScannedFile;

public class SameNameEqualityCriteria implements EqualityCriteria<String> {

    @Override
    public boolean test(final ScannedFile fileItem1, final ScannedFile fileItem2) {

        return fileItem1.name.equals(fileItem2.name);
    }

    @Override
    public String getFastPseudoUniqueKey(final ScannedFile fileItem) {
        return fileItem.name;
    }
}
