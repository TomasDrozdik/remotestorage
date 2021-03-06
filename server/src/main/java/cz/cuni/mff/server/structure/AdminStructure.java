package cz.cuni.mff.server.structure;

public interface AdminStructure {
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
