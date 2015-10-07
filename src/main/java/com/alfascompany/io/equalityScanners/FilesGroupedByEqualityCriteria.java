package com.alfascompany.io.equalityScanners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FilesGroupedByEqualityCriteria<T> {

    private final Map<T, ArrayList<ArrayList<ScannedFile>>> files = new HashMap<>(500);
    private final EqualityCriteria<T> equalityCriteria;

    public FilesGroupedByEqualityCriteria(final EqualityCriteria<T> equalityCriteria) {

        this.equalityCriteria = equalityCriteria;
    }

    public void addFile(final ScannedFile scannedFile) {

        final T comparisonKey = equalityCriteria.getFastPseudoUniqueKey(scannedFile);
        ArrayList<ArrayList<ScannedFile>> fileGroupList = files.get(comparisonKey);

        if (fileGroupList == null) {
            fileGroupList = new ArrayList<>();
            files.put(comparisonKey, fileGroupList);
        }

        ArrayList<ScannedFile> selectedFileGroup = null;
        for (final ArrayList<ScannedFile> fileGroup : fileGroupList) {

            if (equalityCriteria.test(fileGroup.get(0), scannedFile)) {
                selectedFileGroup = fileGroup;
                break;
            }
        }
        if (selectedFileGroup == null) {
            selectedFileGroup = new ArrayList<>();
            selectedFileGroup.add(scannedFile);
            fileGroupList.add(selectedFileGroup);
        } else {
            selectedFileGroup.add(scannedFile);
        }
    }

    public ArrayList<ArrayList<ScannedFile>> getEqualityFilesGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<ArrayList<ScannedFile>> equalityFilesGroups = new ArrayList<>(files.values().size());
        for (final ArrayList<ArrayList<ScannedFile>> equalityFilesGroup : files.values()) {

            for (final ArrayList<ScannedFile> scannedFiles : equalityFilesGroup) {
                if (!onlyWithRepeatedFiles || scannedFiles.size() > 1) {
                    equalityFilesGroups.add(scannedFiles);
                }
            }
        }
        return equalityFilesGroups;
    }
}