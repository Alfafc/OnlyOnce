package fileEqualityScanner;

import utils.Comparison;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileReplacer {

    private final List<Comparison<ScannedFile>> allowedToMove = new ArrayList<Comparison<ScannedFile>>();

    public void add(final Comparison<ScannedFile> comparison) {

        allowedToMove.add(comparison);
    }

    public void replace(final ArrayList<ArrayList<ScannedFile>> groupsWithRepeatedItems, final String directoryPath) {

        for (final ArrayList<ScannedFile> groupsWithRepeatedItem : groupsWithRepeatedItems) {
            for (final Comparison<ScannedFile> fileComparison : allowedToMove) {
                for (final ScannedFile scannedFile1 : groupsWithRepeatedItem) {

                    for (final ScannedFile scannedFile2 : groupsWithRepeatedItem) {

                        if (scannedFile1.equals(scannedFile2)) {
                            continue;
                        }

                        if (fileComparison.compare(scannedFile1, scannedFile2)) {

                            final File destinationFile = new File(directoryPath + "\\" + scannedFile1.name);
                            boolean renameTo = new File(scannedFile1.fullPath).renameTo(destinationFile);
                            System.err.println(((renameTo) ? "Succesfully" : "Failed") + " move " + scannedFile1.fullPath + " to " + directoryPath);
                        }
                    }
                }
            }
        }
    }
}
