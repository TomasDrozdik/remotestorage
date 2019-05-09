package structure.files;

public class PasswdRecord {
	public String hashedPasswd;
	public boolean isAdmin;
	public long salt;

	public PasswdRecord(String hashedPasswd, boolean isAdmin, long salt) {
		this.hashedPasswd = hashedPasswd;
		this.isAdmin = isAdmin;
		this.salt = salt;
	}
}
