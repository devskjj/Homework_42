package Enum;

import Entity.Client;
import Utility.Utility;

import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

public enum Commands {
    DATE("/date") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            printWriter.println(LocalDate.now());
            printWriter.flush();
        }
    },

    TIME("/time") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            printWriter.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
            printWriter.flush();
        }
    },

    NAME("/name") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            String[] parts = input.split(" ", 2);

            if (parts.length < 2 || parts[1].isBlank()) {
                Utility.sendResponse("Введите имя без пробелов после команды.", printWriter);
                return;
            }

            String newName = parts[1];

            if (newName.contains(" ")) {
                Utility.sendResponse("Имя не должно содержать пробелов.", printWriter);
                return;
            }

            boolean notUniqueName = clients.stream().
                    anyMatch(c -> c.getNickname().equalsIgnoreCase(newName));

            if (notUniqueName) {
                Utility.sendResponse("Такое имя уже существует.", printWriter);
                return;
            }

            String oldName = client.getNickname();
            client.setNickname(newName);

            Utility.sendToAnotherClients("Пользователь " + oldName + " теперь известен как " + client.getNickname(), client);
            Utility.sendResponse("Вы теперь известны как " + client.getNickname(), printWriter);
        }
    },

    LIST("/list") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            clients.stream()
                    .filter(Client::isConnected)
                    .forEach(c -> printWriter.println(c.getNickname()));
            printWriter.flush();
        }
    },

    REVERSE("/reverse") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            String[] parts = input.split(" ", 2);

            if (parts.length < 2 || parts[1].isBlank()) {
                Utility.sendResponse("Введите /reverse сообщение", printWriter);
                return;
            }

            String output = new StringBuilder(parts[1]).reverse().toString();

            Utility.sendToAnotherClients(output, client);
            Utility.sendResponse(output, printWriter);
        }
    },

    WHISPER("/whisper") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            String[] parts = input.split(" ", 3);

            if (parts.length < 3 || parts[1].isBlank()) {
                Utility.sendResponse("Введите /whisper имя_пользователя сообщение", printWriter);
                return;
            }

            String targetName = parts[1];

            clients.stream()
                    .filter(c -> c.getNickname().equalsIgnoreCase(targetName))
                    .findFirst()
                    .ifPresentOrElse(c -> c.sendMsg("От " + client.getNickname() + ": " + parts[2]), () -> {
                                Utility.sendResponse("Пользователя с таким именем нет", printWriter);
                            }
                    );
        }
    },

    UPPER("/upper") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            String[] parts = input.split(" ", 2);

            if (parts.length < 2 || parts[1].isBlank()) {
                Utility.sendResponse("Введите /upper сообщение", printWriter);
                return;
            }

            Utility.sendToAnotherClients(parts[1].toUpperCase(), client);
            Utility.sendResponse(parts[1].toUpperCase(), printWriter);
        }
    };

    private final String cmd;

    Commands(String input) {
        this.cmd = input;
    }

    public abstract void execute(String input, PrintWriter printWriter, Client client, List<Client> clients);

    public static Commands fromInput(String input) {
        if (input == null || input.isBlank()) {
            return null;
        }

        String value = input.split(" ", 2)[0];

        for (Commands c : Commands.values()) {
            if (value.equalsIgnoreCase(c.cmd)) {
                return c;
            }
        }
        return null;
    }
}
