import server.Server;
import structure.Structure;
import structure.StructureException;
import user_manager.UserManager;

import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class ServerMain {

	private static void usageAndExit() {
		System.err.println("usage: java ServerMain [port] [pathToRootStorageDir]");
		System.exit(1);
	}

	public static void main(String[] args) {
		int port = -1;

		if (args.length != 2) {
			usageAndExit();
		}
		try {
			port = Integer.parseInt(args[0]);
		} catch (NumberFormatException e) {
			usageAndExit();
		}
		try {
			/* Now prepare Structure */
			Path pathToRootStorageDir = Paths.get(args[1]);
			Structure.createStructure(pathToRootStorageDir);
			/* Now prepare the UserManager and add two default debuging and demo users. */
			UserManager userManager = UserManager.getUserManager();
			userManager.addDefaultAdminUser("admin", "admin");
			userManager.addDefaultUser("user", "user");
			try {
				Server server = Server.createServer(port, userManager);
				server.start();
			} catch (IOException e) {
				// TODO: log
				e.printStackTrace();
			}
		} catch (StructureException e) {
			// TODO: log
			e.printStackTrace();
		}
	}
}
