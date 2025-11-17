import Utility.Utility;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Server {
    private final int port;
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private Server(int port) {
        this.port = port;
    }

    public static Server bindToServer(int port) {
        return new Server(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.submit(() -> {
                    try {
                        Utility.handle(socket);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (IOException e) {
            System.out.println("Вероятнее всего порт " + port + " занят.");
            e.printStackTrace();
        }
    }
}
