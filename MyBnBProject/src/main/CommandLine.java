package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.math.BigDecimal;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.text.DecimalFormat;

public class CommandLine {

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	// current user, if signed in
	private User currentUser = null;

  DecimalFormat coordinatesDf = new DecimalFormat("#.####");

	private ArrayList<String> amenities = new ArrayList<>(Arrays.asList("wifi", "kitchen", "washer", "dryer",
			"air conditioning", "heating", "dedicated workspace", "tv", "hair dryer", "iron", "pool", "hot tub",
			"free parking", "ev charger", "crib", "gym", "bbq grill", "breakfast", "indoor fireplace",
			"smoking allowed", "beachfront", "waterfront", "ski-in/ski-out", "smoke alarm", "carbon monoxide alarm"));
	/*
	 * private ArrayList<String> amenities = new ArrayList<>(
	 * Arrays.asList("Wifi","Kitchen","Washer","Dryer",
	 * "Air conditioning","Heating","Dedicated workspace", "TV",
	 * "Hair dryer","Iron","Pool","Hot tub","Free parking",
	 * "EV charger","Crib","Gym","BBQ grill","Breakfast",
	 * "Indoor fireplace","Smoking allowed","Beachfront",
	 * "Waterfront","Ski-in/ski-out","Smoke alarm","Carbon monoxide alarm"));
	 */

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
			do {
				if (this.currentUser == null) {
					input = runMenuOptions();
				} else {
					input = runUserMenuOptions();
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

	// Print not signed in menu options
	private static void menu() {
		System.out.println("=========MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create an account.");
		System.out.println("2. Sign in.");
		// NOTE: remember to add the report option
		/*
		 * System.out.println("2. Select a record.");
		 * System.out.println("3. Print schema.");
		 * System.out.println("4. Print table schema.");
		 */
		System.out.print("Choose one of the previous options [0-2]: ");
	}

	// Print signed in menu (user menu) options
	private static void userMenu() {
		System.out.println("=========USER MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create a listing.");
		System.out.println("2. Search for listings.");
		System.out.println("10. Sign out.");
		// add delete account + sign out here later
		System.out.print("Choose one of the previous options [0-2]: ");
	}

	// Print search options
	private static void searchOptions() {
		System.out.println("*********SEARCH OPTIONS*********");
		System.out.println("0. Back.");
		System.out.println("1. Search by exact address.");
		System.out.println("2. Search by latitude and longitude.");
		System.out.println("3. Search by postal code. [not implemented]");
		System.out.print("Choose one of the previous options [0-3]: ");
	}

	// Loop through and execute menu options
	private String runMenuOptions() {
		menu(); // Print Menu
		String input = sc.nextLine();
		try {
			int choice = Integer.parseInt(input);
			// Activate the desired functionality
			switch (choice) {
			case 0:
				break;
			case 1:
				this.createUser();
				break;
			case 2:
				this.signIn();
				break;
			default:
				System.out.println("That's not an option, please try again!");
				break;
			}
		} catch (NumberFormatException e) {
			input = "-1";
		}

		return input;
	}

	// Loop through and execute user menu options
	private String runUserMenuOptions() {
		userMenu(); // Print Menu
		String input = sc.nextLine();
		try {
			int choice = Integer.parseInt(input);
			// Activate the desired functionality
			switch (choice) {
			case 0:
				break;
			case 1:
				this.createListing();
				break;
			case 2:
				String searchInput;
				do {
					searchInput = this.runSearchOptions();
				} while (searchInput.compareTo("0") != 0);
				break;
			case 10:
				this.signOut();
				break;
			default:
				System.out.println("That's not an option, please try again!");
				break;
			}
		} catch (NumberFormatException e) {
			input = "-1";
		}

		return input;
	}

	private String runSearchOptions() {
		searchOptions(); // Print search options
		String input = sc.nextLine();
		try {
			int choice = Integer.parseInt(input);
			// Activate the desired functionality
			switch (choice) {
			case 0:
				break;
			case 1:
				this.searchListingByAddress();
				break;
			case 2:
        this.searchListingByCoord();
				break;
			case 3:
				break;
			default:
				System.out.println("That's not an option, please try again!");
				break;
			}
		} catch (NumberFormatException e) {
			input = "-1";
		}

		return input;
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
			LocalDate parsedDate = LocalDate.parse(date, format);
			if (parsedDate.isAfter(LocalDate.now())) {
				success = false;
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
		String input = "";
		System.out.println("Type \"exit\" to exit anytime in the process.");
		while ((user.sin == null || user.password == null || repeat) && !input.equalsIgnoreCase("exit")) {
			repeat = false;
			try {
				System.out.print("Enter SIN: ");
				input = sc.nextLine().strip();
				if (input.length() != 9) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				int tempInt = Integer.parseInt(input);
				if (tempInt < 0) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				user.sin = input;
				System.out.print("Enter password (8-250 characters): ");
				input = sc.nextLine();
				int tempLen = input.length();
				if (tempLen >= 8 && tempLen <= 250) {
					user.password = getSaltHashedPassword(input, user.salt);
				} else if (tempLen < 8) {
					System.out.println("Your password is not 8 characters long, try again!");
					continue;
				} else if (tempLen > 250) {
					System.out.println("Your password is too long, please try again!");
					continue;
				}
				System.out.print("Enter name: ");
				input = sc.nextLine().strip();
				if (input.length() != 0) {
					user.name = input;
				}
				System.out.print("Enter address: ");
				input = sc.nextLine().strip();
				if (input.length() != 0) {
					user.address = input;
				}
				System.out.print("Enter birthdate (YYYY-MM-DD): ");
				input = sc.nextLine().strip();
				if (input.length() != 0) {
					if (checkValidDate(input)) {
						user.birthdate = input;
					} else {
						System.out.println("That's not a proper birthdate, please try again!");
						repeat = true;
						continue;
					}
				}

				System.out.print("Enter occupation: ");
				input = sc.nextLine().strip();
				if (input.length() != 0) {
					user.occupation = input;
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
		String input = "";
		System.out.println("Type \"exit\" to exit anytime in the process.");
		while (user == null && !input.equalsIgnoreCase("exit")) {
			while (sin == null && !input.equalsIgnoreCase("exit")) {
				System.out.print("Enter SIN: ");
				input = sc.nextLine().strip();
				if (input.length() != 9) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				int tempInt = Integer.parseInt(input);
				if (tempInt < 0) {
					System.out.println("That's not a SIN, please try again!");
					continue;
				}
				sin = input;
			}

			user = sqlMngr.selectUserBySIN(sin);
			if (user == null) {
				System.out.println("Try again!");
				sin = null;
			}
		}

		String password = null;
		System.out.print("Enter password: ");
		input = sc.nextLine();
		password = getSaltHashedPassword(input, user.salt);

		if (password.equals(user.password)) {
			System.out.println("You are signed in!");
			this.currentUser = user;
		} else {
			System.out.println("Wrong password!");
		}
	}

	private void signOut() {
		String input = "";
		while (!input.equalsIgnoreCase("y")) {
			System.out.print("Are you sure you want to sign out? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("n")) {
				break;
			} else if (input.equalsIgnoreCase("y")) {
				this.currentUser = null;
			} else {
				System.out.println("That's not proper input, please try again!");
			}
		}
		
	}

	private boolean isValid(String input, int max) {
		if (input.strip().equals("")) {
			System.out.println("Input should be non-empty, please try again!");
			return false;
		}
		if (input.length() > max) {
			System.out.println("Length of input should not be more than " + max + " characters, please try again!");
			return false;
		}
		return true;
	}

	private boolean isValidAmenities(String list) {
		String[] splStrings = list.split(",");
		for (String word : splStrings) {
			if (!amenities.contains(word.strip().toLowerCase())) {
				return false;
			}
		}
		return true;
	}

	private void printAmenities() {
		System.out.println("Wifi\t\t\t\tKitchen\t\t\tWasher\nDryer"+
        "\t\t\t\tAir conditioning\tHeating\nDedicated workspace\t\tTV"+
        "\t\t\tHair dryer\nIron\t\t\t\tPool\t\t\tHot tub\nFree parking"+
        "\t\t\tEV charger\t\tCrib\nGym\t\t\t\tBBQ grill\t\tBreakfast"+
        "\nIndoor fireplace\t\tSmoking allowed\t\tBeachfront"+
        "\nWaterfront\t\t\tSki-in/ski-out\t\tSmoke alarm\nCarbon monoxide alarm");
	}

	// Function that handles the feature: "Create a listing."
	private void createListing() {
		String type = null;
		String street = null;
		int number = 0;
		String postalCode = null;
		String country = null;
		String city = null;
		BigDecimal latitude = null;
		BigDecimal longitude = null;
		String traits = null;
		String temp;

		// insert new row to table Listing
		while (type == null || postalCode == null || latitude == null || longitude == null) {
			System.out.print("Enter a type of listing (apartment, house, or room): ");
			temp = sc.nextLine();
			if (!temp.equals("apartment") && !temp.equals("house") && !temp.equals("room")) {
				System.out.println("That's not a valid type of listing, please try again!");
				continue;
			}
			type = temp;

			System.out.print("Enter the street name: ");
			temp = sc.nextLine();
			if (!isValid(temp, 40)) {
				continue;
			}
			street = temp;

			try {
				System.out.print("Enter the street number: ");
				temp = sc.nextLine();
				if (!isValid(temp, 6)) {
					continue;
				}
				number = Integer.parseInt(temp);

				System.out.print("Enter the postal code: ");
				temp = sc.nextLine();
				if (!isValid(temp, 10)) {
					continue;
				}
				postalCode = temp;

				System.out.print("Enter the country: ");
				temp = sc.nextLine();
				if (!isValid(temp, 56)) {
					continue;
				}
				country = temp;

				System.out.print("Enter the city: ");
				temp = sc.nextLine();
				if (!isValid(temp, 20)) {
					continue;
				}
				city = temp;

				System.out.print("Enter the latitude (in decimal values): ");
				temp = sc.nextLine();
				if (!isValid(temp, 7)) {
					continue;
				}
				latitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(temp)));

				System.out.print("Enter the longitude (in decimal values): ");
				temp = sc.nextLine();
				if (!isValid(temp, 8)) {
					continue;
				}
				longitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(temp)));
			} catch (NumberFormatException e) {
				System.out.println("That's not a number, please try again!");
			}
		}

		System.out.println("\nSelect any of the following amenities/characteristics:\n");
		printAmenities();
		System.out.println();
		while (traits == null) {
			System.out.print(
					"Enter a list of amenities above separated by commas, no space in between! (e.g., Wifi,Hot tub,Gym): ");
			temp = sc.nextLine();
			if (!isValidAmenities(temp)) {
				System.out.println("That's an invalid list, please try again!");
				continue;
			}
			traits = temp;
		}

		int rows = sqlMngr.insertListing(type, street, number, postalCode, country, city, latitude, longitude, traits);
		System.out.println("\nRows affected: " + rows + "\n");
		// inserting to Listing failed.
		if (rows == 0) {
			return;
		}
		// insert new row to table Hosts
		rows = sqlMngr.insertHosts(currentUser.sin, street, number, postalCode, country);
	}

	private void printListings(ArrayList<Listing> listings) {
		int count = 0;
		System.out.println("\nResult: " + listings.size() + " listings\n");
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("| %-14s | %-97s | %-8s | %-9s |%n", "Type", "Address", "Latitude", "Longitude");
		System.out.println(
				"=============================================================================================================================================");
		for (Listing listing : listings) {
			count++;
			System.out.printf("| %d. %-11s | %d %-29s %-20s %-30s %-10s | %.4f  | %.4f   |%n", count, listing.type, listing.number,
					listing.street, listing.city, listing.country, listing.postalCode,listing.latitude.doubleValue(),listing.longitude.doubleValue());
		}
		System.out.println(
				"---------------------------------------------------------------------------------------------------------------------------------------------\n");
	}

	private void searchListingByAddress() {
		String street, postalCode, country, city;
		int number = -1;

		System.out.print("Enter a street name: ");
		street = sc.nextLine();

		while (number == -1) {
			try {
				System.out.print("Enter a street number: ");
				number = Integer.parseInt(sc.nextLine());
			} catch (NumberFormatException e) {
				System.out.println("That's not a numeral value, please try again!");
				continue;
			}
		}
		System.out.print("Enter a postal code: ");
		postalCode = sc.nextLine();
		System.out.print("Enter a country: ");
		country = sc.nextLine();
		System.out.print("Enter a city: ");
		city = sc.nextLine();

		ArrayList<Listing> listings = sqlMngr.searchListingAddr(street, number, postalCode, country, city);

		printListings(listings); // print result
	}

  // Calculation code from:
  // https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
  private ArrayList<Listing> calDistance(ArrayList<Listing> listings, double lat1, double long1, double distance) {
    ArrayList<Listing> filtered = new ArrayList<>();
    final int radius = 6371; // radius of earth in km

    for (Listing listing: listings) {
      double lat2 = listing.latitude.doubleValue();
      double long2 = listing.longitude.doubleValue();

      double latDistance = Math.toRadians(lat2 - lat1);
      double lonDistance = Math.toRadians(long2 - long1);
      double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
            + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
            * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
      double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
      double calDistance = radius * c;

      if (calDistance <= distance) {
        filtered.add(listing);
      }
    }
    return filtered;
  }

  private void searchListingByCoord() {
    BigDecimal latitude = null, longitude = null;
    double distance = -1;
    
    while (distance == -1) {
      try {
        System.out.print("Enter a latitude (in decimal values): ");
        latitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(sc.nextLine())));

        System.out.print("Enter a longitude (in decimal values): ");
        longitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(sc.nextLine())));

        String input;
        System.out.print("Enter distance (in km) to the specified coordinates (Leave input empty for default value of 124km): ");
        input = sc.nextLine();
        if (input.strip().equals("")) {
          distance = 124;
        }
        else {
          distance = Double.parseDouble(input);
        }
      } catch (NumberFormatException e) {
        System.out.println("That's not a numeral value, please try again!");
      }
    }

    ArrayList<Listing> listings = sqlMngr.searchAllListing();
    listings = calDistance(listings, latitude.doubleValue(), longitude.doubleValue(), distance);
    printListings(listings);
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
