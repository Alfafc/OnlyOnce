package com.alfascompany.io.scanners.equality;

import com.alfascompany.io.scanners.equality.criterias.SameNamePrefixEqualityCriteria;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.function.BiPredicate;
import java.util.stream.Collectors;

public class FilesGroupedByEqualityCriteria<T> implements Serializable {

    private static final long serialVersionUID = -795646043306946698L;

    private static final Logger logger = LoggerFactory.getLogger(FilesGroupedByEqualityCriteria.class);
    private final Map<T, ArrayList<TreeSet<ScannedFile>>> files = new HashMap<>(500);
    private final EqualityCriteria<T> equalityCriteria;

    public FilesGroupedByEqualityCriteria(final EqualityCriteria<T> equalityCriteria) {

        this.equalityCriteria = equalityCriteria;
    }

    public void addFile(final ScannedFile scannedFile) {

        final T fastPseudoUniqueKey = equalityCriteria.getFastPseudoUniqueKey(scannedFile);
        ArrayList<TreeSet<ScannedFile>> fileGroupList = files.get(fastPseudoUniqueKey);

        if (fileGroupList == null) {
            fileGroupList = new ArrayList<>();
            files.put(fastPseudoUniqueKey, fileGroupList);
        }

        // there can be more than one group of equals files that have the same pseudo key
        TreeSet<ScannedFile> selectedFileGroup = null;
        for (final TreeSet<ScannedFile> fileGroup : fileGroupList) {

            if (equalityCriteria.test(fileGroup.first(), scannedFile)) {
                selectedFileGroup = fileGroup;
                break;
            }
        }

        if (selectedFileGroup == null) {
            selectedFileGroup = new TreeSet<>();
            fileGroupList.add(selectedFileGroup);
        }

        selectedFileGroup.add(scannedFile);
    }

    public void clearFiles() {
        files.clear();
    }

