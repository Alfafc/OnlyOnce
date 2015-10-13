package com.alfascompany.io.scanners.equality.criterias;

import com.alfascompany.io.scanners.equality.EqualityCriteria;
import com.alfascompany.io.scanners.equality.ScannedFile;

public class SameNamePrefixEqualityCriteria implements EqualityCriteria<String> {
    private static final long serialVersionUID = -5656553754289004113L;

    @Override
    public boolean test(final ScannedFile fileItem1, final ScannedFile fileItem2) {

        return areEquals(fileItem1.name, fileItem2.name) &&
                fileItem1.sizeInBytes == fileItem2.sizeInBytes;
    }

    @Override
    public String getFastPseudoUniqueKey(final ScannedFile fileItem) {

        // remove from slash
        final String fileName = fileItem.name;
        final int slashIndex = fileName.indexOf("_");
        final int pointIndex = fileName.lastIndexOf(".");

        if (slashIndex == -1 && pointIndex == -1) {
            return fileName;
        }
        if (slashIndex == -1) {
            return fileName.substring(0, pointIndex) + "." + fileName.substring(pointIndex + 1, fileName.length());
        }
        if (pointIndex == -1) {
            return fileName.substring(0, slashIndex);
        }
        return fileName.substring(0, slashIndex) + "." + fileName.substring(pointIndex + 1, fileName.length());
    }

    public static boolean areEquals(final String filePath1, final String filePath2) {

        return filePath1.equals(filePath2) ||
                filePath1.equals(removeSufix(filePath2)) ||
                removeSufix(filePath1).equals(filePath2) ||
                removeSufix(filePath1).equals(removeSufix(filePath2));
    }

    private static String removeSufix(final String filePath) {

        final int lastSlashIndex = filePath.lastIndexOf("_");
        final int pointIndex = filePath.lastIndexOf(".");

        if (lastSlashIndex == -1) {
            return filePath;
        }
        if (pointIndex == -1) {
            return filePath.substring(0, lastSlashIndex);
        }
        return filePath.substring(0, lastSlashIndex) + "." + filePath.substring(pointIndex + 1, filePath.length());
    }
}
