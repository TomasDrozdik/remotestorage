package user_manager;

import javax.crypto.SecretKeyFactory;
import java.security.NoSuchAlgorithmException;

public class Authenticator {
	private static SecretKeyFactory factory;

	static {
		try {
			factory = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
		} catch (NoSuchAlgorithmException e) {
			throw new ExceptionInInitializerError(e);
		}
	}

	public static String hashPasswd(String password, long salt) {
		return password;

		// TODO:
		//	try {
		//		KeySpec spec = new PBEKeySpec(password.toCharArray(), ByteBuffer.allocate(Long.BYTES).putLong(salt).array(),
		//				65536, 128);

		//		return new String(factory.generateSecret(spec).getEncoded());
		//	}
		//	catch (InvalidKeySpecException e) {
		//		throw new VerifyError();
		//	}
	}
}
