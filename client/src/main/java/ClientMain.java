import client.Client;

import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class ClientMain {

	private static void usage() {
		System.err.println("usage: java ClientMain [ip] [port]");
	}

	public static void main(String[] args) {
		if (args.length != 2)
			usage();

		Socket socket;
		try {
			socket = new Socket(args[0], Integer.parseInt(args[1]));
		} catch (UnknownHostException e) {
			System.err.println("Error: unknown host.");
			return;
		} catch (NumberFormatException e) {
			usage();
			return;
		} catch (IOException e) {
			System.err.println("Error: unable to create socket for given host and port.");
			return;
		}
		Client client = new Client(System.in, System.out, System.err, socket);
		client.run();
	}
}
