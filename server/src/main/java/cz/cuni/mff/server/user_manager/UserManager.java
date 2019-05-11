package cz.cuni.mff.server.user_manager;

import cz.cuni.mff.server.structure.AuthenticatorStructure;
import cz.cuni.mff.server.structure.Structure;
import cz.cuni.mff.server.structure.StructureException;
import cz.cuni.mff.server.structure.files.PasswdRecord;
import cz.cuni.mff.server.user_manager.users.AdminUser;
import cz.cuni.mff.server.user_manager.users.BaseUser;
import cz.cuni.mff.server.user_manager.users.User;

public class UserManager {
    private static UserManager singleton;
    private AuthenticatorStructure storage;

    private UserManager(AuthenticatorStructure s) {
        this.storage = s;
    }

    public static UserManager getUserManager() {
        if (UserManager.singleton == null) {
            UserManager.singleton = new UserManager(Structure.getStructure());
        }
        return UserManager.singleton;
    }

    public void addDefaultAdminUser(String username, String password) throws StructureException {
        storage.addUser(username, password, true);
    }

    public void addDefaultUser(String username, String password) throws StructureException {
        storage.addUser(username, password, false);
    }

    /**
     * Authenticates given user.
     * @param username username
     * @param password password
     * @return Either Admin or BaseUser or null if authentication fails.
     */
    public User authenticate(String username, String password) {
        PasswdRecord pr = storage.getUserPasswd(username);
        if (pr == null) {
            return null;
        }
        if (pr.hashedPasswd.equals(Authenticator.hashPasswd(password, pr.salt))) {
            if (storage.isAdmin(username)) {
                return new AdminUser(username);
            } else {
                return new BaseUser(username);
            }
        }
        return null;
    }
}
