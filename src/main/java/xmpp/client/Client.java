package xmpp.client;

import org.jivesoftware.smack.AbstractXMPPConnection;
import  org.jxmpp.jid.EntityFullJid;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.SmackException;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.roster.RosterGroup;
import org.jivesoftware.smack.tcp.XMPPTCPConnection;
import org.jivesoftware.smack.chat2.Chat;
import org.jivesoftware.smack.chat2.ChatManager;
import org.jivesoftware.smack.roster.Roster;
import org.jivesoftware.smack.roster.RosterEntry;
import org.jivesoftware.smack.tcp.XMPPTCPConnectionConfiguration;
import org.jivesoftware.smackx.disco.ServiceDiscoveryManager;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo;
import org.jivesoftware.smackx.disco.packet.DiscoverInfo.Feature;
import org.jivesoftware.smackx.iqregister.AccountManager;
import org.jxmpp.jid.DomainBareJid;
import org.jxmpp.jid.EntityBareJid;
import org.jxmpp.jid.BareJid;
import org.jxmpp.jid.Jid;
import org.jxmpp.jid.impl.JidCreate;
import org.jxmpp.jid.parts.Localpart;
import org.jxmpp.jid.parts.Resourcepart;
import org.jxmpp.stringprep.XmppStringprepException;
import org.jivesoftware.smackx.muc.MultiUserChat;
import org.jivesoftware.smackx.muc.MucEnterConfiguration;
import org.jivesoftware.smackx.muc.MultiUserChatManager;
import org.jivesoftware.smackx.muc.HostedRoom;
import org.minidns.record.A;

import java.io.IOException;
import java.nio.file.Files;
import java.io.File;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Base64;
import java.util.logging.Logger;
import javax.swing.JFileChooser;


import org.jivesoftware.smackx.filetransfer.FileTransferManager;
import org.jivesoftware.smackx.filetransfer.OutgoingFileTransfer;
import org.jivesoftware.smackx.filetransfer.FileTransfer.Status;

