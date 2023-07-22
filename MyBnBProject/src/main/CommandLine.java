package main;

import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class CommandLine {

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	// current user, if signed in
	private User currentUser = null;

	/*
	 * ------------ Public functions - CommandLine State Functions ------------
	 */

	/*
	 * Function used for initializing an instance of current class
	 */
	public boolean startSession() {
		boolean success = true;
		if (sc == null) {
			sc = new Scanner(System.in);
		}
		if (sqlMngr == null) {
			sqlMngr = new SQLController();
		}
		try {
			success = sqlMngr.connect(this.getCredentials());
		} catch (ClassNotFoundException e) {
			success = false;
			System.err.println("Establishing connection triggered an exception!");
			e.printStackTrace();
			sc = null;
			sqlMngr = null;
		}
		return success;
	}

	/*
	 * Function that acts as destructor of an instance of this class. Performs some
	 * housekeeping setting instance's private field to null
	 */
	public void endSession() {
		if (sqlMngr != null)
			sqlMngr.disconnect();
		if (sc != null) {
			sc.close();
		}
		sqlMngr = null;
		sc = null;
	}

	/*
	 * Function that executes an infinite loop and activates the respective
	 * functionality according to user's choice. At each time it also outputs the
	 * menu of core functionalities supported from our application.
	 */
	public boolean execute() {
		if (sc != null && sqlMngr != null) {
			System.out.println("");
			System.out.println("***************************");
			System.out.println("******ACCESS GRANTED*******");
			System.out.println("***************************");
			System.out.println("");

			String input = "";
			int choice = -1;
			do {
				menu(); // Print Menu
				input = sc.nextLine();
				try {
					choice = Integer.parseInt(input);
					switch (choice) { // Activate the desired functionality
					case 0:
						break;
					case 1:
						this.createUser();
						// this.insertOperator();
						break;
					case 2:
						this.signIn();
						// this.selectOperator();
						break;
					/*
					 * case 3: this.printSchema(); break; case 4: this.printColSchema(); break;
					 */
					default:
						System.out.println("That's not an option, please try again!");
						break;
					}
				} catch (NumberFormatException e) {
					input = "-1";
				}
			} while (input.compareTo("0") != 0);

			return true;
		} else {
			System.out.println("");
			System.out.println("Connection could not been established! Bye!");
			System.out.println("");
			return false;
		}
	}

	/*
	 * ------------ Private functions ------------
	 */

	// Print menu options
	private static void menu() {
		System.out.println("=========MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create an account.");
		System.out.println("2. Sign in.");
		// NOTE: change menu so that once they sign in/create an account,
		// there's sign out and other operations
		// NOTE: remember to add the report option
		/*
		 * System.out.println("2. Select a record.");
		 * System.out.println("3. Print schema.");
		 * System.out.println("4. Print table schema.");
		 */
		System.out.print("Choose one of the previous options [0-2]: ");
	}

	// Called during the initialization of an instance of the current class
	// in order to retrieve from the user the credentials with which our program
	// is going to establish a connection with MySQL
	private String[] getCredentials() {
		String[] cred = new String[3];
		System.out.println("*******ENTER LOGIN CREDENTIALS FOR MYSQL DATABASE*******\n");
		System.out.print("Username: ");
		cred[0] = sc.nextLine();
		System.out.print("Password: ");
		cred[1] = sc.nextLine();
		return cred;
	}

	// Code from:
	// https://www.javaguides.net/2020/02/java-sha-256-hash-with-salt-example.html
	private static byte[] getSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	}

	private static String getSaltHashedPassword(String password, byte[] salt) {
		String shPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			shPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error salt and hashing password!");
			e.printStackTrace();
		}
		return shPassword;
	}

	private boolean checkValidDate(String date) {
		boolean success = true;
		DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
		try {
			format.parse(date);
			int[] splitDate = Arrays.stream(date.split("-")).mapToInt(Integer::parseInt).toArray();
			LocalDate current = LocalDate.now();
			if (splitDate[0] > current.getYear()) {
				success = false;
			} else if (splitDate[0] == current.getYear()) {
				if (splitDate[1] > current.getMonthValue()) {
					success = false;
				} else if (splitDate[1] == current.getMonthValue()) {
					if (splitDate[2] > current.getDayOfMonth()) {
						success = false;
					}
				}
			}
		} catch (DateTimeParseException e) {
			success = false;
		}
		return success;
	}

	// Function that handles the feature: "Create an account."
	private void createUser() {
		boolean repeat = false;
		User user = new User();
		user.salt = getSalt();
		String temp;
		while (user.sin == null || user.password == null || repeat) {
			repeat = false;
			try {
				System.out.print("Enter SIN: ");
				temp = sc.nextLine().strip();
				if (temp.length() != 9) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				int tempInt = Integer.parseInt(temp);
				if (tempInt < 0) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				user.sin = temp;
				System.out.print("Enter password: ");
				temp = sc.nextLine();
				int tempLen = temp.length();
				if (tempLen >= 8 && tempLen <= 250) {
					user.password = getSaltHashedPassword(temp, user.salt);
				} else if (tempLen < 8) {
					System.out.println("Your password is not 8 characters long, try again!");
					continue;
				} else if (tempLen > 250) {
					System.out.println("Your password is too long, try again!");
					continue;
				}
				System.out.print("Enter name: ");
				temp = sc.nextLine().strip();
				if (temp.length() != 0) {
					user.name = temp;
				}
				System.out.print("Enter address: ");
				temp = sc.nextLine().strip();
				if (temp.length() != 0) {
					user.address = temp;
				}
				System.out.print("Enter birthdate (YYYY-MM-DD): ");
				temp = sc.nextLine().strip();
				if (temp.length() != 0) {
					if (checkValidDate(temp)) {
						user.birthdate = temp;
					} else {
						System.out.println("That's not a proper birthdate, try again!");
						repeat = true;
						continue;
					}
				}

				System.out.print("Enter occupation: ");
				temp = sc.nextLine().strip();
				if (temp.length() != 0) {
					user.occupation = temp;
				}
			} catch (NumberFormatException e) {
				System.out.println("That's not a SIN, please try again!");
			}
		}

		int rows = sqlMngr.insertUser(user.sin, user.password, user.salt, user.name, user.address, user.birthdate,
				user.occupation);
		System.out.println("");
		System.out.println("User table rows affected: " + rows);
		System.out.println("");

	}

	// Function that handles the feature: "Sign in."
	private void signIn() {
		User user = null;
		String sin = null;
		String temp;
		while (user == null) {
			while (sin == null) {
				System.out.print("Enter SIN: ");
				temp = sc.nextLine().strip();
				if (temp.length() != 9) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				int tempInt = Integer.parseInt(temp);
				if (tempInt < 0) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				sin = temp;
			}

			user = sqlMngr.selectUserBySIN(sin);
			if (user == null) {
				System.out.println("Try again!");
				sin = null;
			}
		}

		String password = null;
		System.out.print("Enter password: ");
		temp = sc.nextLine();
		password = getSaltHashedPassword(temp, user.salt);

		if (password.equals(user.password)) {
			System.out.println("You are signed in!");
			this.currentUser = user;
		} else {
			System.out.println("Wrong password!");
		}
	}

	/*
	 * // Function that handles the feature: "3. Print schema." private void
	 * printSchema() { ArrayList<String> schema = sqlMngr.getSchema();
	 * 
	 * System.out.println(""); System.out.println("------------");
	 * System.out.println("Total number of tables: " + schema.size()); for (int i =
	 * 0; i < schema.size(); i++) { System.out.println("Table: " + schema.get(i)); }
	 * System.out.println("------------"); System.out.println(""); }
	 * 
	 * // Function that handles the feature: "4. Print table schema." private void
	 * printColSchema() { System.out.print("Table Name: "); String tableName =
	 * sc.nextLine(); ArrayList<String> result = sqlMngr.colSchema(tableName);
	 * System.out.println(""); System.out.println("------------");
	 * System.out.println("Total number of fields: " + result.size() / 2); for (int
	 * i = 0; i < result.size(); i += 2) { System.out.println("-");
	 * System.out.println("Field Name: " + result.get(i));
	 * System.out.println("Field Type: " + result.get(i + 1)); }
	 * System.out.println("------------"); System.out.println(""); }
	 * 
	 * // Function that handles the feature: "2. Select a record." private void
	 * selectOperator() { String query = "";
	 * System.out.print("Issue the Select Query: "); query = sc.nextLine();
	 * query.trim(); if (query.substring(0, 6).compareToIgnoreCase("select") == 0)
	 * sqlMngr.selectOp(query); else
	 * System.err.println("No select statement provided!"); }
	 * 
	 * // Function that handles the feature: "1. Insert a record." private void
	 * insertOperator() { int rowsAff = 0; int counter = 0; String query = "";
	 * System.out.print("Table: "); String table = sc.nextLine();
	 * System.out.print("Comma Separated Columns: "); String cols = sc.nextLine();
	 * System.out.print("Comma Separated Values: "); String[] vals =
	 * sc.nextLine().split(","); // transform the user input into a valid SQL insert
	 * statement query = "INSERT INTO " + table + " (" + cols + ") VALUES("; for
	 * (counter = 0; counter < vals.length - 1; counter++) { query =
	 * query.concat("'" + vals[counter] + "',"); } query = query.concat("'" +
	 * vals[counter] + "');"); System.out.println(query); rowsAff =
	 * sqlMngr.insertOp(query); System.out.println("");
	 * System.out.println("Rows affected: " + rowsAff); System.out.println(""); }
	 */

}
