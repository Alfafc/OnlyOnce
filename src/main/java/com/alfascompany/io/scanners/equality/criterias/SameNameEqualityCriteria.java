package com.alfascompany.io.scanners.equality.criterias;

import com.alfascompany.io.scanners.equality.EqualityCriteria;
import com.alfascompany.io.scanners.equality.ScannedFile;

public class SameNameEqualityCriteria implements EqualityCriteria<String> {

    private static final long serialVersionUID = 5659770514186302519L;

    @Override
    public boolean test(final ScannedFile fileItem1, final ScannedFile fileItem2) {

        return fileItem1.name.equals(fileItem2.name);
    }

    @Override
    public String getFastPseudoUniqueKey(final ScannedFile fileItem) {
        return fileItem.name;
    }
}
