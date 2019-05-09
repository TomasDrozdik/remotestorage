package structure.files;

import structure.StructureException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class HomesDir {
	Path path;
	Map<String, HomeDir> homeDirs;

	private HomesDir(Path p, Map<String, HomeDir> homeDirs) {
		this.path = p;
		this.homeDirs = homeDirs;
	}

	/**
	 * Creates HomesDir interface to home directory.
	 * <p>
	 * Checks integrity according to provided user property dir.
	 *
	 * @param p           path to the user home dir
	 * @param passwdFile  reference to the passwd file for integrity checks
	 * @param propertyDir reference to the properties dir for integrity checks
	 * @return returns non null HomesDir otherwise throws exception
	 * @throws StructureException to signal that something went wrong check exception type for further info.
	 */
	public static HomesDir createHomesDir(Path p, PasswdFile passwdFile, PropertyDir propertyDir)
			throws StructureException {

		Map<String, HomeDir> homeDirs = new HashMap<>();

		if (!Files.isDirectory(p))
			throw new StructureException(StructureException.Type.NOT_DIRECTORY);

		checkIntegrity(p, passwdFile);

		try {
			for (Path dirEnt : Files.newDirectoryStream(p)) {
				homeDirs.put(dirEnt.getFileName().toString(), HomeDir.createHomeDir(dirEnt,
						propertyDir.getUserProperty(dirEnt.getFileName().toString())));
			}
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return new HomesDir(p, homeDirs);
	}

	/**
	 * Checks the integrity of the database by matching it against passwdFile and properties.
	 *
	 * @param p          path of the home directory
	 * @param passwdFile reference to the passwd file for integrity checks
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
	 * Get home directory for given username.
	 *
	 * @param username username
	 * @return HomeDir interface to users home directory
	 */
	public HomeDir getHomedir(String username) {
		return homeDirs.get(username);
	}

	public HomeDir addNewHomeDir(String username, UserProperty up) throws StructureException {
		Path p = Paths.get(path.toString(), username);
		HomeDir hd;

		if (homeDirs.containsKey(username))
			return null;

		try {
			Files.createDirectory(p);
			hd = HomeDir.createHomeDir(p, up);
			homeDirs.put(username, hd);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		}

		return hd;
	}
}
