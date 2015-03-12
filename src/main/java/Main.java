import fileEqualityScanner.FileReplacer;
import fileEqualityScanner.FileScanner;
import fileEqualityScanner.ScannedFile;
import fileEqualityScanner.comparer.commons.SameNameComparisonCriteria;
import fileEqualityScanner.comparer.commons.SameSizeAndContentComparisonCriteria;
import utils.Comparison;
import utils.Condition;
import utils.serialization.FileSerializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    private final static String ROOT_DIRECTORY_PATH = "D:\\CloudBackuped\\Fotos&Videos";
    //    private final static String ROOT_DIRECTORY_PATH = "G:\\";
//    private final static String ROOT_DIRECTORY_PATH = "C:\\Users\\fliacos\\Desktop\\repe";
    private final static String REPEATED_DIRECTORY_PATH = "D:\\Repetido_BORRAR_FER";
    private final static String SERIALIZED_RESULT_FILE_PATH = ROOT_DIRECTORY_PATH + "\\serialized.info";

    public static void main(final String[] args) throws IOException {

        long start = System.currentTimeMillis();
        ArrayList<ArrayList<ScannedFile>> groupsWithRepeatedItems = FileSerializer.DeserializeObject(SERIALIZED_RESULT_FILE_PATH);
        groupsWithRepeatedItems = null;
        if (groupsWithRepeatedItems == null) {

//            System.err.println("NO SERIALIZO!!!");
            final FileScanner fileScanner = new FileScanner();
            fileScanner.addFileComparerCriteria(new SameNameComparisonCriteria());
//            fileScanner.addFileComparerCriteria(new SameSizeAndContentComparisonCriteria());

            fileScanner.skipFile(new Condition<ScannedFile>() {
                @Override
                public boolean applyTo(final ScannedFile scannedFile) {
                    return ".nomedia".equals(scannedFile.name);
                }
            });
            fileScanner.skipFile(new Condition<ScannedFile>() {
                @Override
                public boolean applyTo(final ScannedFile scannedFile) {
                    return "desktop.ini".equals(scannedFile.name);
                }
            });

            fileScanner.scanPath(ROOT_DIRECTORY_PATH);

            groupsWithRepeatedItems = fileScanner.getFileGroups(true);

            FileSerializer.SerializeObject(groupsWithRepeatedItems, SERIALIZED_RESULT_FILE_PATH);
        }

        printResult(groupsWithRepeatedItems);

        System.out.println("Elapsed scan: " + (System.currentTimeMillis() - start) + " ms");

        start = System.currentTimeMillis();

        FileReplacer fileReplacer = new FileReplacer();
        fileReplacer.add(new Comparison<ScannedFile>() {
            @Override
            public boolean compare(final ScannedFile anObject, final ScannedFile anotherObject) {

                if ("D:\\CloudBackuped\\Fotos&Videos\\2012_2013 Fotos Graciosas".equals(anObject.folder)
                        && "D:\\CloudBackuped\\Fotos&Videos\\2014_04_07 Corsa".equals(anotherObject.folder)) {
                    return true;
                }
                return false;
            }
        });

        fileReplacer.replace(groupsWithRepeatedItems, REPEATED_DIRECTORY_PATH);

        System.out.println("Elapsed move: " + (System.currentTimeMillis() - start) + " ms");

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

    private static void printResult(final List<ArrayList<ScannedFile>> fileGroups) {

        for (final ArrayList<ScannedFile> fileGroup : fileGroups) {
            System.err.print("Root");
            for (final ScannedFile scannedFile : fileGroup) {
                System.err.println("\t" + scannedFile.fullPath);
            }
        }
    }

//    private static void printResultFolder(final List<ArrayList<ScannedFile>> fileGroups) {
//
//        final ArrayList<HashMap<String, HashMap<String, Integer>>>
//
//        for (final ArrayList<ScannedFile> fileGroup : fileGroups) {
//            System.err.print("Root");
//            for (final ScannedFile scannedFile : fileGroup) {
//                System.err.println("\t" + scannedFile.fullPath);
//            }
//        }
//    }

}
