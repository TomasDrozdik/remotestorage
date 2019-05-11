package cz.cuni.mff.server.structure;

import cz.cuni.mff.server.structure.files.PasswdRecord;

public interface AuthenticatorStructure {
    /**
     * Get hashed passwd for given user.
     *
     * @param username username
     * @return hashed password of given user
     */
    PasswdRecord getUserPasswd(String username);

    /**
     * Is given user a admin?
     *
     * @param username username
     * @return whether given user is admin
     */
    boolean isAdmin(String username);

    /**
     * Add user to the structure.
     *
     * @param username username
     * @param password password
     * @param isAdmin  bool specifying admin privileges
     * @return ReturnValue enum specifying possible states
     * @throws StructureException
     */
    Structure.ReturnValue addUser(String username, String password, boolean isAdmin) throws StructureException;
}