    public ArrayList<TreeSet<ScannedFile>> getEqualityFilesGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<TreeSet<ScannedFile>> equalityFilesGroupList = new ArrayList<>(files.values().size());
        for (final ArrayList<TreeSet<ScannedFile>> equalityFilesGroups : files.values()) {

            for (final TreeSet<ScannedFile> scannedFiles : equalityFilesGroups) {
                if (!onlyWithRepeatedFiles || scannedFiles.size() > 1) {
                    equalityFilesGroupList.add(scannedFiles);
                }
            }
        }
        equalityFilesGroupList.sort((o1, o2) -> o1.first().fullPath.compareTo(o2.first().fullPath));
        return equalityFilesGroupList;
    }

    public void printEqualityFilesGroupsSize() {

        long totalSize = 0;
        long notDuplicatedSize = 0;
        for (final TreeSet<ScannedFile> scannedFiles : getEqualityFilesGroups(false)) {

            notDuplicatedSize += scannedFiles.first().sizeInBytes;
            for (final ScannedFile scannedFile : scannedFiles) {
                totalSize += scannedFile.sizeInBytes;
            }
        }

        final long duplicatedSize = totalSize - notDuplicatedSize;
        logger.info("Total size scanned {" + ((double) totalSize / 1024d / 1024d) + "} mb and total duplicated files size {" + ((double) duplicatedSize / 1024d / 1024d) + "} mb");
    }

    public void printEqualityFilesGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<TreeSet<ScannedFile>> equalityFilesGroups = getEqualityFilesGroups(onlyWithRepeatedFiles);
        equalityFilesGroups.stream().forEach(e -> logger.info("Repeated files: " + e.stream().map(a -> a.fullPath + " " + a.getSizeInMegaBytes()).collect(Collectors.toList())));
        logger.info("Total groups: " + equalityFilesGroups.size());
    }

    public void printEqualityFilesGroupsGroupByFolder(final boolean onlyWithRepeatedFiles) {

        int quantity = 0;
        final SortedSet<String> folders = new TreeSet<>();
        for (final TreeSet<ScannedFile> equalityFilesGroup : getEqualityFilesGroups(onlyWithRepeatedFiles)) {

            if (folders.size() > 0 && !folders.contains(equalityFilesGroup.first().folder)) {
                quantity++;
                logger.info("Folder: " + folders.first() + " {" + folders + " } ");
                folders.clear();
            }
            for (final ScannedFile scannedFile : equalityFilesGroup) {
                folders.add(scannedFile.folder);
            }
        }
        logger.info("Total groups: " + quantity);
    }

    public void printEqualityFilesGroupsGroupThatMatchRule(final BiPredicate<ScannedFile, ScannedFile> rule) {

        for (final TreeSet<ScannedFile> scannedFiles : getEqualityFilesGroups(false)) {

            boolean allEqual = true;
            for (final ScannedFile scannedFile1 : scannedFiles) {
                for (final ScannedFile scannedFile2 : new ArrayList<>(scannedFiles)) {

                    if (!rule.test(scannedFile1, scannedFile2)) {
                        allEqual = false;
                        break;
                    }
                }
                if (!allEqual) break;
            }
            if (!allEqual) {
                logger.debug("Not conforming to rule: " + scannedFiles);
            }
        }
    }

    public void interactivelyRemoveDuplicatedFiles(final boolean repeatLastActionForFolder) {

        final StringBuilder currentFoldersStringBuilder = new StringBuilder(100);
        String lastFolders = null;
        int lastAction = 0;

        final Scanner scanner = new Scanner(System.in);
        for (final TreeSet<ScannedFile> equalityFilesGroup : getEqualityFilesGroups(true)) {

            currentFoldersStringBuilder.setLength(0);
            for (final ScannedFile scannedFile : equalityFilesGroup) {
                currentFoldersStringBuilder.append(scannedFile.folder);
            }
            final String currentFolders = currentFoldersStringBuilder.toString();

            // select an option (if same folder and can repeart last action take the last one)
            int option;
            if (repeatLastActionForFolder && lastFolders != null && lastFolders.equals(currentFolders)) {
                option = lastAction;
            } else {

                // let the user select an option
                logger.info("Enter an option");
                logger.info("0) to do nothing ");
                int i = 1;
                for (final ScannedFile scannedFile : equalityFilesGroup) {
                    logger.info((i++) + ") to mantain " + scannedFile.fullPath);
                }

                option = scanner.nextInt();
                while (option < 0 || option > equalityFilesGroup.size()) {
                    logger.info("Invalid option " + option);
                    option = scanner.nextInt();
                }
                lastAction = option;
                lastFolders = currentFolders;
            }

            // execute the option if any
            if (option > 0) {
                final Iterator<ScannedFile> iterator = equalityFilesGroup.iterator();
                int j = 1;
                while (iterator.hasNext()) {
                    final ScannedFile next = iterator.next(); //consume always
                    if (j++ != option) {
                        removeFile(next);
                    }
                }
            }
        }

    }


    public void removeDuplicatedInSpecificFolders(final String path, final String duplicatedPath) {

        removeDuplicated(
                new BiPredicate<ScannedFile, TreeSet<ScannedFile>>() {
                    @Override
                    public boolean test(final ScannedFile scannedFile, final TreeSet<ScannedFile> scannedFiles) {

                        return scannedFile.folder.equals(duplicatedPath) &&
                                scannedFiles.stream().anyMatch(f ->
                                        !f.fullPath.equals(scannedFile.fullPath) &&
                                                f.folder.equals(path));
                    }
                });
    }

    public void removeDuplicatedInSameFolder() {

        removeDuplicated(
                new BiPredicate<ScannedFile, TreeSet<ScannedFile>>() {
                    @Override
                    public boolean test(final ScannedFile scannedFile, final TreeSet<ScannedFile> scannedFiles) {

                        final List<ScannedFile> sameFolderFiles = scannedFiles.stream().filter(s -> s.folder.equals(scannedFile.folder)).collect(Collectors.toList());

                        return sameFolderFiles.size() > 1 && sameFolderFiles.contains(scannedFile) &&
                                !scannedFile.fullPath.equals(sameFolderFiles.get(0).fullPath);
                    }
                });
    }

    public void removeDuplicated(final BiPredicate<ScannedFile, TreeSet<ScannedFile>> rule) {

        for (final TreeSet<ScannedFile> scannedFiles : getEqualityFilesGroups(true)) {

            for (final ScannedFile scannedFile : scannedFiles) {

                if (rule.test(scannedFile, scannedFiles)) {
                    logger.error("Delete " + scannedFile.fullPath + " " + scannedFile.sizeInBytes + " --> " + scannedFiles.stream().map(s -> s.fullPath).collect(Collectors.toList()));
                    removeFile(scannedFile);
                }
            }
        }
    }

    private void removeFile(final ScannedFile scannedFile) {
        try {
            logger.error("Delete " + scannedFile.fullPath + " " + scannedFile.sizeInBytes);
            Files.delete(Paths.get(scannedFile.fullPath));
        } catch (final Exception e) {
            logger.error("Error deleting file {" + scannedFile.fullPath + "} " + e.getMessage(), e);
        }
    }
}