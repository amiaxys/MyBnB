package main;

import java.util.ArrayList;
import java.util.Scanner;
import java.sql.Date;

public class CommandLine {

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;

	// Public functions - CommandLine State Functions

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
						//this.insertOperator();
						break;
					case 2:
						System.out.println("To be implemented, nothing here yet.");
						//this.selectOperator();
						break;
					/*case 3:
						this.printSchema();
						break;
					case 4:
						this.printColSchema();
						break;*/
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

	// Private functions

	// Print menu options
	private static void menu() {
		System.out.println("=========MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create an account.");
		System.out.println("2. Sign in.");
		// NOTE: change menu so that once they sign in/create an account, 
		// there's sign out and other operations
		// NOTE: remember to add the report option
		/*System.out.println("2. Select a record.");
		System.out.println("3. Print schema.");
		System.out.println("4. Print table schema.");*/
		System.out.print("Choose one of the previous options [0-2]: ");
	}

	// Called during the initialization of an instance of the current class
	// in order to retrieve from the user the credentials with which our program
	// is going to establish a connection with MySQL
	private String[] getCredentials() {
		String[] cred = new String[3];
		System.out.print("Username: ");
		cred[0] = sc.nextLine();
		System.out.print("Password: ");
		cred[1] = sc.nextLine();
		/*
		 * System.out.print("Database: "); cred[2] = sc.nextLine();
		 */
		return cred;
	}
	
	// Function that handles the feature: "Create an account."
	private void createUser() {
		// NOTE: remember to salt and hash password later
		// https://www.javaguides.net/2020/02/java-sha-256-hash-with-salt-example.html
		// NOTE: make a class/model for this later
		String sin = null;
		String password = null;
		String name = null;
		String address = null;
		Date birthdate = null;
		String occupation = null;
		String temp;
		while (sin == null || password == null) {
			try {
				System.out.print("Enter SIN: ");
				temp = sc.nextLine();
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
				System.out.print("Enter password: ");
				temp = sc.nextLine();
				if (temp.length() >= 8) {
					password = temp;
				} else {
					System.out.println("Your password is not 8 characters long, try again!");
					continue;
				}
				System.out.print("Enter name: ");
				temp = sc.nextLine();
				if (temp.length() != 0) {
					name = temp;
				}
				System.out.print("Enter address: ");
				temp = sc.nextLine();
				if (temp.length() != 0) {
					address = temp;
				}
				System.out.print("Enter birthdate (YYYY-MM-DD): ");
				temp = sc.nextLine();
				if (temp.length() != 0) {
					//birthdate = temp;
					/* DateTimeFormatter sdr = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSzzz");
    				ZonedDateTime MydateTime = ZonedDateTime.parse(kafkaTime,sdr);
    				this.time = MydateTime.toInstant().toEpochMilli(); */
				}
				
				System.out.print("Enter occupation: ");
				temp = sc.nextLine();
				if (temp.length() != 0) {
					occupation = temp;
				}
			} catch (NumberFormatException e) {
				System.out.println("That's not a SIN, please try again!");
			}
		}
		
		int rows = sqlMngr.insertUser(sin, password, name, address, birthdate, occupation);
		System.out.println("");
		System.out.println("Rows affected: " + rows);
		System.out.println("");
	
	}

/*	// Function that handles the feature: "3. Print schema."
	private void printSchema() {
		ArrayList<String> schema = sqlMngr.getSchema();

		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of tables: " + schema.size());
		for (int i = 0; i < schema.size(); i++) {
			System.out.println("Table: " + schema.get(i));
		}
		System.out.println("------------");
		System.out.println("");
	}

	// Function that handles the feature: "4. Print table schema."
	private void printColSchema() {
		System.out.print("Table Name: ");
		String tableName = sc.nextLine();
		ArrayList<String> result = sqlMngr.colSchema(tableName);
		System.out.println("");
		System.out.println("------------");
		System.out.println("Total number of fields: " + result.size() / 2);
		for (int i = 0; i < result.size(); i += 2) {
			System.out.println("-");
			System.out.println("Field Name: " + result.get(i));
			System.out.println("Field Type: " + result.get(i + 1));
		}
		System.out.println("------------");
		System.out.println("");
	}

	// Function that handles the feature: "2. Select a record."
	private void selectOperator() {
		String query = "";
		System.out.print("Issue the Select Query: ");
		query = sc.nextLine();
		query.trim();
		if (query.substring(0, 6).compareToIgnoreCase("select") == 0)
			sqlMngr.selectOp(query);
		else
			System.err.println("No select statement provided!");
	}

	// Function that handles the feature: "1. Insert a record."
	private void insertOperator() {
		int rowsAff = 0;
		int counter = 0;
		String query = "";
		System.out.print("Table: ");
		String table = sc.nextLine();
		System.out.print("Comma Separated Columns: ");
		String cols = sc.nextLine();
		System.out.print("Comma Separated Values: ");
		String[] vals = sc.nextLine().split(",");
		// transform the user input into a valid SQL insert statement
		query = "INSERT INTO " + table + " (" + cols + ") VALUES(";
		for (counter = 0; counter < vals.length - 1; counter++) {
			query = query.concat("'" + vals[counter] + "',");
		}
		query = query.concat("'" + vals[counter] + "');");
		System.out.println(query);
		rowsAff = sqlMngr.insertOp(query);
		System.out.println("");
		System.out.println("Rows affected: " + rowsAff);
		System.out.println("");
	}*/

}
