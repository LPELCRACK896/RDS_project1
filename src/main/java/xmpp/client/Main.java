package xmpp.client;

import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main( String[] args )
    {
        Client client = new Client("fualp_test", "passwordpapa123", false,"alumchat.xyz", "146.190.213.97", true);
        //System.out.println(client.getSupportedFeatures());

        // Initialize message listener

        List<List<String>> groups = client.listAvailableGroups();
        String mygroup_id = groups.get(0).get(0);
        String my_group_name = groups.get(0).get(1);
        Scanner scanner = new Scanner(System.in);
        boolean exit = false;

        client.sendFile("fualp2@alumchat.xyz");

        scanner.close();
    }
}
