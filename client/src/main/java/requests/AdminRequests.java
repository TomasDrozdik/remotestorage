package requests;


import remotestorage.proto.MessagesProtos.ClientRequest;

import java.io.PrintStream;

public class AdminRequests {

	/* Static methods only class */
	private AdminRequests() {}

	public static void printHelp(PrintStream out) {
		System.err.println("List of available admin commands: ");
		addUserHelp(out);
		out.println();
		cmdExecHelp(out);
		out.println();
	}

	public static ClientRequest addUser(String[] tokens, PrintStream err) {
		if (tokens.length != 4 || (!tokens[3].equals("admin") && !tokens[3].equals("user"))) {
			addUserHelp(err);
			return null;
		}

		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.ADD_USER);

		b.setArg1(tokens[1]);
		b.setArg2(tokens[2]);
		b.setArg3(tokens[3]);

		return b.build();
	}

	private static void cmdExecHelp(PrintStream err) {
		err.println("You can also execute any programs via exec call. Just start with \"!\"");
		err.println("-> ![cmd] [arg]...");
	}

	private static void addUserHelp(PrintStream err) {
		err.println("> adduser [username] [password] [admin|user]");
	}
}
