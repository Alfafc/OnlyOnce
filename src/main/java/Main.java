import com.alfascompany.io.scanners.equality.EqualityFileScanner;
import com.alfascompany.io.scanners.equality.FilesGroupedByEqualityCriteria;
import com.alfascompany.io.scanners.equality.criterias.SameSizeAndContentEqualityCriteria;

import java.io.IOException;
import java.util.List;

public class Main {

    private final static String ROOT_DIRECTORY_PATH = "D:\\CloudBackuped";

    public static void main(final String[] args) throws IOException {

        long start = System.currentTimeMillis();

        final EqualityFileScanner equalityFileScanner = new EqualityFileScanner();
        equalityFileScanner.addEqualityCriteria(new SameSizeAndContentEqualityCriteria());
        equalityFileScanner.setThreadsCount(4);
        equalityFileScanner.tryToUseCachedResults(true);
        equalityFileScanner.saveResultsForCache(true);
        equalityFileScanner.skipFile(file -> ".nomedia".equals(file.getName()));
        equalityFileScanner.skipFile(file -> "desktop.ini".equals(file.getName()));

        final List<FilesGroupedByEqualityCriteria> filesGroupedByEqualityCriteriaList = equalityFileScanner.scanRecursively(ROOT_DIRECTORY_PATH);

        System.err.println("Elapsed scan: " + (System.currentTimeMillis() - start) + " ms");

        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.printEqualityFilesGroupsSize());
        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.interactivelyRemoveDuplicatedFiles(true));
    }
}
