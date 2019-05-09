package server.client_handler;

import com.google.protobuf.InvalidProtocolBufferException;
import communication.ProtoCommunication;
import remotestorage.proto.MessagesProtos;
import server.client_handler.responses.AdminResponses;
import server.client_handler.responses.BaseUserResponses;
import server.client_handler.responses.ResponseBuilder;
import user_manager.UserManager;
import user_manager.users.AdminUser;
import user_manager.users.BaseUser;
import user_manager.users.User;

import java.io.IOException;
import java.net.Socket;
import java.util.Collections;

public class ClientHandler extends Thread {
	private Socket sock;
	private UserManager userManager;
	private ProtoCommunication protoComm;

	public ClientHandler(Socket sock) {
		this.sock = sock;
		this.userManager = UserManager.getUserManager();
		start();
	}

	@Override
	public void run() {
		User u = null;
		try {
			this.protoComm = new ProtoCommunication(sock.getInputStream(), sock.getOutputStream());

			while ((u = authenticate()) == null) {
			}

			if (u instanceof BaseUser) {
				//TODO log
				System.err.printf("User %s successfully authenticated.\n", u.getUsername());
				serveUser((BaseUser) u);
			} else /* u instanceof AdminUser */ {
				//TODO log
				System.err.printf("Admin %s successfully authenticated.\n", u.getUsername());
				serveAdmin((AdminUser) u);
			}
		} catch (InvalidProtocolBufferException e) {
			System.err.printf("Communication with client %s has unexpectedly ended.", u.getUsername());
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private User authenticate() throws IOException {
		MessagesProtos.ClientRequest clientRequest = protoComm.recvClientRequest();

		if (!(clientRequest.getType() == MessagesProtos.ClientRequest.MessageType.AUTH))
			return null;

		User user = userManager.authenticate(clientRequest.getArgs(0), clientRequest.getArgs(1));

		if (user != null) {
			if (user instanceof BaseUser)
				protoComm.send(ResponseBuilder.buildPositiveResponse(Collections.singletonList("user")));
			else
				protoComm.send(ResponseBuilder.buildPositiveResponse(Collections.singletonList("admin")));
		} else {
			//TODO log
			System.err.printf("Client failed to authenticate with username %s.\n", clientRequest.getArgs(0));
			protoComm.send(ResponseBuilder.buildNegativeResponse(MessagesProtos.ServerResponse.ErrorType.AUTH_FAIL));
		}
		return user;
	}

	private void serveUser(BaseUser u) {
		try {
			while (true) {
				MessagesProtos.ClientRequest clientRequest = protoComm.recvClientRequest();
				switch (clientRequest.getType()) {
					case LS:
						protoComm.send(BaseUserResponses.ls(u));
						break;
					case UP:
						protoComm.send(BaseUserResponses.up(u, protoComm, clientRequest.getArgs(0)));
						break;
					case DOWN:
						protoComm.send(BaseUserResponses.down(u, protoComm, clientRequest.getArgs(0)));
						break;
					default:
						protoComm.send(ResponseBuilder.invalidRequest());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void serveAdmin(AdminUser u) {
		try {
			while (true) {
				MessagesProtos.ClientRequest clientRequest = protoComm.recvClientRequest();
				switch (clientRequest.getType()) {
					case ADD_USER:
						protoComm.send(AdminResponses.addUser(u, clientRequest.getArgs(0), clientRequest.getArgs(1),
								clientRequest.getArgs(2).equals("admin")));
						break;
					case REMOVE_USER:
					case CHECK_USER_PROPERTY:
					case CHANGE_USER_PROPERTY:
						protoComm.send(ResponseBuilder.buildNegativeResponse(
								MessagesProtos.ServerResponse.ErrorType.OPERATION_NOT_YET_IMPLEMENTED));
						break;
					default:
						protoComm.send(ResponseBuilder.invalidRequest());
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
