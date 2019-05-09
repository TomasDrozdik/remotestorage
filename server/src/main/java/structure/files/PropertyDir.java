package structure.files;

import structure.StructureException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class PropertyDir {
	private Path path;
	private Map<String, UserProperty> data;

	private PropertyDir(Path p, Map<String, UserProperty> data) {
		this.path = p;
		this.data = data;
	}

	/**
	 * Create inteface to the propery dir.
	 *
	 * @param p          path to the directory
	 * @param passwdFile reference to passwd file for integrity check
	 * @return non null PropertyDir othrwise exception is thrown
	 * @throws StructureException to signal that something went wrong check exception type for further info.
	 */
	public static PropertyDir createPropertyDir(Path p, PasswdFile passwdFile) throws StructureException {
		Map<String, UserProperty> data = new HashMap<>();

		if (!Files.isDirectory(p))
			throw new StructureException(StructureException.Type.NOT_DIRECTORY);

		checkIntegrity(p, passwdFile);

		try {
			for (Path dirEntry : Files.newDirectoryStream(p)) {
				data.put(dirEntry.getFileName().toString(), UserProperty.createUserProperty(dirEntry));
			}
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return new PropertyDir(p, data);
	}

	/**
	 * Checks the integrity of the properties directory according to the passwd file.
	 *
	 * @param p          path to properites directory
	 * @param passwdFile path to the passwd file
	 */
	private static void checkIntegrity(Path p, PasswdFile passwdFile) throws StructureException {
		Set<String> userSet = passwdFile.getUsers();

		try {
			for (Path dirEntry : Files.newDirectoryStream(p)) {
				if (!userSet.contains(dirEntry.getFileName().toString()))
					throw new StructureException(StructureException.Type.INTEGRITY_ERROR);
			}
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}
	}

	/**
	 * Get property interface for given user
	 *
	 * @param username username
	 * @return UserProperty for username
	 */
	public UserProperty getUserProperty(String username) {
		return data.get(username);
	}

	public UserProperty addNewPropertyFile(String username) throws StructureException {
		UserProperty up;

		if (data.containsKey(username))
			return null;

		try {
			Path path = Paths.get(this.path.toString(), username);
			Files.createFile(path);
			up = UserProperty.createUserProperty(path);
			data.put(username, up);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return up;
	}
}
