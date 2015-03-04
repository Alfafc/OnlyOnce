package fileEqualityScanner;

import fileEqualityScanner.comparer.FileComparisonCriteria;
import utils.Condition;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    private final List<Condition<ScannedFile>> skipConditions = new ArrayList<Condition<ScannedFile>>();
    private final List<FileComparer> fileComparers = new ArrayList<FileComparer>();

    public <T> void addFileComparerCriteria(final FileComparisonCriteria<T> fileComparisonCriteria) {

        fileComparers.add(new FileComparer<T>(fileComparisonCriteria));
    }

    public void skipFile(final Condition<ScannedFile> skipCondition) {

        skipConditions.add(skipCondition);
    }

    public List<ArrayList<ScannedFile>> getFileGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<ArrayList<ScannedFile>> fileGroups = new ArrayList<ArrayList<ScannedFile>>(fileComparers.size() * 30);
        for (final FileComparer<?> fileComparer : fileComparers) {

            if(!onlyWithRepeatedFiles || fileGroups.size() > 1)

            fileGroups.addAll(fileComparer.getFileGroups());
        }
        return fileGroups;
    }

    public void scanPath(final String rootDirectoryPath) throws IOException {

        scanPath(rootDirectoryPath, true);
    }

    public void scanPath(final String rootDirectoryPath, final boolean recursively) throws IOException {

        final Path dir = FileSystems.getDefault().getPath(rootDirectoryPath);
        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(dir);

            for (Path path : stream) {

                final File file = path.toFile();

                // skip directories
                if (file.isDirectory()) {
                    // scan directories recursively
                    if (recursively) {
                        scanPath(path.toAbsolutePath().toString(), true);
                    }
                    continue;
                }

                final ScannedFile fileItem = new ScannedFile(file);

                if (hasToSkipFile(fileItem)) {
                    continue;
                }

                for (final FileComparer<?> fileComparer : fileComparers) {
                    fileComparer.addFile(fileItem);
                }
            }
        } catch (final Exception e) {
            System.err.println("Error scanning [" + rootDirectoryPath + "]: [" + e.getMessage() + "]");
        } finally {
            if (stream != null) {
                stream.close();
            }
        }
    }

    private boolean hasToSkipFile(final ScannedFile fileItem) {

        for (final Condition<ScannedFile> skipCondition : skipConditions) {
            if (skipCondition.applyTo(fileItem)) {
                return true;
            }
        }
        return false;
    }
}
