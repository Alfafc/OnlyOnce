package com.alfascompany.io.equalityScanners;

import com.alfascompany.io.scanners.MultiThreadFileScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EqualityFileScanner extends MultiThreadFileScanner {

    private static Logger logger = LoggerFactory.getLogger(EqualityFileScanner.class);
    private final List<FilesGroupedByEqualityCriteria> filesGroupedByEqualityCriteriaList = new ArrayList<>();

    public <T> void addEqualityCriteria(final EqualityCriteria<T> equalityCriteria) {

        filesGroupedByEqualityCriteriaList.add(new FilesGroupedByEqualityCriteria<>(equalityCriteria));
    }

    public void scanRecursively(final String rootPath) {

        scan(rootPath, true);
    }

    public void scan(final String rootPath, final boolean recursively) {

        scan(rootPath, file -> {
            final ScannedFile fileItem = new ScannedFile(file);

            for (final FilesGroupedByEqualityCriteria<?> fileCriteriaGroup : filesGroupedByEqualityCriteriaList) {
                fileCriteriaGroup.addFile(fileItem);
            }

        }, recursively);
    }

    public ArrayList<ArrayList<ScannedFile>> getEqualityFilesGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<ArrayList<ScannedFile>> fileGroups = new ArrayList<>(filesGroupedByEqualityCriteriaList.size() * 30);
        for (final FilesGroupedByEqualityCriteria<?> filesGroupedByEqualityCriteria : filesGroupedByEqualityCriteriaList) {
            fileGroups.addAll(filesGroupedByEqualityCriteria.getEqualityFilesGroups(onlyWithRepeatedFiles));
        }
        return fileGroups;
    }
}
