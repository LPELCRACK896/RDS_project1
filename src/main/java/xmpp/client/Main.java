package xmpp.client;

import java.util.Scanner;

public class Main {
    public static void main( String[] args )
    {
        Client client = new Client("fualp_test", "passwordpapa123", false,"alumchat.xyz", "146.190.213.97", true);
        System.out.println(client.getSupportedFeatures());

        // Initialize message listener
        client.initMessageListener();

        Scanner scanner = new Scanner(System.in);
        boolean exit = false;
        while (!exit) {
            System.out.println("Enter a message to send to fualp2@alumnchat.xyz (or type 'exit' to quit):");
            String message = scanner.nextLine();

            if ("exit".equalsIgnoreCase(message)) {
                exit = true;
                continue;
            }
            client.sendMessage("fualp2@alumchat.xyz", message);
        }

        // Ensure you handle resource cleanup, like disconnecting from the server
        scanner.close();
    }
}
