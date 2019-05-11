package cz.cuni.mff.server.user_manager.users;

import cz.cuni.mff.server.structure.AdminStructure;
import cz.cuni.mff.server.structure.Structure;

public class AdminUser extends User {
    private AdminStructure storage;

    public AdminUser(String username) {
        this.username = username;
        this.storage = Structure.getStructure();
    }

    public AdminStructure getStorage() {
        return storage;
    }
}
