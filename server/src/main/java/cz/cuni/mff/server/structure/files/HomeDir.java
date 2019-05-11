package cz.cuni.mff.server.structure.files;

import cz.cuni.mff.server.structure.StructureException;
import cz.cuni.mff.server.user_manager.users.BaseUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class HomeDir {
    Path path;
    private BaseUser user;
    private List<UploadedFile> files;

    private HomeDir(Path p, List<UploadedFile> files) {
        this.path = p;
        this.files = files;
    }

    /**
     * Creates HomeDir interface to user home directory.
     * <p>
     * Checks integrity according to provided user property dir.
     *
     * @param p            path to the home dir
     * @param userProperty reference to the corresponding user property file
     * @return returns non null HomeDir otherwise throws exception
     * @throws StructureException to signal that something went wrong check exception type for further info.
     */
    public static HomeDir createHomeDir(Path p, UserProperty userProperty) throws StructureException {
        List<UploadedFile> files = new ArrayList<>();
        if (!Files.isDirectory(p)) {
            throw new StructureException(StructureException.Type.NOT_DIRECTORY);
        }
        checkIntegrity(p, userProperty);
        try (var dirStream = Files.newDirectoryStream(p)) {
            for (Path dirEnt : dirStream) {
                files.add(new UploadedFile(dirEnt));
            }
        } catch (SecurityException e) {
            throw new StructureException(StructureException.Type.SECURITY_EXCEPTION);
        } catch (IOException e) {
            throw new StructureException(StructureException.Type.IO_EXCEPTION);
        }
        return new HomeDir(p, files);
    }

    private static void checkIntegrity(Path p, UserProperty userProperty) {
        //TODO
    }

    public List<UploadedFile> getFiles() {
        return files;
    }

    public Path getFileByName(String fileName) {
        for (UploadedFile uf : files) {
            if (uf.getName().equals(fileName)) {
                return uf.path;
            }
        }

        return null;
    }

    public Path addNewFile(String fileName) throws IOException {
        Path p = Paths.get(path.toString(), fileName);
        /* May throw FileAlreadyExistsException in that case don't add it to the Files. */
        // TODO maybe redo this a little so that it doesn't use exceptions
        Files.createFile(p);
        files.add(new UploadedFile(p));
        return p;
    }

    public void removeFile(String fileName) throws IOException {
        Path p = Paths.get(path.toString(), fileName);

        /* May throw IOException in that case don't remove it from the Files. */
        Files.delete(p);

        files.removeIf(uploadedFile -> uploadedFile.getName().equals(fileName));
    }
}
