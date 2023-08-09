import org.jivesoftware.smack.AccountManager;
import org.jivesoftware.smack.XMPPConnection;

public class MyAccountManager {
    private final AccountManager accountManager;

    public MyAccountManager(XMPPConnection connection) {
        this.accountManager = AccountManager.getInstance(connection);
    }

    public void registerAccount(String username, String password) throws Exception {
        accountManager.createAccount(username, password);
    }

    public void deleteAccount() throws Exception {
        accountManager.deleteAccount();
    }
}
