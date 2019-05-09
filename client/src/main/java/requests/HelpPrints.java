package requests;

import java.io.PrintStream;

public class HelpPrints {
	/* Static method only class. */
	private HelpPrints() {
	}

	public static void printBaseUserHelp(PrintStream out) {
		out.println("List of available user commands:");
		lsHelp(out);
		upHelp(out);
		downHelp(out);
		whoHelp(out);
		out.println();
		cmdExecHelp(out);
		out.println();
	}

	public static void printAdminHelp(PrintStream out) {
		System.err.println("List of available admin commands: ");
		addUserHelp(out);
		out.println();
		cmdExecHelp(out);
		out.println();
	}

	static void cmdExecHelp() {
		cmdExecHelp();
	}

	static void cmdExecHelp(PrintStream err) {
		err.println("You can also execute any programs via exec call. Just start with \"!\"");
		err.println("-> ![cmd] [arg]...");
	}

	static void lsHelp(PrintStream err) {
		err.println("-> ls");
	}

	static void upHelp(PrintStream err) {
		err.println("-> up [file]");
	}

	static void downHelp(PrintStream err) {
		err.println("-> down [file]");
	}

	static void whoHelp(PrintStream err) {
		err.println("-> who");
	}

	static void addUserHelp(PrintStream err) {
		err.println("> adduser [username] [password] [admin|user]");
	}
}
