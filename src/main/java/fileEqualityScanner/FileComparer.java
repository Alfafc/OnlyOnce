package fileEqualityScanner;

import fileEqualityScanner.comparer.FileComparisonCriteria;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FileComparer<T> {

    private final Map<T, ArrayList<ArrayList<ScannedFile>>> files = new HashMap<T, ArrayList<ArrayList<ScannedFile>>>(500);
    private final FileComparisonCriteria<T> fileComparisonCriteria;

    public FileComparer(final FileComparisonCriteria<T> fileComparisonCriteria) {

        this.fileComparisonCriteria = fileComparisonCriteria;
    }

    public void addFile(final ScannedFile scannedFile) {

        final T comparisonValue = fileComparisonCriteria.getComparisonValue(scannedFile);
        ArrayList<ArrayList<ScannedFile>> fileGroupList = files.get(comparisonValue);

        if (fileGroupList == null) {
            fileGroupList = new ArrayList<ArrayList<ScannedFile>>();
            files.put(comparisonValue, fileGroupList);
        }

        ArrayList<ScannedFile> selectedFileGroup = null;
        for (final ArrayList<ScannedFile> fileGroup : fileGroupList) {

            if (fileComparisonCriteria.compare(fileGroup.get(0), scannedFile)) {
                selectedFileGroup = fileGroup;
                break;
            }
        }
        if (selectedFileGroup == null) {
            selectedFileGroup = new ArrayList<ScannedFile>();
            selectedFileGroup.add(scannedFile);
            fileGroupList.add(selectedFileGroup);
        } else {
            selectedFileGroup.add(scannedFile);
        }
    }

    public ArrayList<ArrayList<ScannedFile>> getFileGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<ArrayList<ScannedFile>> fileGroups = new ArrayList<ArrayList<ScannedFile>>(files.values().size());
        for (final ArrayList<ArrayList<ScannedFile>> fileGroup : files.values()) {

            for (final ArrayList<ScannedFile> scannedFiles : fileGroup) {
                if (!onlyWithRepeatedFiles || scannedFiles.size() > 1) {
                    fileGroups.add(scannedFiles);
                }
            }
        }
        return fileGroups;
    }
}