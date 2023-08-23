package xmpp.client;

import java.util.List;
import java.util.Scanner;

public class ClientMenu {

    private Client client;

    public ClientMenu(Client client) {
        this.client = client;
    }

    public void showMenu() {
        while (true) {
            System.out.println("--------- XMPP Client Menu ---------");
            System.out.println("1. Login");
            System.out.println("2. Logout");
            System.out.println("3. Register Account");
            System.out.println("4. Delete Account");
            System.out.println("5. Send Message");
            System.out.println("6. Join Group");
            System.out.println("7. Send Message to Group");
            System.out.println("8. List Available Groups");
            System.out.println("9. Show roaster");
            System.out.println("9. Exit");
            System.out.print("Enter your choice: ");

            Scanner scanner = new Scanner(System.in);
            int choice = scanner.nextInt();

            switch (choice) {
                case 1:
                    System.out.print("Enter Username: ");
                    String username = scanner.next();
                    System.out.print("Enter Password: ");
                    String password = scanner.next();
                    client.setCredentials(username, password);
                    client.login(true);
                    break;
                case 2:
                    client.logout();
                    break;
                case 3:
                    System.out.print("Enter Username: ");
                    String username_r = scanner.next();
                    System.out.print("Enter Password: ");
                    String password_r = scanner.next();
                    System.out.println("¿Desea loggearse tras registrarse? S/N");
                    String choice_r = scanner.nextLine();
                    boolean logginThen = choice_r.equalsIgnoreCase("S");
                    client.setCredentials(username_r, password_r);
                    client.registerAccount(true, logginThen);
                    break;
                case 4:
                    client.deleteAccount();
                    break;
                case 5:
                    System.out.print("Enter JID of the recipient: ");
                    String toJID = scanner.next();
                    System.out.print("Enter your message: ");
                    String message = scanner.next();
                    client.sendMessage(toJID, message);
                    break;
                case 6:
                    System.out.print("Enter Room JID: ");
                    String roomJID = scanner.next();
                    System.out.print("Enter Nickname: ");
                    String nickname = scanner.next();
                    client.joinGroup(roomJID, nickname, "");
                    break;
                case 7:
                    System.out.print("Enter Room Name: ");
                    String roomName = scanner.next();
                    System.out.print("Enter your message: ");
                    String groupMessage = scanner.next();
                    client.sendMessageToGroup(roomName, groupMessage);
                    break;
                case 8:
                    List<List<String>> groups = client.listAvailableGroups();
                    System.out.println("Available Groups:");
                    for (List<String> group : groups) {
                        System.out.println("JID: " + group.get(0) + ", Name: " + group.get(1));
                    }
                    break;
                case 9:
                    client.printRosterEntries();
                case 10:

                case 20:
                    System.out.println("Exiting...");
                    System.exit(0);
                    break;
                default:
                    System.out.println("Invalid choice, please try again.");
            }
        }
    }
}