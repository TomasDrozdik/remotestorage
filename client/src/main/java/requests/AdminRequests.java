package requests;


import remotestorage.proto.MessagesProtos.ClientRequest;

import java.io.PrintStream;

import static requests.HelpPrints.addUserHelp;

public class AdminRequests {
	/* Static methods only class */
	private AdminRequests() {}

	public static ClientRequest addUser(String[] tokens, PrintStream err) {
		if (tokens.length != 4 || (!tokens[3].equals("admin") && !tokens[3].equals("user"))) {
			addUserHelp(err);
			return null;
		}

		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.ADD_USER);

		for (int i = 1; i < 4; ++i) {
			b.addArgs(tokens[i]);
		}

		return b.build();
	}

}
