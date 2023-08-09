package org.xmpp_chat;
import org.xmpp_chat.CLI;
import java.io.IOException;

public class Main {
    public static void main(String[] args) {
        CLI cli = new CLI();
        try {
            cli.run();
        } catch (IOException e) {
            System.err.println("Error en la ejecución: " + e.getMessage());
        }
    }
}
