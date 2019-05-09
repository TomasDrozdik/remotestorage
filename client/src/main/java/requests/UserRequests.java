package requests;

import remotestorage.proto.MessagesProtos.ClientRequest;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class UserRequests {
	/* Static methods only class */
	private UserRequests() {}

	public static void printHelp(PrintStream out) {
		out.println("List of available user commands:");
		lsHelp(out);
		upHelp(out);
		downHelp(out);
		whoHelp(out);
		out.println();
		cmdExecHelp(out);
		out.println();
	}

	public static ClientRequest authenticationRequest(String username, String password, PrintStream err) {
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.AUTH);
		b.setArg1(username);
		b.setArg2(password);
		return b.build();
	}

	public static ClientRequest ls(String[] tokens, PrintStream err) {
		if (tokens.length != 1) {
			lsHelp(err);
			return null;
		}
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.LS);
		return b.build();
	}

	public static ClientRequest up(String[] tokens, PrintStream err) {
		if (tokens.length != 2) {
			upHelp(err);
			return null;
		}
		Path p = Paths.get(tokens[1]);
		if (!Files.exists(p)) {
			err.println(String.format("Error: file %s doesn't exist.", p));
			return null;
		}
		if (!Files.isRegularFile(p)) {
			err.println(String.format("Error: file %s isn't regular file.", p));
			return null;
		}
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.UP);
		b.setArg1(Paths.get(tokens[1]).getFileName().toString());
		return b.build();
	}

	public static ClientRequest down(String[] tokens, PrintStream err) {
		if (tokens.length != 2) {
			downHelp(err);
			return null;
		}
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.DOWN);
		b.setArg1(tokens[1]);
		return b.build();
	}

	public static ClientRequest who(String[] tokens, PrintStream err) {
		//TODO
		return null;
	}

	private static void cmdExecHelp(PrintStream err) {
		err.println("You can also execute any programs via exec call. Just start with \"!\"");
		err.println("-> ![cmd] [arg]...");
	}

	private static void lsHelp(PrintStream err) {
		err.println("-> ls");
	}

	private static void upHelp(PrintStream err) {
		err.println("-> up [file]");
	}

	private static void downHelp(PrintStream err) {
		err.println("-> down [file]");
	}

	private static void whoHelp(PrintStream err) {
		err.println("-> who");
	}
}
