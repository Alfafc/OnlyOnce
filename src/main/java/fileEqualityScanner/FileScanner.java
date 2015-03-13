package fileEqualityScanner;

import fileEqualityScanner.comparer.FileComparisonCriteria;
import utils.Condition;
import utils.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class FileScanner {
    private static final int MAX_THREADS = 1;
    private static final int SCANNED_FILES_PRE_THREAD = 1;
    private final List<Condition<ScannedFile>> skipConditions = new ArrayList<Condition<ScannedFile>>();
    private final List<FileComparer> fileComparers = new ArrayList<FileComparer>();

    public <T> void addFileComparerCriteria(final FileComparisonCriteria<T> fileComparisonCriteria) {

        fileComparers.add(new FileComparer<T>(fileComparisonCriteria));
    }

    public void skipFile(final Condition<ScannedFile> skipCondition) {

        skipConditions.add(skipCondition);
    }

    public ArrayList<ArrayList<ScannedFile>> getFileGroups(final boolean onlyWithRepeatedFiles) {

        final ArrayList<ArrayList<ScannedFile>> fileGroups = new ArrayList<ArrayList<ScannedFile>>(fileComparers.size() * 30);
        for (final FileComparer<?> fileComparer : fileComparers) {
            fileGroups.addAll(fileComparer.getFileGroups(onlyWithRepeatedFiles));
        }
        return fileGroups;
    }

    public void scanPath(final String rootDirectoryPath) {

        scanPath(rootDirectoryPath, true);
    }

    public void scanPath(final String rootDirectoryPath, final boolean recursively) {

        final ScanWorker scanWorker = new ScanWorker(MAX_THREADS);

        scanWorker.work(rootDirectoryPath, recursively);

        scanWorker.waitUntilFinish();

        System.err.println();
        System.err.println("TERMINO SCAN");
        System.err.println();
    }

    private void scanPathImpl(final String rootDirectoryPath, final boolean recursively, final ScanWorker scanWorker) {

        final Path directory = Paths.get(rootDirectoryPath);

        DirectoryStream<Path> stream = null;
        try {
            stream = Files.newDirectoryStream(directory);

            for (Path path : stream) {

                final File file = path.toFile();

                // skip directories
                if (file.isDirectory()) {
                    // scan directories recursively
                    if (recursively) {
                        scanWorker.work(path.toAbsolutePath().toString(), recursively);
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
                try {
                    stream.close();
                } catch (final IOException e) {
                    System.err.println("Error closing stream: [" + e.getMessage() + "]");
                }
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

    private class ScanWorker {

        private final ThreadPool threadsPool;

        public ScanWorker(final int workerSize) {

            this.threadsPool = new ThreadPool(workerSize);
        }

        public void work(final String filePath, final boolean recursively) {

            System.err.println("Process " + filePath);
            threadsPool.processInPool(new Runnable() {
                @Override
                public void run() {
                    scanPathImpl(filePath, recursively, ScanWorker.this);
                }
            });
        }

        public void waitUntilFinish() {

            threadsPool.waitUntilFinish();
        }
    }
}
