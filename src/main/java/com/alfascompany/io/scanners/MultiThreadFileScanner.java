package com.alfascompany.io.scanners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alfascompany.thread.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public class MultiThreadFileScanner {

    private static Logger logger = LoggerFactory.getLogger(MultiThreadFileScanner.class);

    private final List<Predicate<File>> skipFiles = new ArrayList<>();
    private int threadsCount = 100;
    private long totalScannedCount = 0;
    private BigInteger totalScannedSize = BigInteger.ZERO;
    private long lastLoggedInfo = 0;

    public void setThreadsCount(final int threadsCount) {

        this.threadsCount = threadsCount;
    }

    public void skipFile(final Predicate<File> skipCondition) {

        skipFiles.add(skipCondition);
    }

    public void scanRecursively(final String rootPath, final Consumer<File> consumer) {

        scan(rootPath, consumer, true);
    }

    public void scan(final String rootPath, final Consumer<File> consumer, final boolean recursively) {

        scan(rootPath, file -> file, consumer, recursively);
    }

    public <T> void scan(final String rootPath, final Function<File, T> produceItemForConsumer, final Consumer<T> consumer, final boolean recursively) {

        final long start = System.currentTimeMillis();
        logger.debug("Scan path started!");

        final ThreadPool threadPool = new ThreadPool(threadsCount);

        scanFolder(threadPool, Paths.get(rootPath), produceItemForConsumer, consumer, recursively);

        threadPool.waitUntilFinish();

        logger.debug("Scan path finished in {" + (System.currentTimeMillis() - start) + "} millis!");
    }

    private <T> void scanFolder(final ThreadPool threadPool, final Path rootPath, final Function<File, T> produceItemForConsumer, final Consumer<T> consumer, final boolean recursively) {

        DirectoryStream<Path> pathStream = null;
        try {

            pathStream = Files.newDirectoryStream(rootPath);
            final Iterator<Path> filesIterator = pathStream.iterator();
            Path path = filesIterator.hasNext() ? filesIterator.next() : null;

            while (path != null) {

                scanFile(threadPool, path, produceItemForConsumer, consumer, recursively);

                path = filesIterator.hasNext() ? filesIterator.next() : null;
            }

        } catch (final IOException e) {
            throw new RuntimeException("Error scanning [" + rootPath + "]: [" + e + "]", e);
        } finally {
            if (pathStream != null) {
                try {
                    pathStream.close();
                } catch (final IOException e) {
                    logger.error("Error closing stream: [" + e.getMessage() + "]", e);
                }
            }
        }
    }

    private <T> void scanFile(final ThreadPool threadPool, final Path path, final Function<File, T> produceItemForConsumer, final Consumer<T> consumer, final boolean recursively) {

        final File file = path.toFile();

        // has to skip file?
        if (skipFiles.stream().anyMatch(condition -> condition.test(file))) {
            return;
        }

        logProgress(file);

        // generate item consumable in same thread (so you can take lenght and other things before its resource is free)
        final T itemConsumable = produceItemForConsumer.apply(file);

        // the magic of multi-thread workers, another thread will consume elements from the same iterator
        threadPool.processInPool(() -> consumer.accept(itemConsumable));

        // scan directories recursively?
        if (recursively && file.isDirectory()) {
            scanFolder(threadPool, path, produceItemForConsumer, consumer, recursively);
        }
    }

    private void logProgress(final File file) {

        synchronized (this) {
            totalScannedCount++;
            totalScannedSize = totalScannedSize.add(BigInteger.valueOf(file.length()));
            if (System.currentTimeMillis() - lastLoggedInfo > 5000) {
                lastLoggedInfo = System.currentTimeMillis();
                final BigInteger bitInteger1024 = BigInteger.valueOf(1024);
                logger.info("Total scanned files {" + totalScannedCount + "} with {" + totalScannedSize.divide(bitInteger1024).divide(bitInteger1024) + "} mb");
            }
        }
    }
}
