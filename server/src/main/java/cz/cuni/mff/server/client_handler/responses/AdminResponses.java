package cz.cuni.mff.server.client_handler.responses;

import remotestorage.proto.MessagesProtos.ServerResponse;
import cz.cuni.mff.server.structure.Structure;
import cz.cuni.mff.server.structure.StructureException;
import cz.cuni.mff.server.user_manager.users.AdminUser;

import static cz.cuni.mff.server.client_handler.responses.ResponseBuilder.buildNegativeResponse;
import static cz.cuni.mff.server.client_handler.responses.ResponseBuilder.buildPositiveResponse;

public class AdminResponses {

    /* Static only class */
    private AdminResponses() {}

    /**
     * Adds user to the database.
     * @param u user requesting operation
     * @param username username of new user
     * @param password password of new user
     * @param isAdmin admin flag of new user
     * @return appropriate ServerResponse
     */
    public static ServerResponse addUser(AdminUser u, String username, String password,
                                                        boolean isAdmin) {
        try {
            Structure.ReturnValue rv = u.getStorage().addUser(username, password, isAdmin);
            switch (rv) {
                case OK:
                    return buildPositiveResponse(null);
                case ALREADY_EXISTS:
                    return buildNegativeResponse(ServerResponse.ErrorType.ALREADY_EXISTS);
                default:
                    return buildNegativeResponse(ServerResponse.ErrorType.OPERATION_FAIL);
            }
        }
        catch (StructureException e) {
            return buildNegativeResponse(ServerResponse.ErrorType.OPERATION_FAIL);
        }
    }



}
