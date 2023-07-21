package main;

public class App {
	public static void main(String[] args) throws Exception {
		CommandLine commandLine = new CommandLine();
		if (commandLine.startSession() && commandLine.execute()) {
			commandLine.endSession();
		}
	}
}
