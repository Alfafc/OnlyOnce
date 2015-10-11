package com.alfascompany.io.scanners;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.alfascompany.thread.ThreadPool;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class MultiThreadFileScanner {

    private static Logger logger = LoggerFactory.getLogger(MultiThreadFileScanner.class);

    private final List<Predicate<File>> skipFiles = new ArrayList<>();
    private int threadsCount = 100;

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

        final long start = System.currentTimeMillis();
        logger.debug("Scan path started!");

        final ThreadPool threadPool = new ThreadPool(threadsCount);

        scanFolder(threadPool, Paths.get(rootPath), consumer, recursively);

        threadPool.waitUntilFinish();

        logger.debug("Scan path finished in {" + (System.currentTimeMillis() - start) + "} millis!");
    }

    private void scanFolder(final ThreadPool threadPool, final Path rootPath, final Consumer<File> consumer, final boolean recursively) {

        DirectoryStream<Path> pathStream = null;
        try {

            pathStream = Files.newDirectoryStream(rootPath);
            final Iterator<Path> filesIterator = pathStream.iterator();
            Path path = filesIterator.hasNext() ? filesIterator.next() : null;

            while (path != null) {

                // the magic of multi-thread workers, another thread will consume elements from the same iterator
                final Path finalPath = path;
                threadPool.processInPool(() -> scanFile(threadPool, finalPath, consumer, recursively));

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

    private void scanFile(final ThreadPool threadPool, final Path path, final Consumer<File> consumer, final boolean recursively) {

        final File file = path.toFile();

        // has to skip file?
        if (skipFiles.stream().anyMatch(condition -> condition.test(file))) {
            return;
        }

        // consume file
        consumer.accept(file);

        // scan directories recursively?
        if (recursively && file.isDirectory()) {
            threadPool.processInPool(() -> scanFolder(threadPool, path, consumer, recursively));
        }
    }
}
