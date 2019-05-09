package client;

import com.google.protobuf.InvalidProtocolBufferException;
import communication.ProtoCommunication;
import processors.UserRequestProcessor;
import remotestorage.proto.MessagesProtos;
import requests.AdminRequests;
import requests.HelpPrints;
import requests.UserRequests;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Client implements Runnable {
	private BufferedReader in;
	private PrintStream out;
	private PrintStream err;
	private Socket socket;
	private ProtoCommunication protoComm;
	private boolean isAdmin;

	public Client(InputStream is, OutputStream os, OutputStream err, Socket socket) {
		this.in = new BufferedReader(new InputStreamReader(is));
		this.out = new PrintStream(os);
		this.err = new PrintStream(err);
		this.socket = socket;
	}

	public void run() {
		String line;
		Request req;
		try {
			this.protoComm = new ProtoCommunication(socket.getInputStream(), socket.getOutputStream());

			while (!authenticate()) {
			}

			out.print('>');
			while ((line = in.readLine()) != null) {
				if ((req = parseRequest(line)) == null || req.clientRequest == null) {
					out.print('>');
					continue;
				}
				processRequest(req);
				processRespond();
				out.print('>');
			}
		} catch (ExitException e) {
			out.println("Exiting...");
		} catch (InvalidProtocolBufferException e) {
			out.println("Connection closed by server.\nExiting...");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private boolean authenticate() throws IOException {
		String username, password;
		MessagesProtos.ServerResponse serverResponse;

		out.print("username:");
		username = in.readLine();
		out.print("password:");
		password = in.readLine();
		if (username == null & password == null) {
			return false;
		}
		/* Send authentication packet */
		protoComm.send(UserRequests.authenticationRequest(username, password, err));
		/* Wait for the response */
		serverResponse = protoComm.recvServerResponse();
		if (serverResponse.getAck()) {
			if (serverResponse.getArgs(0).equals("admin")) {
				isAdmin = true;
				out.println(String.format("Welcome admin %s!", username));
			} else {
				isAdmin = false;
				out.println(String.format("Welcome %s!", username));
			}
		} else {
			err.println("Authentication unsuccessful.");
		}
		return serverResponse.getAck();
	}

	private Request parseRequest(String line) throws ExitException {
		String[] tokens = line.split(" +");
		if (tokens.length < 1 || line.length() == 0)
			return null;
		if (line.charAt(0) == '!') {
			out.print(executeShellCmd(line.substring(1)));
			return null;
		}
		switch (tokens[0]) {
			case "ls":
				return new Request(UserRequests.ls(tokens, err), tokens);
			case "up":
				return new Request(UserRequests.up(tokens, err), tokens);
			case "down":
				return new Request(UserRequests.down(tokens, err), tokens);
			case "who":
			case "addproperty":
			case "listproperty":
			case "listusers":
			case "removeuser":
				err.println("Yet unimplemented command: " + tokens[0]);
				break;
			case "adduser":
				return new Request(AdminRequests.addUser(tokens, err), tokens);
			case "help":
			case "?":
				if (isAdmin) {
					HelpPrints.printAdminHelp(err);
				} else {
					HelpPrints.printBaseUserHelp(err);
				}
				break;
			case "exit":
				throw new ExitException();
			default:
				err.println("Unknown command: " + tokens[0]);
				err.println("Press \"?\" or \"help\" + ENTER for help.");
				return null;
		}
		return null;
	}

	private void processRequest(Request req) throws IOException {
		/* Send request to server. */
		switch (req.clientRequest.getType()) {
			case UP:
				protoComm.send(req.clientRequest);
				UserRequestProcessor.up(protoComm, req.tokens[1]);
				break;
			case DOWN:
				if (checkFileOverride(req.tokens[1])) {
					protoComm.send(req.clientRequest);
					UserRequestProcessor.down(protoComm, req.tokens[1]);
				}
				break;
			default:
				protoComm.send(req.clientRequest);
		}
	}

	private void processRespond() throws IOException {
		MessagesProtos.ServerResponse response = protoComm.recvServerResponse();
		/* Print out whole message from server. */
		for (String s : response.getArgsList()) {
			out.println(s);
		}
		if (!response.getAck()) {
			/* Print out the error code. */
			switch (response.getError()) {
				case ALREADY_EXISTS:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.ALREADY_EXISTS));
					break;
				case CONNECTION_FAIL:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.CONNECTION_FAIL));
					break;
				case NOT_FOUND:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.NOT_FOUND));
					break;
				case INVALID_REQUEST:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.INVALID_REQUEST));
					break;
				case OPERATION_FAIL:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.OPERATION_FAIL));
					break;
				case OPERATION_NOT_YET_IMPLEMENTED:
					err.println(String.format("SERVER ERROR: %s",
						MessagesProtos.ServerResponse.ErrorType.OPERATION_NOT_YET_IMPLEMENTED));
					break;
				case UNRECOGNIZED:
					err.println(String.format("SERVER ERROR: %s", MessagesProtos.ServerResponse.ErrorType.UNRECOGNIZED));
					break;
			}
		}
	}

	/**
	 * Safety check for for file overrides.
	 * @param fileName name of the file
	 * @return true if process can continue, false if user disagrees with file override.
	 */
	private boolean checkFileOverride(String fileName) throws IOException {
		if (Files.exists(Paths.get(fileName))) {
			out.print(String.format("File %s already exists. Do you want to override it? (y/N): ", fileName));
			return in.readLine().equals("y");
		}
		return true;
	}

	/**
	 * Execute a given command in separate process using Runtime.exec() function.
	 *
	 * @param command command to execute
	 * @return exit value of given process
	 */
	private int executeShellCmd(String command) {
		try {
			var proc = Runtime.getRuntime().exec(command);
			/* Redirect the output and error stream. */
			var t1 = new ParallelStreamRedirect(proc.getInputStream(), out);
			var t2 = new ParallelStreamRedirect(proc.getErrorStream(), err);
			t1.start();
			t2.start();
			int exitval =  proc.waitFor();
			t1.join();
			t2.join();
			return exitval;
		} catch (IOException | InterruptedException e) {
			err.println("Error: exec unsuccessful.");
			return -1;
		}
	}

	/**
	 * Redirects given input stream to given output stream in parallel thread.
	 */
	class ParallelStreamRedirect extends Thread {
		InputStream in;
		PrintStream out;

		ParallelStreamRedirect(InputStream in, PrintStream out) {
			this.in = in;
			this.out = out;
		}

		public void run() {
			try {
				BufferedReader br = new BufferedReader(new InputStreamReader(in));
				String line;
				while ((line = br.readLine()) != null)
					out.println(line);
			} catch (IOException ioe) {
				ioe.printStackTrace();
			}
		}
	}

	/**
	 * Pair like class combining ClientRequest and the tokens it was parsed from due to some information loss in
	 * process of creating ClientRequest.
	 */
	private class Request {
		MessagesProtos.ClientRequest clientRequest;
		String[] tokens;

		Request(MessagesProtos.ClientRequest clientRequest, String[] tokens) {
			this.clientRequest = clientRequest;
			this.tokens = tokens;
		}
	}
}
