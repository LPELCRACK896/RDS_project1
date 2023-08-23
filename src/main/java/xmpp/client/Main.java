package xmpp.client;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main( String[] args )
    {

        Scanner scanner = new Scanner(System.in);
        System.out.println("¿Desea ingresar con usuario nuevo? S/N");
        String choice = scanner.nextLine();

        boolean isNew = choice.equalsIgnoreCase("S");


        System.out.println("Ingresar usuario:");
        String username = scanner.nextLine();
        System.out.println("Ingresar contraseña:");
        String password = scanner.nextLine();


        Client client = new Client(username, password, isNew,"alumchat.xyz", "146.190.213.97", true);
        ClientMenu clientMenu = new ClientMenu(client);
        clientMenu.showMenu();


    }
}
