package Enum;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public enum Commands {
    DATE {
        @Override
        public void execute(String input, PrintWriter printWriter, Socket socket) {
            printWriter.println(LocalDate.now());
        }
    },
    TIME {
        @Override
        public void execute(String input, PrintWriter printWriter, Socket socket) {
            printWriter.println(LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss")));
        }
    },
    REVERSE {
        @Override
        public void execute(String input, PrintWriter printWriter, Socket socket) {
            String output = new StringBuilder(input).reverse().toString();
            printWriter.println(output);
        }
    },
    UPPER {
        @Override
        public void execute(String input, PrintWriter printWriter, Socket socket) {
            printWriter.println(input.toUpperCase());
        }
    },
    BYE {
        @Override
        public void execute(String input, PrintWriter printWriter, Socket socket) {
            try {
                socket.close();
            } catch (IOException e) {
                System.out.println("Вероятнее всего процесс занят.");
            }
        }
    };

    public abstract void execute(String input, PrintWriter printWriter, Socket socket);
}