import org.jivesoftware.smackx.httpfileupload.HttpFileUploadManager;
import org.jivesoftware.smackx.httpfileupload.element.Slot;
import org.jivesoftware.smackx.httpfileupload.UploadProgressListener;


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
    private static final Logger LOGGER = Logger.getLogger(ChatManager.class.getName());

    /**
     *
     * @param username JID must include username and domain
     * @param password JID password, in case of login expects the
     * @param isNew Boolean indicates if user is new, so itll register or already exists, so it will only login.
     * @param domain Server domain
     * @param ip Server IP
     * @param globalShowLogs Boolean indicates if it displays some logs related with some actions related to connection.
     */
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
            } else {
                login(true);
            }
            this.chatManager = ChatManager.getInstanceFor(connection);
            initMessageListener();
        }
    }

    /**
     * To create inicital connection that will allow to do queries with server and other clients thorugh server.
     * @param domain Server domain
     * @param ip Server IP
     * @return Connection that will use all along the objects life.
     */
    private AbstractXMPPConnection createConnection(String domain, String ip) {
        System.out.println("Comienza a crear conexion");
        try {
            XMPPTCPConnectionConfiguration config = XMPPTCPConnectionConfiguration.builder()
                    .setXmppDomain(domain)
                    .setHost(ip)
                    .setPort(5222)
                    .setSecurityMode(ConnectionConfiguration.SecurityMode.disabled)
                    //.enableDefaultDebugger()
                    .build();
            System.out.println("Termina creacion de conexion");
            return new XMPPTCPConnection(config);
        } catch (XmppStringprepException e) {
            System.err.println("Error creating XMPP connection: " + e.getMessage());
            return null;  // or you can throw a RuntimeException to terminate the program
        }
    }

    /**
     * Resgister using Client credentials.
     * @param showLogs Local variable to show logs related to either error or success message during register proccess.
     * @param onSuccessLogin Boolean to indicate if it wants to log in after register,
     */
    public void registerAccount(boolean showLogs, boolean onSuccessLogin) {
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

    /**
     * Login using the Client credentials user, password.
     * @param showLogs Boolean to indicate if show logs related to login (either errors or success messages) during log in proccess.
     */
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

    /**
     * To get all contacts.
     * @return String with all contancts of current logged user.
     */
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
            strRoaster.append("User JID: ").append(entry.getJid()).append("\n").append("Name: ").append(entry.getName()).append("\n");
            Presence presence = roster.getPresence(entry.getJid());
            if (presence != null) {
                strRoaster.append("Tipo de presencia: ").append(presence.getType()).append("\n").append("Modo: ").append(presence.getMode()).append("\n").append("Estado: ").append(presence.getStatus()).append("\n");
            }
            strRoaster.append("Subcripción confirmada: ").append(!entry.isSubscriptionPending()).append("\n").append("\n-------------\n");
        }
        if (globalShowLogs) System.out.println(strRoaster);
        return strRoaster.toString();
    }

    /**
     *
     */
    public void logout() {
        if (connection != null && connection.isConnected()) {
            connection.disconnect();
            System.out.println("Logged out successfully!");
            this.isLoggedIn = false;
        }
    }

    /**
     * Delete account, using current logged user.
     */
    public void deleteAccount() {
        if (connection == null || !connection.isConnected()) {
            System.err.println("You must be connected to delete your account!");
            return;
        }

        try {
            AccountManager accountManager = AccountManager.getInstance(connection);
            accountManager.deleteAccount();
            System.out.println("Account deleted successfully!");
        } catch (XMPPException | SmackException | InterruptedException e) {
            System.err.println("Error deleting account: " + e.getMessage());
        }
    }

    /**
     *
     * @param userJID User JID to add to roaster.
     * @param nickname Nickname to save the new user in roaster.
     * @param groups Group to add, might be null.
     */
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

    /**
     *
     * @param userJID UserJID to show details.
     * @return String with all data related with contact.
     */
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

    /**
     * Remove contact
     * @param userJID user JID to remove
     */
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

    /***
     * remove user from roaster and delete subscription.
     * @param userJID UserJID to remove from roaster, must include dmoain.
     */
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

    public void sendMessage(String toJID, String messageContent) {
        if (chatManager == null) {
            ChatManager.getInstanceFor(connection);
        }
        try {
            Chat chat = chatManager.chatWith(JidCreate.entityBareFrom(toJID));
            chat.send(messageContent);
        } catch (SmackException.NotConnectedException | InterruptedException | XmppStringprepException e) {
            System.err.println("Error sending message: " + e.getMessage());
        }
    }
    public void initMessageListener() {
        chatManager.addIncomingListener((from, message, chat) -> {
            System.out.println("holla");
            System.out.println("Received message from " + from + ": " + message.getBody());
        });
    }
    public void joinGroup(String roomJID, String nickname, String password) {
        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
            EntityBareJid roomBareJid = JidCreate.entityBareFrom(roomJID);
            MultiUserChat muc = mucManager.getMultiUserChat(roomBareJid);

            if (password != null && !password.trim().isEmpty()) {
                muc.join(Resourcepart.from(nickname), password);
            } else {
                muc.join(Resourcepart.from(nickname));
            }

            System.out.println("Joined group " + roomJID + " as " + nickname);
        } catch (Exception e) {
            System.err.println("Error joining group: " + e.getMessage());
        }
    }
    public void sendMessageToGroup(String roomName, String messageContent) {
        if (connection == null || !connection.isConnected()) {
            System.err.println("Debes estar conectado para enviar un mensaje a un grupo.");
            return;
        }
        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);
            MultiUserChat muc = mucManager.getMultiUserChat(JidCreate.entityBareFrom(roomName));
            muc.sendMessage(messageContent);
        } catch (Exception e) {
            System.err.println("Error al enviar mensaje al grupo: " + e.getMessage());
        }
    }
    public List<List<String>> listAvailableGroups() {
        List<List<String>> roomList = new ArrayList<>();

        try {
            MultiUserChatManager mucManager = MultiUserChatManager.getInstanceFor(connection);

            // Obtiene los dominios que ofrecen el servicio de chat multiusuario
            List<DomainBareJid> mucServiceDomains = mucManager.getMucServiceDomains();

            for (DomainBareJid domain : mucServiceDomains) {
                // Obtiene las salas alojadas en el dominio específico
                Map<EntityBareJid, HostedRoom> rooms = mucManager.getRoomsHostedBy(domain);

                for (Map.Entry<EntityBareJid, HostedRoom> entry : rooms.entrySet()) {
                    HostedRoom hostedRoom = entry.getValue();
                    List<String> room = new ArrayList<String>();
                    room.add(hostedRoom.getJid().toString());
                    room.add(hostedRoom.getName());
                    roomList.add(room);
                }
            }

        } catch (Exception e) {
            System.err.println("Error listing available groups: " + e.getMessage());
        }

        return roomList;
    }

    public void setCredentials(String username, String password) {
        setUsername(username);
        setPassword(password);
    }

    /**
     * Password setter.
     * @param password Password related to JID in XMPP server.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * Username setter.
     * @param username JID to login or register in XMPP server.
     */
    public void setUsername(String username) {
        this.username = username;
    }
//    public String uploadFile(File file) {
//        if (connection == null || !connection.isConnected()) {
//            System.err.println("Debes estar conectado para subir un archivo.");
//            return null;
//        }
//
//        HttpFileUploadManager httpFileUploadManager = HttpFileUploadManager.getInstanceFor(connection);
//        try {
//            Slot slot = httpFileUploadManager.requestSlot(file.getName(), file.length(), "image/png");  // Cambia "image/png" al tipo MIME adecuado si es diferente.
//
//            UploadProgressListener uploadProgressListener = new UploadProgressListener() {
//                @Override
//                public void onUploadProgress(long uploadedBytes, long totalBytes) {
//                    System.out.printf("Uploaded: %d of %d bytes\n", uploadedBytes, totalBytes);
//                }
//            };
//
//            // Sube el archivo al servidor.
//            httpFileUploadManager.uploadFile(file, slot, uploadProgressListener);
//
//            // Retorna la URL de descarga del archivo.
//            return slot.getGetUrl().toString();
//        } catch (Exception e) {
//            System.err.println("Error al subir el archivo: " + e.getMessage());
//            return null;
//        }

//    }
}