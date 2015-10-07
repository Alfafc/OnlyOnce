package com.alfascompany.io.serialization;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

public class FileSerializer {

    private static Logger logger = LoggerFactory.getLogger(FileSerializer.class);

    public static <T> void SerializeObject(T object, final String filePath) {

        OutputStream file = null;
        OutputStream buffer = null;
        try {
            file = new FileOutputStream(filePath);
            buffer = new BufferedOutputStream(file);
            try (final ObjectOutput output = new ObjectOutputStream(buffer)) {
                output.writeObject(object);
            }

        } catch (final IOException e) {
            logger.error("Error serializing object [" + e.getMessage() + "]", e);
            throw new RuntimeException("Error serializing object [" + e.getMessage() + "]", e);
        } finally {
            tryToCloseStream(file);
            tryToCloseStream(buffer);
        }
    }

    public static <T> T DeserializeObject(final String filePath) {

        InputStream file = null;
        InputStream buffer = null;
        try {
            file = new FileInputStream(filePath);
            buffer = new BufferedInputStream(file);
            try (final ObjectInputStream input = new ObjectInputStream(buffer)) {
                return (T) input.readObject();
            }
        } catch (final Exception e) {
            logger.error("Error serializing object [" + e.getMessage() + "]", e);
        } finally {
            tryToCloseStream(file);
            tryToCloseStream(buffer);
        }
        return null;
    }

    private static void tryToCloseStream(final Closeable file) {
        if (file != null) {
            try {
                file.close();
            } catch (final IOException e) {
                logger.error("Error closing stream object [" + e.getMessage() + "]", e);
            }
        }
    }
}
