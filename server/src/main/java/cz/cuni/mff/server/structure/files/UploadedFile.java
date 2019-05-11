package cz.cuni.mff.server.structure.files;

import java.nio.file.Path;

public class UploadedFile {
    public Path path;

    public UploadedFile(Path p) {
        this.path = p;
    }

    public String getName() {
        return path.getFileName().toString();
    }
}
