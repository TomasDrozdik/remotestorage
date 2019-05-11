package cz.cuni.mff.server.structure;

import cz.cuni.mff.server.structure.files.HomeDir;
import cz.cuni.mff.server.structure.files.UserProperty;
import cz.cuni.mff.server.user_manager.users.BaseUser;

public interface UserStructure {
    /**
     * Get users home folder.
     *
     * @param u user
     * @return non null HomeDir
     */
    HomeDir getHome(BaseUser u);

    /**
     * Get user property for given user.
     *
     * @param u user
     * @return non null UserProperty
     */
    UserProperty getUserProperty(BaseUser u);

}
