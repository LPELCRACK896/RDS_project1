package org.xmpp_chat;

import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;

public class MyAccountManager {
    private final AccountManager myAccountManager;
    public MyAccountManager(XMPPConnection connection) {
        this.myAccountManager = myAc.getInstance(connection);
    }

    public void registerAccount(String username, String password) throws Exception {
            myAccountManager.createAccount(username, password);
        }

        public void deleteAccount() throws Exception {
            myAccountManager.deleteAccount();
        }

}
