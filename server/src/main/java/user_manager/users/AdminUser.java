package user_manager.users;

import structure.AdminStructure;
import structure.Structure;

public class AdminUser extends User {
	private AdminStructure storage;

	public AdminUser(String username) {
		this.username = username;
		this.storage = Structure.getStructure();
	}

	public AdminStructure getStorage() {
		return storage;
	}
}
