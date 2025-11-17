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
                printWriter.println("Введите имя без пробелов после команды.");
                printWriter.flush();
                return;
            }

            String newName = parts[1];

            if (newName.contains(" ")) {
                printWriter.println("Имя не должно содержать пробелов.");
                printWriter.flush();
                return;
            }

            boolean notUniqueName = clients.stream().
                    anyMatch(c -> c.getNickname().equalsIgnoreCase(newName));

            if (notUniqueName) {
                printWriter.println("Такое имя уже существует.");
                printWriter.flush();
                return;
            }

            String oldName = client.getNickname();
            client.setNickname(newName);

            for (Client c : clients) {
                if (!c.getNickname().equals(client.getNickname())) {
                    if (c.isConnected()) {
                        c.sendMsg("Пользователь " + oldName + " теперь известен как " + client.getNickname());
                    }
                }
            }

            printWriter.printf("Вы теперь известны как %s%n", client.getNickname());
            printWriter.flush();
        }
    },
    LIST("/list") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            clients.forEach(c -> printWriter.println(c.getNickname()));
            printWriter.flush();
        }
    },
    REVERSE("/reverse") {
        @Override
        public void execute(String input, PrintWriter printWriter, Client client, List<Client> clients) {
            String output = new StringBuilder(input).reverse().toString();
            printWriter.println(output);
            printWriter.flush();
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
            printWriter.println(input.toUpperCase());
            printWriter.flush();
        }
    };

    private final String cmd;

    Commands(String input) {
        this.cmd = input;
    }

    public abstract void execute(String input, PrintWriter printWriter, Client client, List<Client> clients);

    public static Commands fromInput(String input) {
        if (input.toLowerCase().startsWith("/name ")) {
            return NAME;
        }

        if (input.toLowerCase().startsWith("/whisper ")) {
            return WHISPER;
        }

        for (Commands c : Commands.values()) {
            if (input.equalsIgnoreCase(c.cmd)) {
                return c;
            }
        }
        return null;
    }
}
