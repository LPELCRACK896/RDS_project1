package xmpp.client;

import  org.jivesoftware.smackx.carbons.*;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Feature;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.AbstractXMPPConnection;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smackx.carbons.CarbonManager;
import org.jxmpp.jid.Jid;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.packet.Presence;

import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.chat2.Chat;
import org.jxmpp.jid.parts.Localpart;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.jivesoftware.smackx.carbons.CarbonManager;

import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.impl.JidCreate;

import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
public class Client {
    private String username;
    private String password;
    private boolean isNew;
    private AbstractXMPPConnection connection;
    private String domain;
    private String ip;
    private boolean globalShowLogs;
    private boolean isLoggedIn;
    private Map<BareJid, List<String>> chatHistory = new HashMap<>();


    private ChatManager chatManager;

    public Client(String username, String password, boolean isNew, String domain, String ip, boolean globalShowLogs) {
        this.globalShowLogs = globalShowLogs;
        this.username = username;
        this.password = password;
        this.isNew = isNew;
        this.domain = domain;
        this.connection = createConnection(domain, ip);
        this.chatManager = null;
        this.ip = ip;
        this.isLoggedIn = false;

        if (connection != null) {
            if (isNew) {
                registerAccount(true, true);
            }else {
                login(true);
            }
            this.chatManager = ChatManager.getInstanceFor(connection);


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

    public String printRosterEntries() {
        Roster roster = Roster.getInstanceFor(connection);
        StringBuilder strRoaster = new StringBuilder();

        // Ensure the roster is loaded before accessing it
        try {
            roster.reloadAndWait();
        } catch (Exception e) {
            System.err.println("Error loading roster: " + e.getMessage());
            return "";
        }

        // Print all entries in the roster
        strRoaster.append("=====Roaster=====\n");
        for (RosterEntry entry : roster.getEntries()) {
            strRoaster.append("User JID: ").append(entry.getJid()).append("\n");
            Presence presence = roster.getPresence(entry.getJid());
            if (presence != null) {
                strRoaster.append("Estado: ").append(presence.getType()).append("\n").append("Modo: ").append(presence.getMode()).append("\n");
            }
            strRoaster.append("Subcripción confirmada: ").append(!entry.isSubscriptionPending()).append("\n").append("\n-------------\n");
        }
        if (globalShowLogs) System.out.println(strRoaster);
        return strRoaster.toString();
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

    public void addContact(String userJID, String nickname, String[] groups) {
        Roster roster = Roster.getInstanceFor(connection);
        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                System.err.println("Error reloading roster: " + e.getMessage());
                return;
            }
        }
        try {
            BareJid jid = JidCreate.bareFrom(userJID);
            roster.createItemAndRequestSubscription(jid, nickname, groups);
            System.out.println("Contact added and subscription requested successfully!");
        } catch (Exception e) {
            System.err.println("Error adding contact: " + e.getMessage());
        }
    }

    public String showContactDetails(String userJID) {
        Roster roster = Roster.getInstanceFor(connection);
        StringBuilder detailsBuilder = new StringBuilder();

        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                detailsBuilder.append("Error reloading roster: ").append(e.getMessage()).append("\n");
                return detailsBuilder.toString();
            }
        }

