package server.client_handler.responses;

import communication.ProtoCommunication;
import remotestorage.proto.MessagesProtos.ServerResponse;
import structure.files.UploadedFile;
import user_manager.users.BaseUser;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;
import java.util.stream.Collectors;

import static server.client_handler.responses.ResponseBuilder.*;

public class BaseUserResponses {
	/* Static methods only */
	private BaseUserResponses () {}

	/**
	 * Perform ls (list) operation.
	 * @param u user
	 * @return appropriate ServerResponse
	 */
	public static ServerResponse ls(BaseUser u) {
		return buildPositiveResponse(u.getHome()
				.getFiles()
				.stream()
				.map(UploadedFile::getName)
				.collect(Collectors.toList()));
	}

	/**
	 * Perform upload operation.
	 * @param u user
	 * @param fileName file
	 * @return appropriate ServerResponse
	 * @throws IOException when protoComm communication fails
	 */
	public static ServerResponse up(BaseUser u, ProtoCommunication protoComm, String fileName) throws IOException {
		Path newFilePath;
		try {
			newFilePath = u.getHome().addNewFile(fileName);
		} catch (FileAlreadyExistsException e) {
			endFileTransfer(protoComm);
			return buildNegativeResponse(ServerResponse.ErrorType.ALREADY_EXISTS);
		} catch (IOException e) {
			endFileTransfer(protoComm);
			return buildNegativeResponse(ServerResponse.ErrorType.OPERATION_FAIL);
		}
		/* Now download the file from the client via protoComm to new file. */
		try (var fileOs = new BufferedOutputStream(new FileOutputStream(newFilePath.toString()))) {
			protoComm.downloadFile(fileOs);
			return buildPositiveResponse(null);
		} catch (IOException e) {
			return buildNegativeResponse(ServerResponse.ErrorType.CONNECTION_FAIL);
		}
	}

	/**
	 * Try to perform download operation for given user on given file.
	 * @param u user
	 * @param fileName file
	 * @return appropriate ServerResponse (may indicate error) or null in case of internal error.
	 * @throws IOException when protoComm communication fails
	 */
	public static ServerResponse down(BaseUser u, ProtoCommunication protoComm, String fileName) throws IOException {
		Path fileToDownload = u.getHome().getFileByName(fileName);
		if (fileToDownload == null) {
			endFileTransfer(protoComm);
			return buildNegativeResponse(ServerResponse.ErrorType.NOT_FOUND);
		}
		/* Now upload the file to client via protoComm. */
		try (var fileIs = new BufferedInputStream(new FileInputStream(fileToDownload.toString()))) {
			protoComm.uploadFile(fileIs);
			return buildPositiveResponse(null);
		} catch (IOException e) {
			endFileTransfer(protoComm);
			return buildNegativeResponse(ServerResponse.ErrorType.OPERATION_FAIL);
		}
	}

	/**
	 * Ends the file transfer stream by sending Error type FileTransfer message.
	 *
	 * @param protoComm Communication to send Error FileTransfer message to.
	 * @throws IOException In case the protoComm throws on send.
	 */
	private static void endFileTransfer(ProtoCommunication protoComm) throws IOException {
		protoComm.send(buildErrorFileTransferResponse());
	}
}
