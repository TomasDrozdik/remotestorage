package cz.cuni.mff.server.structure.files;

import cz.cuni.mff.server.structure.StructureException;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

/**
 * Interface to file storing user property.
 * <p>
 * Format:
 * key:value
 * ...
 */
public class UserProperty {
    Path path;
    Map<String, String> data;

    private UserProperty(Path p, Map<String, String> data) {
        this.path = p;
        this.data = data;
    }

    /**
     * Creates interface to the property file for given user
     *
     * @param p path to user property file
     * @return non null UserProperty otherwise the exception is thrown
     * @throws StructureException to signal that something went wrong check exception type for further info.
     */
    public static UserProperty createUserProperty(Path p) throws StructureException {
        Map<String, String> data = new HashMap<>();
        String line;
        String[] tokens;

        if (!Files.isRegularFile(p))
            throw new StructureException(StructureException.Type.NOT_FILE);

        try (BufferedReader br = new BufferedReader(new FileReader(p.toFile()))) {
            while ((line = br.readLine()) != null) {
                tokens = line.split(":");
                if (tokens.length != 2)
                    throw new StructureException(StructureException.Type.INTEGRITY_ERROR);

                data.put(tokens[0], tokens[1]);
            }
        } catch (FileNotFoundException e) {
            throw new StructureException(StructureException.Type.FILE_NOT_FOUND_EXCEPTION);
        } catch (IOException e) {
            throw new StructureException(StructureException.Type.IO_EXCEPTION);
        }

        return new UserProperty(p, data);
    }

    public String getValue(String key) {
        //TODO check aht get returns on invalid key
        return data.get(key);
    }

    public boolean setNew(String key, String value) {
        if (data.containsKey(key))
            return false;

        data.put(key, value);
        return true;
    }
}
