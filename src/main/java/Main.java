import com.alfascompany.io.scanners.equality.EqualityFileScanner;
import com.alfascompany.io.scanners.equality.FilesGroupedByEqualityCriteria;
import com.alfascompany.io.scanners.equality.ScannedFile;
import com.alfascompany.io.scanners.equality.criterias.SameNamePrefixEqualityCriteria;
import com.alfascompany.io.scanners.equality.criterias.SameSizeAndContentEqualityCriteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.function.BiPredicate;

public class Main {

    //    private final static String ROOT_DIRECTORY_PATH = "G:\\";
//    private final static String ROOT_DIRECTORY_PATH = "G:\\RecuperadoJPG";
//    private final static String ROOT_DIRECTORY_PATH = "G:\\RecuperadoMP3";
//    private final static String ROOT_DIRECTORY_PATH = "G:\\Salvado";
//    private final static String ROOT_DIRECTORY_PATH = "D:\\CloudBackuped\\Fotos&Videos";
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
//        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.printEqualityFilesGroups(true));
        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.printEqualityFilesGroupsGroupByFolder(true));
//        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.removeDuplicated(
//                new BiPredicate<ScannedFile, TreeSet<ScannedFile>>() {
//                    @Override
//                    public boolean test(final ScannedFile scannedFile, final TreeSet<ScannedFile> scannedFiles) {
//
//                        if (!scannedFiles.stream().allMatch(f -> f.folder.equals(scannedFile.folder))) {
//                            return false;
//                        }
//
//                        if (scannedFiles.first().fullPath.equals(scannedFile.fullPath)) {
//                            System.err.println("Skip " + scannedFile.fullPath);
//                            return false;
//                        }
//                        System.err.println("Delete " + scannedFile.fullPath);
//                        return true;
//                    }
//                }));

        start = System.currentTimeMillis();
        System.err.println("Elapsed print: " + (System.currentTimeMillis() - start) + " ms");
    }
}
