package communication;

import com.google.protobuf.ByteString;
import com.google.protobuf.CodedInputStream;
import com.google.protobuf.CodedOutputStream;
import remotestorage.proto.MessagesProtos.ClientRequest;
import remotestorage.proto.MessagesProtos.FileTransfer;
import remotestorage.proto.MessagesProtos.ServerResponse;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ProtoCommunication {
	private CodedInputStream input;
	private CodedOutputStream output;

	public ProtoCommunication(InputStream input, OutputStream output) {
		this.input = CodedInputStream.newInstance(input);
		this.output = CodedOutputStream.newInstance(output);
	}

	public void send(ServerResponse message) throws IOException {
		var buffer = message.toByteArray();
		/* First send the length of the message. */
		output.writeRawByte(buffer.length);
		/* Then send the message. */
		output.writeRawBytes(buffer);
		output.flush();
	}

	public void send(ClientRequest message) throws IOException {
		var buffer = message.toByteArray();
		/* First send the length of the message. */
		output.writeRawByte(buffer.length);
		/* Then send the message. */
		output.writeRawBytes(message.toByteArray());
		output.flush();
	}

	public void send(FileTransfer msg) throws IOException {
		var buffer = msg.toByteArray();
		/* First send the length of the message. */
		output.writeRawByte(buffer.length);
		/* Then send the message. */
		output.writeRawBytes(msg.toByteArray());
		output.flush();
	}

	public ServerResponse recvServerResponse() throws IOException {
		int length;
		length = input.readInt32();

		/* Read buffer of given length and parse ClientRequest from it. */
		return ServerResponse.parseFrom(input.readRawBytes(length));
	}

	// TODO check whether this can be made generic
	public ClientRequest recvClientRequest() throws IOException {
		int length;
		length = input.readInt32();
		/* Read buffer of given length and parse ClientRequest from it. */
		return ClientRequest.parseFrom(input.readRawBytes(length));
	}

	public void uploadFile(InputStream in) throws IOException {
		byte[] buffer = new byte[1024];
		int bytesRead = 0;
		FileTransfer.Builder messageBuilder;
		try {
			while ((bytesRead = in.read(buffer)) != -1) {
				messageBuilder = FileTransfer.newBuilder();
				messageBuilder.setData(ByteString.copyFrom(buffer, 0, bytesRead));
				messageBuilder.setType(FileTransfer.Type.CONTINUE);
				send(messageBuilder.build());
			}
			messageBuilder = FileTransfer.newBuilder();
			messageBuilder.setType(FileTransfer.Type.LAST);
			send(messageBuilder.build());
		} catch (IOException e) {
			messageBuilder = FileTransfer.newBuilder();
			messageBuilder.setType(FileTransfer.Type.ERROR);
			/* This send my throw exception meaning something is broken and we can't signal error -> end connection */
			send(messageBuilder.build());
		}
	}

	public void downloadFile(OutputStream out) throws IOException {
		FileTransfer msg;
		do {
			int length = input.readInt32();
			msg = FileTransfer.parseFrom(input.readRawBytes(length));
			if (msg.getType() == FileTransfer.Type.ERROR) {
				throw new IOException();
			}
			out.write(msg.getData().toByteArray());
		} while (msg.getType() != FileTransfer.Type.LAST);
	}
}
