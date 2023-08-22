package xmpp.client;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;

public class Main {
    public static void main( String[] args )
    {

        Client client = new Client("fualp_test", "passwordpapa123", false,"alumchat.xyz", "146.190.213.97", true);
        client.addContact("fualp2@alumchat.xyz", "mi otro yo", null);
        //client.removeContact("fualp2@alumchat.xyz");
        client.printRosterEntries();


    }
}
