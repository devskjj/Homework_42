package Entity;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.Writer;
import java.net.Socket;
import java.util.Scanner;
import java.util.UUID;

public class Client {
    private final Socket socket;
    private String nickname;
    private final Scanner in;
    private final PrintWriter out;
    private boolean isConnected;

    public Client(Socket socket) throws IOException {
        this.socket = socket;
        this.nickname = "User-" + UUID.randomUUID().toString().substring(0, 5);
        this.in = new Scanner(socket.getInputStream());
        this.out = new PrintWriter(socket.getOutputStream());
    }

    public void sendMsg(String input) {
        out.println(input);
        out.flush();
    }

    public String getNickname() {
        return nickname;
    }


    public void setNickname(String input) {
        this.nickname = input;
    }

    public boolean isConnected() {
        return isConnected;
    }

    public void setConnected(boolean connected) {
        isConnected = connected;
    }


}
