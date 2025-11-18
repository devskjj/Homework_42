package Utility;

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

        try (socket; Scanner reader = client.getIn(); PrintWriter writer = client.getOut()) {
            sendResponse(client.getNickname() + ", " + "Hello from Server " + socket.getPort(), writer);
            while (true) {
                String input = reader.nextLine().trim();
                if (isEmptyMsg(input) || isQuitMsg(input)) {
                    sendResponse("You are disconnected!", writer);
                    break;
                }

                Commands cmd = Commands.fromInput(input);
                try {
                    cmd.execute(input, writer, client, clients);
                } catch (NullPointerException e) {
                    Utility.sendToAnotherClients(input, client);
                }
                System.out.printf("Got message: %s%n", input);
            }
        } catch (NoSuchElementException e) {
            System.out.println("Client dropped connection!");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            client.setConnected(false);
            clients.remove(client);
            sendToAnotherClients("Client " + client.getNickname() + " is disconnected!", client);
        }
    }

    private static boolean isQuitMsg(String msg) {
        return "bye".equalsIgnoreCase(msg);
    }

    private static boolean isEmptyMsg(String msg) {
        return msg == null || msg.isBlank();
    }

    public static void sendResponse(String response, Writer writer) {
        try {
            writer.write(response);
            writer.write(System.lineSeparator());
            writer.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void sendToAnotherClients(String msg, Client client) {
        for (Client c : clients) {
            if (!c.getNickname().equals(client.getNickname())) {
                if (c.isConnected()) {
                    c.sendMsg(client.getNickname() + ": " + msg);
                }
            }
        }
    }
}