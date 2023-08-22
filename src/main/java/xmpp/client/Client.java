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
    private String ip;

    private boolean isLoggedIn;

    public Client(String username, String password, boolean isNew, String domain, String ip) {
        this.username = username;
        this.password = password;
        this.isNew = isNew;
        this.domain = domain;
        this.connection = createConnection(domain, ip);
        this.ip = ip;
        this.isLoggedIn = false;

        if (connection != null) {
            if (isNew) {
                registerAccount(true, true);
            }else {
                login(true);
            }

        }
    }

    private AbstractXMPPConnection createConnection(String domain, String ip) {
        System.out.println("Comienza a crear conexion");
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(ip)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    .build();
            System.out.println("Termina creacion de conexion");
            return new XMPPTCPConnection(config);
        } catch (XmppStringprepException e) {
            System.err.println("Error creating XMPP connection: " + e.getMessage());
            return null;  // or you can throw a RuntimeException to terminate the program
        }
    }

    private void registerAccount(boolean showLogs, boolean onSuccessLogin) {
        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            if (!connection.isConnected()) {
                connection.connect();
            }
            accountManager.sensitiveOperationOverInsecureConnection(true);
            accountManager.createAccount(Localpart.from(username), password);
            if (showLogs) System.out.println("Account registered successfully!");
            if (onSuccessLogin) login(false);

        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            if (showLogs) System.err.println("Error registering account: " + e.getMessage());
        }
    }


    public void login(boolean showLogs) {
        try {
            if (!connection.isConnected()) {
                connection.connect();
            }
            connection.login(username, password);
            this.isLoggedIn = true;

            if (showLogs) System.out.println("Logged in successfully!");
        } catch (XMPPException | SmackException | InterruptedException | IOException e) {
            if (showLogs) System.err.println("Error logging in: " + e.getMessage());
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
    public void logout() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Logged out successfully!");
            this.isLoggedIn = false;
        }
    }
    public void deleteAccount() {
        if (connection == null || !connection.isConnected()) {
            System.err.println("You must be connected to delete your account!");
            return;
        }

        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            System.out.println("Account deleted successfully!");
        } catch (XMPPException | SmackException |InterruptedException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
    }


    public void setCredentials(String username, String password){
        setUsername(username);
        setPassword(password);
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }


}
