package cz.cuni.mff.server.user_manager.users;

import cz.cuni.mff.server.structure.UserStructure;
import cz.cuni.mff.server.structure.Structure;
import cz.cuni.mff.server.structure.files.HomeDir;
import cz.cuni.mff.server.structure.files.UserProperty;

public class BaseUser extends User {
    private UserStructure storage;

    public BaseUser(String username) {
        this.username = username;
        this.storage = Structure.getStructure();
    }

    public HomeDir getHome() {
        return storage.getHome(this);
    }

    public UserProperty getProperty() {
        return storage.getUserProperty(this);
    }
}
