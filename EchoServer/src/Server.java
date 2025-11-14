import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import Enum.Commands;

public class Server {
    private final int port;
    //
    private final ExecutorService pool = Executors.newCachedThreadPool();

    private Server(int port) {
        this.port = port;
    }

    public static Server bindToServer(int port) {
        return new Server(port);
    }

    public void run() {
        try (ServerSocket server = new ServerSocket(port)) {
//            try (Socket socket = server.accept()) {
//                handle(socket);
//            }
            while (!server.isClosed()) {
                Socket socket = server.accept();
                pool.submit(() -> handle(socket));
            }
        } catch (IOException e) {
            System.out.println("Вероятнее всего порт " + port + " занят.");
            e.printStackTrace();
        }
    }

    private void handle(Socket socket) {
        System.out.printf("Connected client: %s%n", socket);

        try (socket;
             Scanner reader = getReader(socket);
             PrintWriter writer = getWriter(socket)
        ) {
            sendResponse("Hello from Server " + socket.getPort(), writer);
            while (true) {
                String input = reader.nextLine().trim();
                if (isEmptyMsg(input) || isQuitMsg(input)) {
                    break;
                }
                sendResponse(input.toUpperCase(), writer);

                System.out.printf("Got message: %s%n", input);

            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection!");
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Client is disconnected!");
    }

    private PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }

    private Scanner getReader(Socket socket) throws IOException {
        InputStream inputStream = socket.getInputStream();
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        return new Scanner(inputStreamReader);
    }

    private static boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }

    private static void sendResponse(String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}
