package fileEqualityScanner;

import com.alfascompany.io.equalityScanners.ScannedFile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileReplacer {

    private static Logger logger = LoggerFactory.getLogger(FileReplacer.class);
    private final List<FileComparer> allowedToMove = new ArrayList<>(10);

    public void add(final FileComparer fileComparer) {

        allowedToMove.add(fileComparer);
    }

    public void replace(final ArrayList<ArrayList<ScannedFile>> groupsWithRepeatedItems, final String directoryPath) {

        for (final ArrayList<ScannedFile> groupsWithRepeatedItem : groupsWithRepeatedItems) {

            for (final FileComparer fileComparisonIsAllowedToMove : allowedToMove) {

                for (final ScannedFile scannedFile1 : groupsWithRepeatedItem) {

                    for (final ScannedFile scannedFile2 : groupsWithRepeatedItem) {

                        if (scannedFile1.equals(scannedFile2)) {
                            continue;
                        }

                        if (fileComparisonIsAllowedToMove.compare(scannedFile1, scannedFile2)) {

                            final File destinationFile = new File(directoryPath + "\\" + scannedFile1.name);
                            boolean hasBeenRenamedTo = new File(scannedFile1.fullPath).renameTo(destinationFile);

                            final String message = " to move {" + scannedFile1.fullPath + "} to {" + directoryPath + "}";
                            if (hasBeenRenamedTo) {
                                logger.info("Succeed" + message);
                            } else {
                                logger.error("Fail" + message);
                            }

                        }
                    }
                }
            }
        }
    }
}
