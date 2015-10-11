package com.alfascompany.io.replacer;

import com.alfascompany.io.scanners.equality.ScannedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiPredicate;

public class FileReplacer {

    private static Logger logger = LoggerFactory.getLogger(FileReplacer.class);

    public static void replace(final ArrayList<ArrayList<ScannedFile>> groupsWithRepeatedItems, final BiPredicate<ScannedFile, ScannedFile> canMove) {

        for (final ArrayList<ScannedFile> groupsWithRepeatedItem : groupsWithRepeatedItems) {

            for (final ScannedFile scannedFile1 : groupsWithRepeatedItem) {

                for (final ScannedFile scannedFile2 : groupsWithRepeatedItem) {

                    if (!scannedFile1.equals(scannedFile2) && canMove.test(scannedFile1, scannedFile2)) {

//                        final File destinationFile = new File(directoryPath + "\\" + scannedFile1.name);
//                        boolean hasBeenRenamedTo = new File(scannedFile1.fullPath).renameTo(destinationFile);
//
//                        final String message = " to move {" + scannedFile1.fullPath + "} to {" + directoryPath + "}";
//                        if (hasBeenRenamedTo) {
//                            logger.info("Succeed" + message);
//                        } else {
//                            logger.error("Fail" + message);
//                        }

                    }
                }
            }
        }
    }
}
