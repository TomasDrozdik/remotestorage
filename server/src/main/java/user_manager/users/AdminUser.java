package user_manager.users;

import structure.IAdminStructure;
import structure.Structure;

public class AdminUser extends User {
	private IAdminStructure storage;

	public AdminUser(String username) {
		this.username = username;
		this.storage = Structure.getStructure();
	}

	public IAdminStructure getStorage() {
		return storage;
	}
}
