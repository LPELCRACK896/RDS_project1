package xmpp.client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jxmpp.jid.parts.Localpart;
import java.io.IOException;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
public class Client {
    private String username;
    private String password;
    private boolean isNew;
    private AbstractXMPPConnection connection;
    private String domain;

    public Client(String username, String password, boolean isNew, String domain) {
        this.username = username;
        this.password = password;
        this.isNew = isNew;
        this.domain = domain;
        this.connection = createConnection(domain);

        if (connection != null) {
            if (isNew) {
                registerAccount();
            } else {
                login();
            }
        }
    }

    private AbstractXMPPConnection createConnection(String domain) {
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();
            return new XMPPTCPConnection(config);
        } catch (XmppStringprepException e) {
            System.err.println("Error creating XMPP connection: " + e.getMessage());
            return null;  // or you can throw a RuntimeException to terminate the program
        }
    }

    private void registerAccount() {
        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            connection.connect();
            accountManager.sensitiveOperationOverInsecureConnection(true);  // this should only be done for testing on servers without SSL
            accountManager.createAccount(Localpart.from(username), password);
            System.out.println("Account registered successfully!");
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.err.println("Error registering account: " + e.getMessage());
        }
    }

    private void login() {
        try {
            connection.connect();
            connection.login(username, password);
            System.out.println("Logged in successfully!");
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            System.err.println("Error logging in: " + e.getMessage());
        }
    }

    public void printRosterEntries() {
        Roster roster = Roster.getInstanceFor(connection);

        // Ensure the roster is loaded before accessing it
        try {
            roster.reloadAndWait();
        } catch (Exception e) {
            System.err.println("Error loading roster: " + e.getMessage());
            return;
        }

        // Print all entries in the roster
        for (RosterEntry entry : roster.getEntries()) {
            System.out.println("User JID: " + entry.getJid());
            System.out.println("Name: " + entry.getName());
            System.out.println("Type: " + entry.getType());
            //System.out.println("Status: " + entry.getStatus());
            System.out.println("-------------");
        }
    }
}
