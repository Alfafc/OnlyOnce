package utils.serialization;

import java.io.*;

public class FileSerializer {

    public static <T> void SerializeObject(T object, final String filePath) {

        OutputStream file = null;
        OutputStream buffer = null;
        try {
            file = new FileOutputStream(filePath);
            buffer = new BufferedOutputStream(file);
            try (final ObjectOutput output = new ObjectOutputStream(buffer)) {
                output.writeObject(object);
            }

        } catch (final IOException exception) {
            System.err.println("Error serializing object [" + exception.getMessage() + "]");
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

        } catch (final Exception exception) {
            System.err.println("Error serializing object [" + exception.getMessage() + "]");
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
                System.err.println("Error closing stream object [" + e.getMessage() + "]");
            }
        }
    }
}
