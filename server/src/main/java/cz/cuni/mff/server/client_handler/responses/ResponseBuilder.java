package cz.cuni.mff.server.client_handler.responses;

import remotestorage.proto.MessagesProtos;
import remotestorage.proto.MessagesProtos.FileTransfer;
import remotestorage.proto.MessagesProtos.ServerResponse;

import java.util.List;

public class ResponseBuilder {

    /**
     * Builds NACK invalid request response.
     * @return ServerResponse
     */
    public static ServerResponse invalidRequest() {
        return buildNegativeResponse(ServerResponse.ErrorType.INVALID_REQUEST);
    }

    /**
     * Builds NACK response with given error type
     * @param type error type
     * @return ServerResponse
     */
    public static ServerResponse buildNegativeResponse(ServerResponse.ErrorType type) {
        ServerResponse.Builder b = ServerResponse.newBuilder();
        b.setAck(false);
        b.setError(type);
        return b.build();
    }

    /**
     * Builds ACK response with given string message.
     * @param args message, may be null -> empty
     * @return ServerResponse
     */
    public static ServerResponse buildPositiveResponse(List<String> args) {
        ServerResponse.Builder b = ServerResponse.newBuilder();
        b.setAck(true);
        if (args != null)
            b.addAllArgs(args);
        return b.build();
    }

    public static MessagesProtos.FileTransfer buildErrorFileTransferResponse() {
        FileTransfer.Builder b = FileTransfer.newBuilder();
        b.setType(FileTransfer.Type.ERROR);
        return b.build();
    }
}
