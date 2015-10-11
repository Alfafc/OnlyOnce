import com.alfascompany.io.scanners.equality.EqualityFileScanner;
import com.alfascompany.io.scanners.equality.FilesGroupedByEqualityCriteria;
import com.alfascompany.io.scanners.equality.ScannedFile;
import com.alfascompany.io.scanners.equality.criterias.SameSizeAndContentEqualityCriteria;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static String ROOT_DIRECTORY_PATH = "C:\\Users\\fliacos\\Desktop";
    //    private final static String ROOT_DIRECTORY_PATH = "D:\\CloudBackuped\\Fotos&Videos";
    //    private final static String ROOT_DIRECTORY_PATH = "G:\\";
//    private final static String REPEATED_DIRECTORY_PATH = "D:\\Repetido_BORRAR_FER";

    public static void main(final String[] args) throws IOException {

        long start = System.currentTimeMillis();

        final EqualityFileScanner equalityFileScanner = new EqualityFileScanner();
        equalityFileScanner.addEqualityCriteria(new SameSizeAndContentEqualityCriteria());
        equalityFileScanner.setThreadsCount(500);
        equalityFileScanner.tryToUseCachedResults(true);
        equalityFileScanner.saveResultsForCache(true);
        equalityFileScanner.skipFile(file -> ".nomedia".equals(file.getName()));
        equalityFileScanner.skipFile(file -> "desktop.ini".equals(file.getName()));
//        equalityFileScanner.addEqualityCriteria(new SameNameEqualityCriteria());

        final List<FilesGroupedByEqualityCriteria> filesGroupedByEqualityCriteriaList = equalityFileScanner.scanRecursively(ROOT_DIRECTORY_PATH);

        System.err.println("Elapsed scan: " + (System.currentTimeMillis() - start) + " ms");

        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.printEqualityFilesGroups(false));
        filesGroupedByEqualityCriteriaList.stream().forEach(g -> g.printEqualityFilesGroupsGroupByFolder(true));

        start = System.currentTimeMillis();
        System.err.println("Elapsed print: " + (System.currentTimeMillis() - start) + " ms");

//        final ArrayList<ArrayList<ScannedFile>> groupsWithRepeatedItems = new ArrayList<>(100);
//        filesGroupedByEqualityCriteriaList.stream().forEach(f -> groupsWithRepeatedItems.addAll(f.getEqualityFilesGroups(true)));


        start = System.currentTimeMillis();

//        FileReplacer fileReplacer = new FileReplacer();
//        fileReplacer.add((anObject, anotherObject) -> {
//
//            if ("D:\\CloudBackuped\\Fotos&Videos\\2012_2013 Fotos Graciosas".equals(anObject.folder)
//                    && "D:\\CloudBackuped\\Fotos&Videos\\2014_04_07 Corsa".equals(anotherObject.folder)) {
//                return true;
//            }
//            return false;
//        });
//
//        fileReplacer.replace(groupsWithRepeatedItems, REPEATED_DIRECTORY_PATH);
//
//        System.err.println("Elapsed move: " + (System.currentTimeMillis() - start) + " ms");

//        long savedSpace = 0;
//        long fileCount = 0;
//        for (final ArrayList<ScannedFile> groupsWithRepeatedItem : groupsWithRepeatedItems) {
//
//            savedSpace -= groupsWithRepeatedItem.get(0).size;
//            fileCount += groupsWithRepeatedItem.size() - 1;
//            for (final ScannedFile file : groupsWithRepeatedItem) {
//                savedSpace += file.size;
//            }
//        }
//        System.err.println("Save space is [" + (savedSpace / 1024 / 1024) + "] MB in [" + fileCount + "] files");
    }
}
