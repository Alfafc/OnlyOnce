package com.alfascompany.io.equalityScanners.criterias;

import com.alfascompany.io.equalityScanners.EqualityCriteria;
import com.alfascompany.io.equalityScanners.ScannedFile;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;

public class SameSizeAndContentEqualityCriteria implements EqualityCriteria<Long> {

    @Override
    public boolean test(final ScannedFile fileItem1, final ScannedFile fileItem2) {

        if (fileItem1.size != fileItem2.size) {
            return false;
        }

        final File file1 = new File(fileItem1.fullPath);
        final File file2 = new File(fileItem2.fullPath);

        try {
            return FileUtils.contentEquals(file1, file2);
        } catch (final IOException e) {
            throw new RuntimeException(e);
        }
    }

    @Override
    public Long getFastPseudoUniqueKey(final ScannedFile fileItem) {
        return fileItem.size;
    }
}
