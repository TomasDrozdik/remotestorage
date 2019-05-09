package structure;

import structure.files.*;
import user_manager.Authenticator;
import user_manager.users.BaseUser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.SecureRandom;

/**
 * Singleton class used as interface to the file structure behind the server.
 * <p>
 * Supports loading existing coherent file structure or creating new one.
 */
public class Structure implements IUserStructure, IAdminStructure, IAuthenticatorStructure {
	private static String rootDirName = "storage", passwdName = "passwd", logName = "log", propertiesName = "properties",
			homeName = "home";
	private static Structure s;
	private Path rootDir;
	private LogFile log;
	private HomesDir home;
	private PropertyDir properties;
	private PasswdFile passwd;
	private SecureRandom random = new SecureRandom();

	private Structure(Path rootDir, PasswdFile passwdFile, LogFile logFile, HomesDir homeDir,
	                  PropertyDir properties) {
		this.rootDir = rootDir;
		this.passwd = passwdFile;
		this.log = logFile;
		this.home = homeDir;
		this.properties = properties;
	}

	/**
	 * Creation of singleton class Structure.
	 *
	 * @param rootDir root directory of the database structure
	 * @return found structure or new created one
	 */
	public static void createStructure(Path rootDir) throws StructureException {
		assert (Structure.s == null);

		Structure.s = tryExistingStructure(rootDir);

		if (Structure.s == null)
			Structure.s = initDefault(rootDir);
	}

	/**
	 * If no existing structure is found create new one in dir path.
	 *
	 * @param rootDir Path to dir in which the storage should be created.
	 * @return Not null Newly created structure.
	 * @throws StructureException if creation was not possible, reason specified inside exception Type.
	 */
	private static Structure initDefault(Path rootDir) throws StructureException {
		PasswdFile passwdFile;
		LogFile logFile;
		HomesDir homesDir;
		PropertyDir propertyDir;

		if (!Files.isDirectory(rootDir))
			throw new StructureException(StructureException.Type.NOT_DIRECTORY);

		try {
			/* Create passwd file */
			passwdFile = PasswdFile.createPasswdFile(Files.createFile(Paths.get(rootDir.toString(), passwdName)));

			/* Create log file */
			logFile = LogFile.createLogFile(Files.createFile(Paths.get(rootDir.toString(), logName)));

			/* Create properties dir */
			propertyDir = PropertyDir.createPropertyDir(Files.createDirectory(Paths.get(rootDir.toString(),
					propertiesName)), passwdFile);

			/* Create home dir */
			homesDir = HomesDir.createHomesDir(Files.createDirectory(Paths.get(rootDir.toString(), homeName)),
					passwdFile, propertyDir);
		} catch (IOException e) {
			throw new StructureException(StructureException.Type.IO_EXCEPTION);
		} catch (SecurityException e) {
			throw new StructureException(StructureException.Type.SECURITY_EXCEPTION);
		}

		return new Structure(rootDir, passwdFile, logFile, homesDir, propertyDir);
	}

	/**
	 * Get Structure
	 *
	 * @return non null Structure
	 */
	public static Structure getStructure() {
		assert (s != null);

		return s;
	}

	private static Structure tryExistingStructure(Path path) throws StructureException {
		Path p;
		PasswdFile passwdFile;
		LogFile logFile;
		HomesDir homesDir;
		PropertyDir propertyDir;

		if (!Files.isDirectory(path))
			return null;

		/* Check for correct structure starting with passwd file. */
		p = Paths.get(path.toString(), passwdName);
		if (!Files.exists(p))
			return null;
		passwdFile = PasswdFile.createPasswdFile(p);

		/* Check for log directory. */
		p = Paths.get(path.toString(), logName);
		if (!Files.exists(p))
			return null;
		logFile = LogFile.createLogFile(p);

		/* Check for properties directory. */
		p = Paths.get(path.toString(), propertiesName);
		if (!Files.exists(p))
			return null;
		propertyDir = PropertyDir.createPropertyDir(p, passwdFile);

		/* Check for home directory. */
		p = Paths.get(path.toString(), homeName);
		if (!Files.exists(p))
			return null;
		homesDir = HomesDir.createHomesDir(p, passwdFile, propertyDir);

		return new Structure(path, passwdFile, logFile, homesDir, propertyDir);
	}

	/**
	 * Get hashed passwd for given user.
	 *
	 * @param username username
	 * @return hashed password and salt in record
	 */
	public PasswdRecord getUserPasswd(String username) {
		return passwd.getPasswd(username);
	}

	/* ---------------------------------------- IUserStructure ---------------------------------------- */

	/**
	 * Get users home folder.
	 *
	 * @param u user
	 * @return non null HomeDir
	 */
	public HomeDir getHome(BaseUser u) {
		return home.getHomedir(u.getUsername());
	}

	/**
	 * Get user property for given user.
	 *
	 * @param u user
	 * @return non null UserProperty
	 */
	public UserProperty getUserProperty(BaseUser u) {
		return properties.getUserProperty(u.getUsername());
	}

	/* ---------------------------------------- IAuthenticatorStructure ---------------------------------------- */

	/**
	 * Add user to the structure.
	 *
	 * @param username username
	 * @param password password
	 * @param isAdmin  bool specifying admin privileges
	 * @return true if operation was successful, false otherwise
	 * @throws StructureException
	 */
	public ReturnValue addUser(String username, String password, boolean isAdmin) throws StructureException {
		UserProperty up;
		long salt = random.nextLong();

		ReturnValue rv = passwd.addUser(username, Authenticator.hashPasswd(password, salt), isAdmin, salt);

		/* Check if user addition was successful. */
		if (rv != ReturnValue.OK)
			return rv;

		/* Don't create structure for admin users */
		if (!isAdmin) {
			/* Add property file and home dir for user. */
			if ((up = properties.addNewPropertyFile(username)) == null ||
					home.addNewHomeDir(username, up) == null) {
				return ReturnValue.INTEGRITY_FAIL;
			}
		}

		return ReturnValue.OK;
	}

	/**
	 * Is given user a admin?
	 *
	 * @param username username
	 * @return whether given user is admin
	 */
	public boolean isAdmin(String username) {
		return passwd.isAdmin(username);
	}

	/* ---------------------------------------- IAdminStructure ---------------------------------------- */

	public enum ReturnValue {
		OK,
		ALREADY_EXISTS,
		NON_EXITSTENT,
		INTEGRITY_FAIL,
		EXCEPTION,
	}

}
