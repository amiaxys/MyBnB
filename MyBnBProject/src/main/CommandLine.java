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
	DecimalFormat priceDf = new DecimalFormat("#.##");

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
			System.out.println("That's not a number, please try again!");
		}

		return input;
	}

	// Print signed in menu (user menu) options
	private static void userMenu() {
		System.out.println("\n=========USER MENU=========");
		System.out.println("0. Exit.");
		System.out.println("1. Create a listing.");
		System.out.println("2. Search for listings.");
		System.out.println("3. Add availabilities to listings.");
		System.out.println("10. Sign out.");
		// add delete account later
		System.out.print("Choose one of the previous options [0-10]: ");
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
				case 3:
					this.addAvailabilities();
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
			System.out.println("That's not a number, please try again!");
		}

		return input;
	}

	// Print search options
	private static void searchOptions() {
		System.out.println("\n*********SEARCH OPTIONS*********");
		System.out.println("0. Back.");
		System.out.println("1. Search by exact address.");
		System.out.println("2. Search by latitude and longitude.");
		System.out.println("3. Search by postal code.");
		System.out.print("Choose one of the previous options [0-3]: ");
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
					this.searchListingByPostalCode();
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

	private LocalDate checkValidDate(String date) {
		DateTimeFormatter format = DateTimeFormatter.ofPattern("uuuu-MM-dd").withResolverStyle(ResolverStyle.STRICT);
		LocalDate parsedDate = null;
		try {
			parsedDate = LocalDate.parse(date, format);
		} catch (DateTimeParseException e) {
			parsedDate = null;
		}
		return parsedDate;
	}

	private LocalDate checkValidBirthdate(String date) {
		LocalDate parsedDate = checkValidDate(date);
		if (parsedDate != null && parsedDate.isAfter(LocalDate.now())) {
			parsedDate = null;
		}
		return parsedDate;
	}

	private LocalDate checkValidAvailDate(String date) {
		LocalDate parsedDate = checkValidDate(date);
		LocalDate now = LocalDate.now();
		if (parsedDate != null && (parsedDate.isBefore(now) || parsedDate.isEqual(now))) {
			parsedDate = null;
		}
		return parsedDate;
	}

	// Function that handles the feature: "Create an account."
	private void createUser() {
		boolean repeat = false;
		User user = new User();
		user.salt = getSalt();
		String input;
		while (user.sin == null || user.password == null || repeat) {
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
					user.birthdate = checkValidBirthdate(input);
					if (user.birthdate == null) {
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
		System.out.println("User rows affected: " + rows);
		System.out.println("");

	}

	// Function that handles the feature: "Sign in."
	private void signIn() {
		User user = null;
		String sin = null;
		String input;
		while (user == null) {
			while (sin == null) {
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
				System.out.println("This SIN is not attached to an account, please try again!");
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
		System.out.println();
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
		System.out.println("Wifi\t\t\t\tKitchen\t\t\tWasher\nDryer"
				+ "\t\t\t\tAir conditioning\tHeating\nDedicated workspace\t\tTV"
				+ "\t\t\tHair dryer\nIron\t\t\t\tPool\t\t\tHot tub\nFree parking"
				+ "\t\t\tEV charger\t\tCrib\nGym\t\t\t\tBBQ grill\t\tBreakfast"
				+ "\nIndoor fireplace\t\tSmoking allowed\t\tBeachfront"
				+ "\nWaterfront\t\t\tSki-in/ski-out\t\tSmoke alarm\nCarbon monoxide alarm");
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
		String input;

		System.out.println();
		// insert new row to table Listing
		while (type == null || postalCode == null || latitude == null || longitude == null) {
			System.out.print("Enter a type of listing (apartment, house, or room): ");
			input = sc.nextLine();
			if (!input.equals("apartment") && !input.equals("house") && !input.equals("room")) {
				System.out.println("That's not a valid type of listing, please try again!");
				continue;
			}
			type = input;

			System.out.print("Enter the street name: ");
			input = sc.nextLine();
			if (!isValid(input, 40)) {
				continue;
			}
			street = input;

			try {
				System.out.print("Enter the street number: ");
				input = sc.nextLine();
				if (!isValid(input, 6)) {
					continue;
				}
				number = Integer.parseInt(input);

				System.out.print("Enter the postal code: ");
				input = sc.nextLine();
				if (!isValid(input, 10)) {
					continue;
				}
				postalCode = input;

				System.out.print("Enter the country: ");
				input = sc.nextLine();
				if (!isValid(input, 56)) {
					continue;
				}
				country = input;

				System.out.print("Enter the city: ");
				input = sc.nextLine();
				if (!isValid(input, 20)) {
					continue;
				}
				city = input;

				System.out.print("Enter the latitude (in decimal values): ");
				input = sc.nextLine();
				if (!isValid(input, 7)) {
					continue;
				}
				latitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(input)));

				System.out.print("Enter the longitude (in decimal values): ");
				input = sc.nextLine();
				if (!isValid(input, 8)) {
					continue;
				}
				longitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(input)));
			} catch (NumberFormatException e) {
				System.out.println("That's not a number, please try again!");
			}
		}

		System.out.println("\nSelect any of the following amenities/characteristics:\n");
		printAmenities();
		System.out.println();
		while (traits == null) {
			System.out.print("Enter a list of amenities above separated by commas. (E.g., \"Wifi, Hot tub, Gym\"): ");
			input = sc.nextLine().strip().replaceAll("((?<=,)\\s||\\s(?=,))", "");
			if (!isValidAmenities(input)) {
				System.out.println("That's an invalid list, please try again!");
				continue;
			}
			traits = input;
		}

		int rows = sqlMngr.insertListing(type, street, number, postalCode, country, city, latitude, longitude, traits);
		System.out.println("\nListing rows affected: " + rows + "\n");
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
				"----------------------------------------------------------------------------------------------------------------------------");
		System.out.printf("| %-14s | %-80s | %-8s | %-9s |%n", "Type", "Address", "Latitude", "Longitude");
		System.out.println(
				"============================================================================================================================");
		for (Listing listing : listings) {
			count++;
			System.out.printf("| %d. %-11s | %-80s | %.4f  | %.4f   |%n", count, listing.type,
					listing.number + " " + listing.street + ", " +
							listing.city + ", " + listing.country + " " + listing.postalCode,
					listing.latitude.doubleValue(), listing.longitude.doubleValue());
		}
		System.out.println(
				"----------------------------------------------------------------------------------------------------------------------------\n");
	}

	private void searchListingByAddress() {
		String street, postalCode, country, city;
		int number = -1;

		System.out.println();
		System.out.println("Search for listings by entering the exact address");
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

	public void addOrderByDistance(ArrayList<Listing> listings, ArrayList<Double> distList, Listing listing,
			Double distance) {
		if (listings.size() == 0) {
			listings.add(listing);
			distList.add(distance);
			return;
		}

		for (int i = 0; i < distList.size(); i++) {
			if (distance < distList.get(i)) {
				listings.add(i, listing);
				distList.add(i, distance);
				return;
			}
		}
		// insert at the end of the list
		listings.add(listing);
		distList.add(distance);
	}

	// Calculation code from:
	// https://stackoverflow.com/questions/3694380/calculating-distance-between-two-points-using-latitude-longitude
	private ArrayList<Listing> calDistance(ArrayList<Listing> listings, double lat1, double long1, double distance) {
		ArrayList<Listing> filtered = new ArrayList<>();
		ArrayList<Double> distArr = new ArrayList<>();
		final int radius = 6371; // radius of earth in km

		for (Listing listing : listings) {
			double lat2 = listing.latitude.doubleValue();
			double long2 = listing.longitude.doubleValue();

			double latDistance = Math.toRadians(lat2 - lat1);
			double lonDistance = Math.toRadians(long2 - long1);
			double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) + Math.cos(Math.toRadians(lat1))
					* Math.cos(Math.toRadians(lat2)) * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
			double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
			double calDistance = radius * c;

			if (calDistance <= distance) {
				addOrderByDistance(filtered, distArr, listing, (Double) calDistance);
			}
		}
		return filtered;
	}

	private void searchListingByCoord() {
		BigDecimal latitude = null, longitude = null;
		double distance = -1;

		System.out.println();
		System.out.println(
				"Search for listings within a distance from a specified latitude and longitude, ranked by the distance");
		while (distance == -1) {
			try {
				System.out.print("Enter a latitude (in decimal values): ");
				latitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(sc.nextLine())));

				System.out.print("Enter a longitude (in decimal values): ");
				longitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(sc.nextLine())));

				String input;
				System.out.print(
						"Enter distance (in km) to the specified coordinates (Leave input empty for default value of 124km): ");
				input = sc.nextLine();
				if (input.strip().equals("")) {
					distance = 124;
				} else {
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

	private void searchListingByPostalCode() {
		String postalCode;

		System.out.println();
		System.out.println("Search for nearby listings by entering a postal code");
		System.out.print("Enter a postal code: ");
		postalCode = sc.nextLine();

		String pattern = postalCode.substring(0, postalCode.length() - 1) + "_";
		ArrayList<Listing> listings = sqlMngr.searchListingPostalCode(pattern);
		printListings(listings);
	}

	private void printHostedListings(ArrayList<Listing> hostedListings) {
		int count = 0;
		System.out.println("Listings You Host: " + hostedListings.size() + " listings\n");
		System.out.println("-----------------------------------------------------------------------"
				+ "----------------------------------------------------------------------");
		System.out.printf("| %-97s |%n", "Address of Listing");
		System.out.println("======================================================================="
				+ "======================================================================");
		for (Listing listing : hostedListings) {
			count++;
			System.out.printf("| %d. %d %-29s %-30s %-10s |%n", count, listing.number, listing.street, listing.country,
					listing.postalCode);
		}
		System.out.println("-----------------------------------------------------------------------"
				+ "----------------------------------------------------------------------\n");
	}

	private void addAvailabilities() {
		ArrayList<Listing> hostedListings = sqlMngr.selectHostsBySIN(this.currentUser.sin);
		LocalDate fromDate = null;
		LocalDate toDate = null;
		boolean available = true;
		BigDecimal price = null;
		String input;
		int choice = -1;

		while (!hostedListings.isEmpty() && (fromDate == null || toDate == null)) {
			System.out.println("Type \"0\" to exit");
			printHostedListings(hostedListings);
			System.out.printf("Choose a listing to add availabilities to [1-%d]: ", hostedListings.size());
			input = sc.nextLine().strip();
			try {
				choice = Integer.parseInt(input);
			} catch (NumberFormatException e) {
				input = "-1";
				System.out.println("That's not a number, please try again!");
			}
			if (choice == 0) {
				break;
			}
			
			if (choice < 1 || choice > hostedListings.size()) {
				System.out.println("That's not an option, please try again!");
				continue;
			}

			Listing listing = hostedListings.get(choice - 1);
			System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
					+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
			input = sc.nextLine().replaceAll("\\s", "");

			String[] dates = input.split(",");
			if (dates.length != 2) {
				System.out.println("That's not the right number of dates, please try again.");
				continue;
			}

			fromDate = checkValidAvailDate(dates[0]);
			toDate = checkValidAvailDate(dates[1]);
			if (fromDate == null || toDate == null || fromDate.isAfter(toDate)) {
				System.out.println("Those aren't proper dates, please try again!");
				fromDate = null;
				toDate = null;
				continue;
			}

			ArrayList<Availability> availabilities = sqlMngr.selectAvailBetweenDate(listing.street, listing.number,
					listing.postalCode, listing.country, toDate, fromDate, false);

			if (!availabilities.isEmpty()) {
				System.out.println("Those dates overlap with existing bookings, please try again!");
				continue;
			}

			System.out.print("Enter an availability [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {
				available = true;
			} else if (input.equalsIgnoreCase("n")) {
				available = false;
			} else {
				System.out.println("That's not a proper input, please try again!");
				fromDate = null;
				toDate = null;
				continue;
			}

			if (available) {
				System.out.print("Enter a price per day in decimal form: ");
				input = sc.nextLine().strip();
				try {
					price = new BigDecimal(priceDf.format(Double.parseDouble(input)));
				} catch (NumberFormatException e) {
					System.out.println("That's not a decimal, please try again!");
					fromDate = null;
					toDate = null;
					continue;
				}

				LocalDate tempDate = fromDate;
				int rows = 0;
				while (tempDate.isBefore(toDate) || tempDate.isEqual(toDate)) {
					rows += sqlMngr.insertOrUpdateAvailability(listing.street, listing.number, listing.postalCode,
							listing.country,
							tempDate, available, price);
					tempDate = tempDate.plusDays(1);
				}
				System.out.println("Availability rows affected: " + rows);
			} else {
				int rows = sqlMngr.deleteAvailBetweenDate(listing.street, listing.number, listing.postalCode,
						listing.country,
						toDate, fromDate, true);
				System.out.println("Availability rows deleted: " + rows);
			}
		}

		if (hostedListings.isEmpty()) {
			System.out.println("You host no listings to add availabilities to!");
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
	 */

}