        try {
            BareJid jid = JidCreate.bareFrom(userJID);
            RosterEntry entry = roster.getEntry(jid);

            if (entry != null) {
                detailsBuilder.append("Details for contact: ").append(userJID).append("\n");
                detailsBuilder.append("User JID: ").append(entry.getJid()).append("\n");
                detailsBuilder.append("Name: ").append(entry.getName()).append("\n");
                detailsBuilder.append("Type: ").append(entry.getType()).append("\n");

                Presence presence = roster.getPresence(entry.getJid());
                if (presence != null) {
                    detailsBuilder.append("Estado: ").append(presence.getType()).append("\n");
                    detailsBuilder.append("Modo: ").append(presence.getMode()).append("\n");
                }

                detailsBuilder.append("Subscription Status: ").append(entry.getType()).append("\n");
                detailsBuilder.append("Groups: \n");
                for (RosterGroup group : entry.getGroups()) {
                    detailsBuilder.append("- ").append(group.getName()).append("\n");
                }

            } else {
                detailsBuilder.append("No details found for user: ").append(userJID).append("\n");
            }
        } catch (Exception e) {
            detailsBuilder.append("Error fetching contact details: ").append(e.getMessage()).append("\n");
        }
        if (globalShowLogs) System.out.println(detailsBuilder);
        return detailsBuilder.toString();
    }

    public void removeContact(String userJID) {
        Roster roster = Roster.getInstanceFor(connection);

        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                System.err.println("Error reloading roster: " + e.getMessage());
                return;
            }
        }

        try {
            BareJid jid = JidCreate.bareFrom(userJID);
            RosterEntry entry = roster.getEntry(jid);

            if (entry != null) {
                roster.removeEntry(entry);
                System.out.println("Contact removed successfully!");
            } else {
                System.out.println("No contact found with the provided JID: " + userJID);
            }
        } catch (Exception e) {
            System.err.println("Error removing contact: " + e.getMessage());
        }
    }

    public void removeContactAndUnsubscribe(String userJID) {
        Roster roster = Roster.getInstanceFor(connection);

        if (!roster.isLoaded()) {
            try {
                roster.reloadAndWait();
            } catch (Exception e) {
                System.err.println("Error reloading roster: " + e.getMessage());
                return;
            }
        }

        try {
            BareJid jid = JidCreate.bareFrom(userJID);
            RosterEntry entry = roster.getEntry(jid);

            if (entry != null) {
                Presence unsubscribe = new Presence(Presence.Type.unsubscribe);
                unsubscribe.setTo(jid);
                connection.sendStanza(unsubscribe);

                roster.removeEntry(entry);

                System.out.println("Contact removed and unsubscribed successfully!");
            } else {
                System.out.println("No contact found with the provided JID: " + userJID);
            }
        } catch (Exception e) {
            System.err.println("Error removing contact and unsubscribing: " + e.getMessage());
        }
    }


    //public void loadChatHistory(BareJid contactJid) {
      //  List<String> messages = fetchHistoryFromServer(contactJid);
        //chatHistory.put(contactJid, messages);
    //}

    public void sendMessage(String toJID, String messageContent)  {
        if (chatManager == null){
            ChatManager.getInstanceFor(connection);
        }
        try {
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(toJID));
            chat.send(messageContent);
        } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    public void enableCarbons() {
        if (connection == null || !connection.isConnected() || !isLoggedIn) {
            System.err.println("Debe estar conectado y autenticado para habilitar Carbons.");
            return;
        }

        try {
            CarbonManager carbonManager = CarbonManager.getInstanceFor(connection);
            if (!carbonManager.isSupportedByServer()) {
                System.out.println("El servidor no admite Carbons.");
                return;
            }

            carbonManager.enableCarbons();
            System.out.println("Carbons habilitado.");
        } catch (XMPPException | SmackException | InterruptedException e) {
            System.err.println("Error al habilitar Carbons: " + e.getMessage());
        }
    }

    public void addCarbonListener() {
        org.jivesoftware.smackx.carbons.CarbonManager carbonManager = org.jivesoftware.smackx.carbons.CarbonManager.getInstanceFor(connection);

        carbonManager.addCarbonCopyReceivedListener(new onCarbonCopyReceived() {
            @Override
            public void onCarbonCopyReceived(Direction direction, Message carbonCopy, Message wrappingMessage) {
                // Aquí manejas el evento de recepción de una copia de carbono.

                // Por ejemplo, puedes imprimir el contenido del mensaje:
                System.out.println("Dirección: " + direction);
                System.out.println("Copia de Carbono: " + carbonCopy.getBody());
                System.out.println("Mensaje envolvente: " + wrappingMessage.getBody());
            }
        });
    }
    public void initMessageListener() {
        if (chatManager == null){
            ChatManager.getInstanceFor(connection);
        }
        chatManager.addIncomingListener((from, message, chat) -> {
            System.out.println("Received message from " + from + ": " + message.getBody());
        });
    }

    public List<Feature> getSupportedFeatures() {
        try {
            // Asume que 'connection' es tu conexión XMPP activa.
            Jid serverJid = connection.getXMPPServiceDomain(); // Obtener JID del servidor.

            ServiceDiscoveryManager discoManager = ServiceDiscoveryManager.getInstanceFor(connection);
            DiscoverInfo discoverInfo = discoManager.discoverInfo(serverJid);

            return discoverInfo.getFeatures();
        } catch (Exception e) {
            System.err.println("Error al obtener características del servidor: " + e.getMessage());
            return null;
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
