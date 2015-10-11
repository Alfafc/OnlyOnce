package com.alfascompany.io.scanners.equality;

import com.alfascompany.io.scanners.MultiThreadFileScanner;
import com.alfascompany.io.serialization.FileSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class EqualityFileScanner extends MultiThreadFileScanner {

    private static Logger logger = LoggerFactory.getLogger(EqualityFileScanner.class);
    private final List<FilesGroupedByEqualityCriteria> filesGroupedByEqualityCriteriaList = new ArrayList<>();
    private boolean tryToUseCachedResults;
    private boolean saveResultsForCache;

    public <T> void addEqualityCriteria(final EqualityCriteria<T> equalityCriteria) {
        filesGroupedByEqualityCriteriaList.add(new FilesGroupedByEqualityCriteria<>(equalityCriteria));
    }

    public void tryToUseCachedResults(final boolean tryToUseCachedResults) {
        this.tryToUseCachedResults = tryToUseCachedResults;
    }

    public void saveResultsForCache(final boolean saveResultsForCache) {
        this.saveResultsForCache = saveResultsForCache;
    }

    public List<FilesGroupedByEqualityCriteria> scanRecursively(final String rootPath) {

        return scan(rootPath, true);
    }

    public List<FilesGroupedByEqualityCriteria> scan(final String rootPath, final boolean recursively) {

        final String cacheFilePath = rootPath + "/equalityFileScanner.cache";

        if (tryToUseCachedResults) {
            final List<FilesGroupedByEqualityCriteria> serializedResult = FileSerializer.DeserializeObject(cacheFilePath, false);
            if (serializedResult != null) {
                return serializedResult;
            }
        }

        filesGroupedByEqualityCriteriaList.stream().forEach(f -> f.clearFiles());
        scan(rootPath, file -> {

            if (!file.isDirectory()) {
                final ScannedFile fileItem = new ScannedFile(file);
                for (final FilesGroupedByEqualityCriteria<?> fileCriteriaGroup : filesGroupedByEqualityCriteriaList) {
                    fileCriteriaGroup.addFile(fileItem);
                }
            }

        }, recursively);

        if (saveResultsForCache) {
            FileSerializer.SerializeObject(filesGroupedByEqualityCriteriaList, cacheFilePath);
        }

        return filesGroupedByEqualityCriteriaList;
    }
}
