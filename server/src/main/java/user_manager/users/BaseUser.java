package user_manager.users;

import structure.IUserStructure;
import structure.Structure;
import structure.files.HomeDir;
import structure.files.UserProperty;

public class BaseUser extends User {
	private IUserStructure storage;

	public BaseUser(String username) {
		this.username = username;
		this.storage = Structure.getStructure();
	}

	public HomeDir getHome() {
		return storage.getHome(this);
	}

	public UserProperty getProperty() {
		return storage.getUserProperty(this);
	}
}
