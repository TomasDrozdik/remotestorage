package cz.cuni.mff.server;

import cz.cuni.mff.server.client_handler.ClientHandler;
import cz.cuni.mff.server.user_manager.UserManager;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {
    private ServerSocket sock;
    private int port;
    private UserManager userManager;

    private Server(ServerSocket sock, int p, UserManager userManager) {
        this.sock = sock;
        this.port = p;
        this.userManager = userManager;
    }

    /**
     * Atempts to crete a instance of a Server on a given port with given userManager.
     * @param port Port on which the server should be running.
     * @param userManeger User manager of the given server.
     * @return An instance of a server.
     * @throws IOException In case the socket creation fails.
     */
    public static Server createServer(int port, UserManager userManeger) throws IOException {
        ServerSocket sock = new ServerSocket(port);
        return new Server(sock, port, userManeger);
    }

    public void start() throws IOException {
        while (true) {
            Socket socket = sock.accept();
            //TODO log
            System.err.printf("New connection accepted form: %s:%d\n", socket.getInetAddress().toString(),
                    socket.getPort());
            new ClientHandler(socket);
        }
    }
}
