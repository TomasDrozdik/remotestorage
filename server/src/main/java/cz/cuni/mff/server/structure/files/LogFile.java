package cz.cuni.mff.server.structure.files;


import cz.cuni.mff.server.structure.StructureException;

import java.nio.file.Files;
import java.nio.file.Path;

public class LogFile {
    private Path path;

    private LogFile(Path p) {
        this.path = p;
    }

    /**
     * Creates interface to log file.
     *
     * @param p path to the log file
     * @return non null LogFile
     * @throws StructureException to signal that something went wrong check exception type for further info.
     */
    public static LogFile createLogFile(Path p) throws StructureException {
        if (!Files.isRegularFile(p))
            throw new StructureException(StructureException.Type.NOT_FILE);

        return new LogFile(p);
    }

    /**
     * Returns path to the logfile.
     *
     * @return path to the log file
     */
    public Path getPath() {
        return path;
    }
}
