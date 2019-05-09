package processors;

import communication.ProtoCommunication;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;

public class UserRequestProcessor {
	/* Static methods only class */
	private UserRequestProcessor() { }

	/**
	 * Process the upload of a file.
	 * @param protoComm Communication over ProtoCommuniacation object
	 * @param fileName Name of the existing file to be uploaded to the server
	 * @throws IOException When the transfer fails and the cleanup of the File fails.
	 */
	public static void up(ProtoCommunication protoComm, String fileName) throws IOException {
		try (var fileIs = new BufferedInputStream(new FileInputStream(fileName))) {
			protoComm.uploadFile(fileIs);
		} catch (IOException ex) {
			var path = Paths.get(fileName);
			if (Files.exists(path))
				Files.delete(path);
		}
	}

	/**
	 * Process the download of a file.
	 * @param protoComm  Communication over ProtoCommuniacation object
	 * @param fileName Name of the existing file to be uploaded to the server
	 * @throws IOException When the transfer fails and the cleanup of the File fails.
	 */
	public static void down(ProtoCommunication protoComm, String fileName) throws IOException {
		try (var fileOs = new BufferedOutputStream(new FileOutputStream(fileName))) {
			protoComm.downloadFile(fileOs);
		} catch (IOException ex) {
			var path = Paths.get(fileName);
			if (Files.exists(path))
				Files.delete(path);
		}
	}
}
