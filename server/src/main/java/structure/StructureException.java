package structure;

/**
 * Exception signaling error in used structure.
 */
public class StructureException extends Exception {
	private Type type;

	public StructureException(Type t) {
		this.type = t;
	}

	@Override
	public String toString() {
		return "StructureException:" + type.toString();
	}

	/**
	 * Inner enum type, representing type of structure error.
	 */
	public enum Type {
		NOT_DIRECTORY,
		NOT_FILE,
		MISSING_CORE_FILE,
		CREATION_ERROR,
		INTEGRITY_ERROR,
		FILE_NOT_FOUND_EXCEPTION,
		SECURITY_EXCEPTION,
		IO_EXCEPTION,
	}

}
