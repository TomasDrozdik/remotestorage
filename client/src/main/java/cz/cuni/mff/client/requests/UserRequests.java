package cz.cuni.mff.client.requests;

import remotestorage.proto.MessagesProtos.ClientRequest;

import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static cz.cuni.mff.client.requests.HelpPrints.*;

public class UserRequests {
	/* Static methods only class */
	private UserRequests() {}



	public static ClientRequest authenticationRequest(String username, String password, PrintStream err) {
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.AUTH);
		b.addArgs(username);
		b.addArgs(password);
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
		b.addArgs(Paths.get(tokens[1]).getFileName().toString());
		return b.build();
	}

	public static ClientRequest down(String[] tokens, PrintStream err) {
		if (tokens.length != 2) {
			downHelp(err);
			return null;
		}
		ClientRequest.Builder b = ClientRequest.newBuilder();
		b.setType(ClientRequest.MessageType.DOWN);
		b.addArgs(tokens[1]);
		return b.build();
	}

	public static ClientRequest who(String[] tokens, PrintStream err) {
		//TODO
		return null;
	}
}
