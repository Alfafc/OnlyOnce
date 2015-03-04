import fileEqualityScanner.FileReplacer;
import fileEqualityScanner.FileScanner;
import fileEqualityScanner.ScannedFile;
import utils.Comparison;
import utils.Condition;
import fileEqualityScanner.comparer.commons.SameNameComparisonCriteria;
import utils.ListUtils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Main {

    //    private final static String ROOT_DIRECTORY_PATH = "D:\\CloudBackuped\\Fotos&Videos";
    private final static String ROOT_DIRECTORY_PATH = "G:\\";
    private final static String REPEATED_DIRECTORY_PATH = "D:\\Repetido_BORRAR_FER";

    public static void main(final String[] args) throws IOException {

        final FileScanner fileScanner = new FileScanner();
        fileScanner.addFileComparerCriteria(new SameNameComparisonCriteria());
//        fileEqualityScanner.addFileComparerCriteria(new SameSizeAndContentCriteriaGeneric());

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

        long start = System.currentTimeMillis();

        fileScanner.scanPath(ROOT_DIRECTORY_PATH);

        final List<ArrayList<ScannedFile>> groupsWithRepeatedItems = ListUtils.filter(fileScanner.getFileGroups(), new Condition<ArrayList<ScannedFile>>() {
            @Override
            public boolean applyTo(final ArrayList<ScannedFile> item) {
                return item.size() > 1;
            }
        });

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

        long savedSpace = 0;
        long fileCount = 0;
        for (final ArrayList<ScannedFile> groupsWithRepeatedItem : groupsWithRepeatedItems) {

            savedSpace -= groupsWithRepeatedItem.get(0).size;
            fileCount += groupsWithRepeatedItem.size() - 1;
            for (final ScannedFile file : groupsWithRepeatedItem) {
                savedSpace += file.size;
            }
        }
        System.err.println("Save space is [" + (savedSpace / 1024 / 1024) + "] MB in [" + fileCount + "] files");
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
