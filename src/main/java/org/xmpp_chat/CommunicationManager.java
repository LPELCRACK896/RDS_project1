import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;

import java.util.Set;

public class CommunicationManager {
    private final ChatManager chatManager;
    private final Roster roster;

    public CommunicationManager(XMPPConnection connection) {
        this.chatManager = ChatManager.getInstanceFor(connection);
        this.roster = Roster.getInstanceFor(connection);
    }

    public void displayContacts() {
        Set<RosterEntry> entries = roster.getEntries();
        for (RosterEntry entry : entries) {
            System.out.println(entry.getName() + " - " + entry.getStatus());
        }
    }

    public Chat startChat(String userJID) {
        return chatManager.chatWith(userJID);
    }
}
