package fileEqualityScanner;

import java.io.File;
import java.io.Serializable;

public class ScannedFile implements Serializable {

    private static final long serialVersionUID = -2645028126125683552L;

    public final String fullPath;
    public final String folder;
    public final String name;
    public final long size;

    public ScannedFile(final File file) {
        this.fullPath = file.getAbsolutePath();
        this.folder = file.getParentFile().getAbsolutePath();
        this.name = file.getName();
        this.size = file.length();
    }

    @Override
    public String toString() {
        return "ScannedFile{" +
                "fullPath='" + fullPath + '\'' +
                ", folder='" + folder + '\'' +
                ", name='" + name + '\'' +
                ", size=" + size +
                '}';
    }
}
