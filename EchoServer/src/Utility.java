import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Scanner;

import Entity.Client;
import Enum.Commands;

public class Utility {
    private static final List<Client> clients = new ArrayList<>();

    private Utility() {
    }

    public static void handle(Socket socket) throws IOException {
        System.out.printf("Connected client: %s%n", socket);
        Client client = new Client(socket);
        client.setConnected(true);
        clients.add(client);

        try (socket;
             Scanner reader = getReader(socket);
             PrintWriter writer = getWriter(socket)
        ) {
            sendResponse(client, "Hello from Server " + socket.getPort(), writer);
            while (true) {
                String input = reader.nextLine().trim();
                if (isEmptyMsg(input) || isQuitMsg(input)) {
                    break;
                }

//                String[] parts = input.split(" ", 2);
//                String command = " ";
//                String message;
//
//                try {
//                    command = parts[0].toUpperCase();
//                    message = parts[1];
//                } catch (ArrayIndexOutOfBoundsException e) {
//                    message = parts[0];
//                }

                Commands cmd = Commands.fromInput(input);


                try {
                    cmd.execute(input, writer, client, clients);
                } catch (NullPointerException e) {
                    for (Client c : clients) {
                        if (!c.getNickname().equals(client.getNickname())) {
                            if (c.isConnected()) {
                                c.sendMsg(client.getNickname() + ": " + input);
                            }
                        }
                    }
                }
                System.out.printf("Got message: %s%n", input);


                
            }
        } catch (NoSuchElementException e) {
            System.out.println("Entity.Client dropped connection!");
            client.setConnected(false);
            clients.remove(client);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.out.println("Entity.Client is disconnected!");
    }


    private static PrintWriter getWriter(Socket socket) throws IOException {
        OutputStream outputStream = socket.getOutputStream();
        return new PrintWriter(outputStream);
    }

    private static Scanner getReader(Socket socket) throws IOException {
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

    private static void sendResponse(Client client, String response, Writer writer) throws IOException {
        writer.write(response);
        writer.write(System.lineSeparator());
        writer.flush();
    }
}