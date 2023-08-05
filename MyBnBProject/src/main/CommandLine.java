package main;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Scanner;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.io.Console;
import java.math.BigDecimal;
import java.text.DecimalFormat;

public class CommandLine {

	// 'sqlMngr' is the object which interacts directly with MySQL
	private SQLController sqlMngr = null;
	// 'sc' is needed in order to scan the inputs provided by the user
	private Scanner sc = null;
	// current user, if signed in
	private User currentUser = null;

	Password passMethods = new Password();
	Print printMethods = new Print();

	private Console console = System.console();

	DecimalFormat coordinatesDf = new DecimalFormat("#.####");
	DecimalFormat priceDf = new DecimalFormat("#.##");

	private ArrayList<String> amenities = new ArrayList<>(Arrays.asList("wifi", "kitchen", "washer", "dryer",
			"air conditioning", "heating", "dedicated workspace", "tv", "hair dryer", "iron", "pool", "hot tub",
			"free parking", "ev charger", "crib", "gym", "bbq grill", "breakfast", "indoor fireplace",
			"smoking allowed", "beachfront", "waterfront", "ski-in/ski-out", "smoke alarm", "carbon monoxide alarm"));

	private ArrayList<String> paymentMethods = new ArrayList<>(Arrays.asList("credit card", "debit card", "cash"));

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

	// Loop through and execute menu options
	private String runMenuOptions() {
		printMethods.menu(); // Print Menu
		String input = sc.nextLine().strip();
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
				case 3:
					String reportInput;
					do {
						reportInput = this.runReportOptions();
					} while (reportInput.compareTo("0") != 0);
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

	private String runReportOptions() {
		printMethods.reportOptions(); // Print report options
		String input = sc.nextLine().strip();
		try {
			int choice = Integer.parseInt(input);
			// Activate the desired functionality
			switch (choice) {
				case 0:
					break;
				case 1:
					reportNumBookings();
					break;
				case 2:
					reportRenterBookings();
					break;
				case 3:
					reportNumListings();
					break;
				case 4:
					reportRankHost();
					break;
				case 5:
					reportHost10Percent();
					break;
				case 6:
					reportNumCancelled();
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

	// Loop through and execute user menu options
	private String runUserMenuOptions() {
		ArrayList<Booked> updatedBooked = sqlMngr.selectUpdatedBookedBySIN(currentUser.sin);
		boolean updated = false;
		if (!updatedBooked.isEmpty()) {
			updated = true;
		}
		printMethods.userMenu(currentUser.name != null ? currentUser.name : "", updated); // Print Menu
		String input = sc.nextLine().strip();
		if (updated && input.equalsIgnoreCase("u")) {
			printMethods.printBookedWithCanceled(updatedBooked, true);
			sqlMngr.updateBookedStatusBySIN(currentUser.sin);
			System.out.print("Enter to continue. ");
			sc.nextLine();
			return input;
		}

		try {
			int choice = Integer.parseInt(input);
			// Choose the desired functionality
			switch (choice) {
				case 0:
					break;
				case 1:
					this.createListing();
					break;
				case 2:
					this.addAvailabilities();
					break;
				case 3:
					this.changeAvailabilityPrice();
					break;
				case 4:
					this.deleteListing();
					break;
				case 5:
					String searchInput;
					do {
						searchInput = this.runSearchOptions();
					} while (searchInput.compareTo("0") != 0);
					break; // remember to add ability to view comments here
				case 6:
					this.viewBookings();
					break;
				case 7:
					this.cancelBooked();
					break;
				case 8:
					this.viewListingBookings(); // remember to add ability to view comments here
					break;
				case 9:
					this.cancelListingBooked();
					break;
				case 10:
					this.commentOnListing();
					break;
				case 11:
					this.commentOnUser();
					break;
				case 12:
					this.viewComments();
					break;
				case 13:
					this.signOut();
					break;
				case 14:
					this.deleteUser();
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

	private String runSearchOptions() {
		printMethods.searchOptions(); // Print search options
		String input = sc.nextLine().strip();
		ArrayList<AvailabilityListing> listings = null;
		try {
			int choice = Integer.parseInt(input);
			// Activate the desired functionality
			switch (choice) {
				case 0:
					break;
				case 1:
					listings = this.searchListingByAddress();
					break;
				case 2:
					listings = this.searchListingByCoord();
					break;
				case 3:
					listings = this.searchListingByPostalCode();
					break;
				default:
					System.out.println("That's not an option, please try again!");
					break;
			}
		} catch (NumberFormatException e) {
			input = "-1";
			System.out.println("That's not a number, please try again!");
		}

		if (listings != null) {
			this.bookListing(listings);
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

		if (console == null) {
			System.out.print("Error! Cannot get Console instance\nPassword: ");
			cred[1] = sc.nextLine();
		} else {
			char[] password = console.readPassword("Password: ");
			cred[1] = new String(password);
		}
		return cred;
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
		user.salt = passMethods.getSalt();
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

				if (console == null) {
					System.out.print("Error! Cannot get Console instance\nEnter password (8-250 characters): ");
					input = sc.nextLine();
				} else {
					char[] passwordChar = console.readPassword("Enter password (8-250 characters): ");
					input = new String(passwordChar);
				}
				// System.out.print("Enter password (8-250 characters): ");
				// input = sc.nextLine();
				int tempLen = input.length();
				if (tempLen >= 8 && tempLen <= 250) {
					user.password = passMethods.getSaltHashedPassword(input, user.salt);
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
				System.out.print("Enter a SIN or 0 to exit: ");
				input = sc.nextLine().strip();
				if (input.equals("0")) {
					return;
				}
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

		if (console == null) {
			System.out.print("Error! Cannot get Console instance\nEnter password: ");
			input = sc.nextLine();
		} else {
			char[] passwordChar = console.readPassword("Enter password: ");
			input = new String(passwordChar);
		}
		String password = passMethods.getSaltHashedPassword(input, user.salt);

		if (password.equals(user.password)) {
			System.out.println("You are signed in!");
			this.currentUser = user;
		} else {
			System.out.println("Wrong password!");
		}
	}

	// Function that handles the feature: "Sign out."
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

	// Function that handles the feature: "Create a listing."
	private void createListing() {
		Listing listing = new Listing();
		String input;

		System.out.println();
		// insert new row to table Listing
		while (listing.type == null || listing.postalCode == null || listing.latitude == null
				|| listing.longitude == null) {
			System.out.print("Enter a type of listing (apartment, house, or room): ");
			input = sc.nextLine().strip().toLowerCase();
			if (!input.equals("apartment") && !input.equals("house") && !input.equals("room")) {
				System.out.println("That's not a valid type of listing, please try again!");
				continue;
			}
			listing.type = input;

			System.out.print("Enter the street name: ");
			input = sc.nextLine();
			if (!isValid(input, 40)) {
				continue;
			}
			listing.street = input;

			try {
				System.out.print("Enter the street number: ");
				input = sc.nextLine();
				if (!isValid(input, 6)) {
					continue;
				}
				listing.number = Integer.parseInt(input);

				System.out.print("Enter the postal code: ");
				input = sc.nextLine();
				if (!isValid(input, 10)) {
					continue;
				}
				listing.postalCode = input;

				System.out.print("Enter the country: ");
				input = sc.nextLine();
				if (!isValid(input, 56)) {
					continue;
				}
				listing.country = input;

				System.out.print("Enter the city: ");
				input = sc.nextLine();
				if (!isValid(input, 20)) {
					continue;
				}
				listing.city = input;

				System.out.print("Enter the latitude (in decimal values): ");
				input = sc.nextLine();
				if (!isValid(input, 8)) {
					continue;
				} else if (Double.parseDouble(input) < -90 || Double.parseDouble(input) > 90) {
					System.out.println("Latitude in decimal format range from -90 to 90. Try again!");
					continue;
				}
				listing.latitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(input)));

				System.out.print("Enter the longitude (in decimal values): ");
				input = sc.nextLine();
				if (!isValid(input, 9)) {
					continue;
				} else if (Double.parseDouble(input) < -180 || Double.parseDouble(input) > 180) {
					System.out.println("Longitude in decimal format range from -180 to 180. Try again!");
					continue;
				}

				listing.longitude = new BigDecimal(coordinatesDf.format(Double.parseDouble(input)));
			} catch (NumberFormatException e) {
				System.out.println("That's not a number, please try again!");
			}
		}

		System.out.println("\nSelect any of the following amenities/characteristics:\n");
		printMethods.printAmenities();
		System.out.println();
		// print suggestions for amenities based on listing type
		if (listing.type.equals("apartment")) {
			System.out.println(
					"Suggestions for apartments: Wifi, Kitchen, Washer, Dryer, Air conditioning, Heating, Dedicated workspace, \n"
							+ "TV, Pool, Free parking, Beachfront, Waterfront, Smoke alarm\n");
		} else if (listing.type.equals("house")) {
			System.out.println(
					"Suggestions for houses: Wifi, Kitchen, Washer, Dryer, Air conditioning, Heating, Dedicated workspace, \n"
							+ "TV, Free parking, Smoke alarm, Carbon monoxide alarm\n");
		} else {
			System.out.println(
					"Suggestions for rooms: Wifi, Kitchen, Washer, Air conditioning, Heating, Dedicated workspace, Free parking\n");
		}

		while (listing.amenities == null) {
			System.out.print("Enter a list of amenities above separated by commas. (E.g., \"Wifi, Hot tub, Gym\"): ");
			input = sc.nextLine().strip().replaceAll("((?<=,)\\s||\\s(?=,))", "");
			if (!isValidAmenities(input)) {
				System.out.println("That's an invalid list, please try again!");
				continue;
			}
			listing.amenities = input;
		}

		int rows = sqlMngr.insertListing(listing.type, listing.street, listing.number, listing.postalCode,
				listing.country, listing.city, listing.latitude, listing.longitude, listing.amenities);
		System.out.println("\nListing rows affected: " + rows + "\n");
		// inserting to Listing failed.
		if (rows == 0) {
			return;
		}
		// insert new row to table Hosts
		rows = sqlMngr.insertHosts(currentUser.sin, listing.street, listing.number, listing.postalCode,
				listing.country);
	}

	// Function that handles the feature: "Delete a listing."
	private void deleteListing() {
		String input;
		int choice = -1;
		System.out.println("\nNOTE: Active bookings will automatically be canceled when deleting a listing.\n");
		while (choice != 0) {
			ArrayList<Listing> listings = sqlMngr.selectHostsBySIN(currentUser.sin);
			printMethods.printHostedListings(listings);
			System.out.printf("Choose a listing you want to delete [1-%d] or enter 0 to exit: ", listings.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, listings.size(), true)) {
				continue;
			}
			choice = Integer.parseInt(input);
			if (choice == 0) {
				return;
			}

			System.out.print("Are you sure you want to delete this listing? [y/n]: ");
			input = sc.nextLine().strip();

			if (!input.equalsIgnoreCase("y") && !input.equalsIgnoreCase("n")) {
				System.out.println("That's not a valid input, please try again!");
				continue;
			} else if (input.equalsIgnoreCase("n")) {
				break;
			}

			Listing listing = listings.get(choice - 1);
			int rows = sqlMngr.cancelBookedByListing(listing.street, listing.number, listing.postalCode,
					listing.country, true);
			System.out.println("\nBooked rows affected: " + rows + "\n");
			ArrayList<Booked> bookings = sqlMngr.selectBookedByAddress(listing.street, listing.number,
					listing.postalCode, listing.country);
			rows = sqlMngr.insertCancellation(currentUser.sin, bookings);
			System.out.println("\nCancellation rows affected: " + rows + "\n");
			rows = sqlMngr.deleteListing(listing.street, listing.number, listing.postalCode, listing.country);
			System.out.println("\nListing rows affected: " + rows + "\n");
			choice = 0;
		}
	}

	private ArrayList<AvailabilityListing> filterByAmenities(ArrayList<AvailabilityListing> listings,
			String amenities) {
		ArrayList<AvailabilityListing> filtered = new ArrayList<>();
		boolean contains = true;
		String temp;

		for (AvailabilityListing listing : listings) {
			for (String word : amenities.split(",")) {
				if (word.equalsIgnoreCase("TV")) {
					temp = "TV";
				} else if (word.equalsIgnoreCase("EV charger")) {
					temp = "EV charger";
				} else if (word.equalsIgnoreCase("BBQ grill")) {
					temp = "BBQ grill";
				} else {
					temp = word.substring(0, 1).toUpperCase() + word.substring(1);
				}

				if (!listing.amenities.contains(temp)) {
					contains = false;
					break;
				}
			}
			if (contains) {
				filtered.add(listing);
			}
			contains = true;
		}

		return filtered;
	}

	private ArrayList<AvailabilityListing> askFilterAmenities(ArrayList<AvailabilityListing> listings) {
		String amenities = null, input;

		while (amenities == null) {
			System.out.print("Do you want to use an amenities filter? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {
				System.out.println();
				printMethods.printAmenities();
				System.out.println();

				while (amenities == null) {
					System.out.print(
							"Enter a list of amenities above separated by commas. (E.g., \"Wifi, Hot tub, Gym\"): ");
					input = sc.nextLine().strip().replaceAll("((?<=,)\\s||\\s(?=,))", "");
					if (!isValidAmenities(input)) {
						System.out.println("That's an invalid list, please try again!");
						continue;
					}
					amenities = input;
				}
				return filterByAmenities(listings, amenities);
			} else if (input.equalsIgnoreCase("n")) {
				break;
			} else {
				System.out.println("That's not a proper input, please try again!");
				continue;
			}
		}
		return listings;
	}

	private void rankByPrice(ArrayList<AvailabilityListing> availListing, String order) {
		int size = availListing.size();

		for (int i = 0; i < size; i++) {
			for (int j = i + 1; j < size; j++) {
				int compareRes = availListing.get(i).price.compareTo(availListing.get(j).price);

				if ((order.equals("a") && compareRes > 0) || (order.equals("d") && compareRes < 0)) {
					AvailabilityListing swap = availListing.get(i);
					availListing.set(i, availListing.get(j));
					availListing.set(j, swap);
				}
			}
		}
	}

	private void askRankByPrice(ArrayList<AvailabilityListing> availListings) {
		String order = null, input;

		while (order == null) {
			System.out.print("Do you want to rank result by price? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {
				while (order == null) {
					System.out.print(
							"Enter either \"a\" for ascending order or \"d\" for descending order: ");
					input = sc.nextLine().strip();
					if (input.equalsIgnoreCase("a") || input.equalsIgnoreCase("d")) {
						order = input;
					} else {
						System.out.println("That's not a proper input, please try again!");
						continue;
					}
				}
				rankByPrice(availListings, order);
			} else if (input.equalsIgnoreCase("n")) {
				break;
			} else {
				System.out.println("That's not a proper input, please try again!");
				continue;
			}
		}
	}

	private ArrayList<AvailabilityListing> applyDateFilter(ArrayList<AvailabilityListing> availListings, LocalDate from,
			LocalDate to) {
		ArrayList<AvailabilityListing> newAvail = new ArrayList<>();

		availListings.forEach((availListing) -> {
			if (availListing.date.compareTo(from) >= 0 && availListing.date.compareTo(to) <= 0) {
				newAvail.add(availListing);
			}
		});

		return newAvail;
	}

	private ArrayList<AvailabilityListing> applyPriceFilter(ArrayList<AvailabilityListing> availListings,
			BigDecimal from, BigDecimal to) {
		ArrayList<AvailabilityListing> newAvail = new ArrayList<>();

		availListings.forEach((availListing) -> {
			if (availListing.price.compareTo(from) >= 0 && availListing.price.compareTo(to) <= 0) {
				newAvail.add(availListing);
			}
		});

		return newAvail;
	}

	private LocalDate[] askFilterDate() {
		String input;
		LocalDate[] dateFromTo = null;

		while (dateFromTo == null) {
			System.out.print("Do you want to use a date filter? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {

				while (dateFromTo == null) {
					System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
							+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
					input = sc.nextLine().replaceAll("\\s", "");

					dateFromTo = checkFromToDates(input);
				}
			} else if (input.equalsIgnoreCase("n")) {
				break;
			} else {
				System.out.println("That's not a proper input, please try again!");
				continue;
			}
		}
		return dateFromTo;
	}

	private BigDecimal[] checkFromToPrices(String input) {
		String[] prices = input.split(",");
		if (prices.length != 2) {
			System.out.println("That's not the right number of prices, please try again.");
			return null;
		}

		BigDecimal[] priceFromTo = new BigDecimal[2];
		try {
			priceFromTo[0] = new BigDecimal(coordinatesDf.format(Double.parseDouble(prices[0])));
			priceFromTo[1] = new BigDecimal(coordinatesDf.format(Double.parseDouble(prices[1])));
		} catch (NumberFormatException e) {
			System.out.println("Those aren't numeral values, please try again!");
			return null;
		}
		return priceFromTo;
	}

	private BigDecimal[] askFilterPrice() {
		String input;
		BigDecimal[] priceFromTo = null;

		while (priceFromTo == null) {
			System.out.print("Do you want to use a price filter? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {

				while (priceFromTo == null) {
					System.out.print("Enter a \"start\" price and an \"end\" price seperated by commas."
							+ " (E.g., \"10.99, 20.99\"): ");
					input = sc.nextLine().replaceAll("\\s", "");

					priceFromTo = checkFromToPrices(input);
				}
			} else if (input.equalsIgnoreCase("n")) {
				break;
			} else {
				System.out.println("That's not a proper input, please try again!");
				continue;
			}
		}
		return priceFromTo;
	}

	private ArrayList<AvailabilityListing> searchListingByAddress() {
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

		ArrayList<AvailabilityListing> availListings = sqlMngr.searchListingAddr(street, number, postalCode, country,
				city);
		LocalDate[] dateFromTo = askFilterDate();
		BigDecimal[] priceFromTo = askFilterPrice();

		if (dateFromTo != null) { // apply date filter
			availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
		}

		if (priceFromTo != null) { // apply price filter
			availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
		}
		availListings = askFilterAmenities(availListings);
		askRankByPrice(availListings);
		printMethods.printFilteredListings(availListings);
		return availListings;
	}

	public void addOrderByDistance(ArrayList<AvailabilityListing> listings, ArrayList<Double> distList,
			AvailabilityListing listing, Double distance) {
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
	private ArrayList<AvailabilityListing> calDistance(ArrayList<AvailabilityListing> listings, double lat1,
			double long1, double distance) {
		ArrayList<AvailabilityListing> filtered = new ArrayList<>();
		ArrayList<Double> distArr = new ArrayList<>();
		final int radius = 6371; // radius of earth in km

		listings.forEach((listing) -> {
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
		});
		return filtered;
	}

	private ArrayList<AvailabilityListing> searchListingByCoord() {
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

		ArrayList<AvailabilityListing> availListings = sqlMngr.searchAllListing();
		LocalDate[] dateFromTo = askFilterDate();
		BigDecimal[] priceFromTo = askFilterPrice();

		if (dateFromTo != null) { // apply date filter
			availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
		}

		if (priceFromTo != null) { // apply price filter
			availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
		}

		availListings = calDistance(availListings, latitude.doubleValue(), longitude.doubleValue(), distance);
		availListings = askFilterAmenities(availListings);
		askRankByPrice(availListings);
		printMethods.printFilteredListings(availListings);
		return availListings;
	}

	private ArrayList<AvailabilityListing> searchListingByPostalCode() {
		String postalCode;

		System.out.println();
		System.out.println("Search for nearby listings by entering a postal code");
		System.out.print("Enter a postal code: ");
		postalCode = sc.nextLine();

		String pattern = postalCode.substring(0, postalCode.length() - 1) + "_";
		ArrayList<AvailabilityListing> availListings = sqlMngr.searchListingPostalCode(pattern);
		LocalDate[] dateFromTo = askFilterDate();
		BigDecimal[] priceFromTo = askFilterPrice();

		if (dateFromTo != null) { // apply date filter
			availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
		}

		if (priceFromTo != null) { // apply price filter
			availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
		}
		availListings = askFilterAmenities(availListings);
		askRankByPrice(availListings);
		printMethods.printFilteredListings(availListings);
		return availListings;
	}

	private boolean checkInputClamp(String input, int size, boolean isZeroAllowed) {
		int choice = -1;
		try {
			choice = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("That's not a number, please try again!");
			return false;
		}

		if (choice == 0 && !isZeroAllowed) {
			System.out.println("That's not an option, please try again!");
			return false;
		}

		if (choice < 0 || choice > size) {
			System.out.println("That's not an option, please try again!");
			return false;
		}

		return true;
	}

	private LocalDate[] checkFromToDates(String input) {
		String[] dates = input.split(",");
		if (dates.length != 2) {
			System.out.println("That's not the right number of dates, please try again.");
			return null;
		}
		LocalDate[] dateFromTo = new LocalDate[2];
		dateFromTo[0] = checkValidAvailDate(dates[0]);
		dateFromTo[1] = checkValidAvailDate(dates[1]);
		if (dateFromTo[0] == null || dateFromTo[1] == null || dateFromTo[0].isAfter(dateFromTo[1])) {
			System.out.println("Those aren't proper dates, please try again!");
			return null;
		}
		return dateFromTo;
	}

	private void addAvailabilities() {
		ArrayList<Listing> hostedListings = sqlMngr.selectHostsBySIN(this.currentUser.sin);
		LocalDate[] dateFromTo = null;
		boolean available = true;
		BigDecimal price = null;
		String input;
		int choice = -1;

		while (!hostedListings.isEmpty() && dateFromTo == null) {
			printMethods.printHostedListings(hostedListings);
			System.out.printf("Choose a listing to add availabilities to [1-%d] or enter 0 to exit: ",
					hostedListings.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, hostedListings.size(), true)) {
				continue;
			}

			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			Listing listing = hostedListings.get(choice - 1);
			System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
					+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
			input = sc.nextLine().replaceAll("\\s", "");

			dateFromTo = checkFromToDates(input);
			if (dateFromTo == null) {
				dateFromTo = null;
				continue;
			}

			ArrayList<Availability> availabilities = sqlMngr.selectAvailBetweenDate(listing.street, listing.number,
					listing.postalCode, listing.country, dateFromTo[0], dateFromTo[1], false);

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
				dateFromTo = null;
				continue;
			}

			if (available) {
				ArrayList<Double> result = sqlMngr.getPriceRange(listing.type);
				System.out.println("\nSuggested price range for " + listing.type + ": $" + priceDf.format(result.get(0))
						+ " - $" + priceDf.format(result.get(1)));

				System.out.print("Enter a price per day in decimal form: ");
				input = sc.nextLine().strip();
				try {
					price = new BigDecimal(priceDf.format(Double.parseDouble(input)));
				} catch (NumberFormatException e) {
					System.out.println("That's not a decimal, please try again!");
					dateFromTo = null;
					continue;
				}

				LocalDate tempDate = dateFromTo[0];
				int rows = 0;
				while (tempDate.isBefore(dateFromTo[1]) || tempDate.isEqual(dateFromTo[1])) {
					rows += sqlMngr.insertOrUpdateAvailability(listing.street, listing.number, listing.postalCode,
							listing.country, tempDate, available, price);
					tempDate = tempDate.plusDays(1);
				}
				System.out.println("Availability rows affected: " + rows);
			} else {
				int rows = sqlMngr.deleteAvailBetweenDate(listing.street, listing.number, listing.postalCode,
						listing.country, dateFromTo[0], dateFromTo[1], true);
				System.out.println("Availability rows deleted: " + rows);
			}
		}

		if (hostedListings.isEmpty()) {
			System.out.println("You host no listings to add availabilities to!");
		}
	}

	private void changeAvailabilityPrice() {
		ArrayList<Listing> hostedListings = sqlMngr.selectHostsBySIN(this.currentUser.sin);
		LocalDate[] dateFromTo = null;
		BigDecimal price = null;
		String input;
		int choice = -1;

		while (!hostedListings.isEmpty() && dateFromTo == null) {
			printMethods.printHostedListings(hostedListings);
			System.out.printf("Choose a listing to change the price of [1-%d] or enter 0 to exit: ",
					hostedListings.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, hostedListings.size(), true)) {
				continue;
			}

			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			Listing listing = hostedListings.get(choice - 1);
			System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
					+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
			input = sc.nextLine().replaceAll("\\s", "");

			dateFromTo = checkFromToDates(input);
			if (dateFromTo == null) {
				dateFromTo = null;
				continue;
			}

			ArrayList<Availability> availabilities = sqlMngr.selectAvailBetweenDate(listing.street, listing.number,
					listing.postalCode, listing.country, dateFromTo[0], dateFromTo[1]);

			if (availabilities.isEmpty()) {
				System.out.println("Those dates don't have availabilities, please try again!");
				continue;
			}

			System.out.print("Enter a price per day in decimal form: ");
			input = sc.nextLine().strip();
			try {
				price = new BigDecimal(priceDf.format(Double.parseDouble(input)));
			} catch (NumberFormatException e) {
				System.out.println("That's not a decimal, please try again!");
				dateFromTo = null;
				continue;
			}

			int rows = 0;
			boolean existBooked = false;
			for (Availability availability : availabilities) {
				if (!availability.available) {
					existBooked = true;
				}
				rows += sqlMngr.updateAvailability(listing.street, listing.number, listing.postalCode,
						listing.country, availability.date, price);
			}
			System.out.println("Availability rows affected: " + rows);

			rows = 0;
			if (existBooked) {
				ArrayList<Booked> booked = sqlMngr.selectBookedByAddressDate(listing.street, listing.number,
						listing.postalCode, listing.country, dateFromTo[0], dateFromTo[1], false);

				for (Booked booking : booked) {
					BigDecimal totalPrice = sqlMngr.sumAvailBetweenDate(listing.street, listing.number,
							listing.postalCode, listing.country, booking.fromDate, booking.toDate);
					rows += sqlMngr.updateBookedPrice(booking.bid, totalPrice, true);
				}
			}
			System.out.println("Booked rows affected: " + rows);
		}

		if (hostedListings.isEmpty()) {
			System.out.println("You host no listings to change the price of!");
		}
	}

	private boolean isValidPaymentMethod(String input) {
		if (paymentMethods.contains(input.toLowerCase())) {
			return true;
		}
		return false;
	}

	private void printPaymentMethods() {
		System.out.print("Payment methods: ");
		for (int i = 0; i < paymentMethods.size(); i++) {
			System.out.print(paymentMethods.get(i));
			if (i != paymentMethods.size() - 1) {
				System.out.print(", ");
			}
		}
		System.out.println("");
	}

	private ArrayList<AvailabilityListing> getConsecutiveListings(ArrayList<AvailabilityListing> booked,
			ArrayList<AvailabilityListing> listings) {
		ArrayList<AvailabilityListing> sameListings = new ArrayList<AvailabilityListing>();
		for (AvailabilityListing listing : listings) {
			if (listing.street.equals(booked.get(0).street) && listing.number == booked.get(0).number
					&& listing.postalCode.equals(booked.get(0).postalCode)
					&& listing.country.equals(booked.get(0).country)) {
				sameListings.add(listing);
			}
		}
		Collections.sort(sameListings, new Comparator<AvailabilityListing>() {
			@Override
			public int compare(AvailabilityListing o1, AvailabilityListing o2) {
				return o1.date.compareTo(o2.date);
			}
		});
		ArrayList<AvailabilityListing> consecutiveListings = new ArrayList<AvailabilityListing>();
		LocalDate tempDate = null;
		int count = 0;
		while (count < sameListings.size()) {
			if (sameListings.get(count).date.isEqual(booked.get(booked.size() - 1).date.plusDays(1))) {
				tempDate = sameListings.get(count).date;
			}
			if (tempDate != null && sameListings.get(count).date.isEqual(tempDate)) {
				consecutiveListings.add(sameListings.get(count));
				count++;
				tempDate = tempDate.plusDays(1);
			} else if (tempDate != null) {
				break;
			}
		}

		return consecutiveListings;
	}

	private void bookListing(ArrayList<AvailabilityListing> listings) {
		viewListingComments(listings);

		String input = "";
		while (!listings.isEmpty()) {
			System.out.print("Do you want to book a listing? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("y")) {
				break;
			} else {
				System.out.println("That's not proper input, please try again!");
			}
		}

		int choice = -1;
		if (input.equalsIgnoreCase("y")) {
			int rows = 0;
			while (rows == 0) {
				ArrayList<AvailabilityListing> bookedListings = new ArrayList<AvailabilityListing>();
				ArrayList<AvailabilityListing> printListings = new ArrayList<AvailabilityListing>();
				printListings.addAll(listings);
				while (input.equalsIgnoreCase("y")) {
					if (!bookedListings.isEmpty()) {
						System.out.println("old size: " + printListings.size());
						printListings.removeAll(printListings);
						System.out.println("new size: " + printListings.size());
						printListings = getConsecutiveListings(bookedListings, listings);
						System.out.println("consecutive size: " + printListings.size());

						if (printListings.isEmpty()) {
							System.out.print(
									"No more consecutive days available for this listing! Enter to continue. ");
							sc.nextLine();
							break;
						}
						printMethods.printFilteredListings(printListings);
						System.out.println("Choosing a date will book all consecutive days to that date.");
					}

					System.out.printf("Choose %s to book [1-%d]: ", bookedListings.isEmpty() ? "a listing" : "dates",
							printListings.size());
					input = sc.nextLine().replaceAll("\\s", "");
					if (!checkInputClamp(input, printListings.size(), true)) {
						continue;
					}
					choice = Integer.parseInt(input);
					if (choice == 0) {
						break;
					}

					if (!bookedListings.isEmpty()) {
						for (int i = 0; i < choice; i++) {
							bookedListings.add(printListings.get(i));
							listings.remove(printListings.get(i));
						}
					} else {
						bookedListings.add(printListings.get(choice - 1));
						listings.remove(printListings.get(choice - 1));
					}

					System.out.print("Add more days to booking? [y/n]: ");
					input = sc.nextLine().strip();
				}

				if (choice == 0) {
					break;
				}

				BigDecimal totalPrice = new BigDecimal("0");
				for (AvailabilityListing listing : bookedListings) {
					totalPrice = totalPrice.add(listing.price);
				}

				System.out.println("Total price: " + totalPrice);
				printPaymentMethods();
				System.out.print("Enter a payment method: ");
				input = sc.nextLine().strip();

				if (!isValidPaymentMethod(input)) {
					System.out.println("That's not a valid payment method, please try again!");
					continue;
				}

				rows = sqlMngr.insertBooked(currentUser.sin, bookedListings.get(0).street,
						bookedListings.get(0).number,
						bookedListings.get(0).postalCode,
						bookedListings.get(0).country, bookedListings.get(0).date,
						bookedListings.get(bookedListings.size() - 1).date, input, totalPrice);

				System.out.println("Booking rows affected: " + rows);

				rows = sqlMngr.updateAvailability(bookedListings.get(0).street, bookedListings.get(0).number,
						bookedListings.get(0).postalCode, bookedListings.get(0).country, bookedListings.get(0).date,
						bookedListings.get(bookedListings.size() - 1).date, false);
				System.out.println("Availability rows affected: " + rows);
			}
		}
	}

	private void viewListingComments(ArrayList<AvailabilityListing> listings) {
		String input = "";
		while (input.equals("")) {
			System.out.print("Do you want to view comments for a listing? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("n") || input.equalsIgnoreCase("y")) {
				break;
			} else {
				System.out.println("That's not proper input, please try again!");
				input = "";
			}
		}

		while (input.equalsIgnoreCase("y")) {
			System.out.printf("Choose a listing to view comments [1-%d]: ", listings.size());
			input = sc.nextLine().replaceAll("\\s", "");
			if (!checkInputClamp(input, listings.size(), true)) {
				continue;
			}
			int choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			ArrayList<Comment> comments = sqlMngr.selectCommentByListing(listings.get(choice - 1).street,
					listings.get(choice - 1).number, listings.get(choice - 1).postalCode,
					listings.get(choice - 1).country);
			printMethods.printComments(comments);
			System.out.print("View comments for another listing? [y/n]: ");
			input = sc.nextLine().strip();
			if (!(input.equalsIgnoreCase("n") || input.equalsIgnoreCase("y"))) {
				System.out.println("That's not proper input!");
				input = "y";
			}
		}
	}

	private void viewBookings() {
		String input = "";
		while (input.equals("")) {
			printMethods.printViewBookingOptions();
			input = sc.nextLine().strip();
			if (input.equals("1") || input.equals("2") || input.equals("3")) {
				break;
			} else {
				System.out.println("That's not a valid option, please try again!");
				input = "";
			}
		}

		ArrayList<Booked> booked;

		switch (input) {
			case "1":
				booked = sqlMngr.selectBookedBySIN(currentUser.sin, false);
				if (booked.isEmpty()) {
					System.out.println("You have no active bookings!");
					return;
				}
				printMethods.printBooked(booked);
				break;
			case "2":
				booked = sqlMngr.selectBookedBySIN(currentUser.sin, true);
				if (booked.isEmpty()) {
					System.out.println("You have no canceled bookings!");
					return;
				}
				printMethods.printBooked(booked);
				break;
			case "3":
				booked = sqlMngr.selectBookedBySIN(currentUser.sin);
				if (booked.isEmpty()) {
					System.out.println("You have no bookings!");
					return;
				}
				printMethods.printBookedWithCanceled(booked, false);
				break;
		}

		System.out.print("Enter to continue. ");
		sc.nextLine();
	}

	private void cancelBooked() {
		String input = "";
		ArrayList<Booked> booked = sqlMngr.selectBookedBySIN(currentUser.sin, false);
		if (booked.isEmpty()) {
			System.out.println("You have no bookings to cancel!");
			return;
		}

		int choice = -1;
		Booked booking = null;
		while (booking == null) {
			printMethods.printBooked(booked);
			System.out.printf("Choose a booking to cancel [1-%d] or enter 0 to exit: ", booked.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, booked.size(), true)) {
				continue;
			}
			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			booking = booked.get(choice - 1);

			int rows = sqlMngr.cancelBooked(booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate, booking.toDate, false);
			System.out.println("Booking rows affected: " + rows);
			ArrayList<Booked> temp = new ArrayList<>();
			temp.add(booking);
			rows = sqlMngr.insertCancellation(currentUser.sin, temp);
			System.out.println("Cancellation rows affected: " + rows);

			rows = sqlMngr.updateAvailability(booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate, booking.toDate, true);
			System.out.println("Availability rows affected: " + rows);
		}
	}

	private void viewListingBookings() {
		String input = "";
		while (input.equals("")) {
			printMethods.printViewBookingOptions();
			input = sc.nextLine().strip();
			if (input.equals("1") || input.equals("2") || input.equals("3")) {
				break;
			} else {
				System.out.println("That's not a valid option, please try again!");
				input = "";
			}
		}

		ArrayList<Booked> booked = new ArrayList<>();

		switch (input) {
			case "1":
				booked = sqlMngr.selectBookedByHostListings(currentUser.sin, false);
				if (booked.isEmpty()) {
					System.out.println("You have no active bookings for your listings!");
					return;
				}
				printMethods.printBookedWithSIN(booked);
				break;
			case "2":
				booked = sqlMngr.selectBookedByHostListings(currentUser.sin, true);
				if (booked.isEmpty()) {
					System.out.println("You have no canceled bookings for your listings!");
					return;
				}
				printMethods.printBookedWithSIN(booked);
				break;
			case "3":
				booked = sqlMngr.selectBookedByHostListings(currentUser.sin);
				if (booked.isEmpty()) {
					System.out.println("You have no bookings for your listings!");
					return;
				}
				printMethods.printBookedWithSINCanceled(booked, false);
				break;
		}

		viewUserComments(booked);

		System.out.print("Enter to continue. ");
		sc.nextLine();
	}

	private void viewUserComments(ArrayList<Booked> booked) {
		String input = "";
		while (input.equals("")) {
			System.out.print("Would you like to view comments of a booked user [y/n]? ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y") || input.equalsIgnoreCase("n")) {
				break;
			} else {
				System.out.println("That's not a valid option, please try again!");
				input = "";
			}
		}

		if (input.equalsIgnoreCase("y")) {
			input = "";
			int choice = -1;
			Booked booking = null;
			while (booking == null) {
				System.out.printf("Choose a booking to view user comments of [1-%d] or enter 0 to exit: ",
						booked.size());
				input = sc.nextLine().strip();
				if (!checkInputClamp(input, booked.size(), true)) {
					continue;
				}
				choice = Integer.parseInt(input);
				if (choice == 0) {
					break;
				}

				booking = booked.get(choice - 1);
				ArrayList<Comment> comments = sqlMngr.selectCommentByUser(booking.sin);
				if (comments.isEmpty()) {
					System.out.println("This user has no comments!");
					return;
				}
				printMethods.printComments(comments);
			}
		}
	}

	private void cancelListingBooked() {
		String input = "";
		ArrayList<Booked> booked = sqlMngr.selectBookedByHostListings(currentUser.sin, false);
		if (booked.isEmpty()) {
			System.out.println("You have no bookings to cancel!");
			return;
		}

		int choice = -1;
		Booked booking = null;
		while (booking == null) {
			printMethods.printBooked(booked);
			System.out.printf("Choose a booking to cancel [1-%d] or enter 0 to exit: ", booked.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, booked.size(), true)) {
				continue;
			}
			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			booking = booked.get(choice - 1);

			int rows = sqlMngr.cancelBooked(booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate, booking.toDate, true);
			System.out.println("Booking rows affected: " + rows);
			ArrayList<Booked> temp = new ArrayList<>();
			temp.add(booking);
			rows = sqlMngr.insertCancellation(currentUser.sin, temp);
			System.out.println("Cancellation rows affected: " + rows);

			rows = sqlMngr.updateAvailability(booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate, booking.toDate, true);
			System.out.println("Availability rows affected: " + rows);
		}
	}

	private void commentOnListing() {
		ArrayList<Listing> listings = sqlMngr.selectListingByBooked(currentUser.sin);
		if (listings.isEmpty()) {
			System.out.println("You have booked no listings and cannot comment!");
			return;
		}

		String input = "";
		int choice = -1;
		Comment comment = new Comment();
		while (comment.sin == null) {
			printMethods.printListings(listings);
			System.out.printf("Choose a listing to comment on [1-%d] or enter 0 to exit: ", listings.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, listings.size(), true)) {
				continue;
			}
			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			Listing listing = listings.get(choice - 1);
			System.out.print("Enter a rating (1-5): ");
			input = sc.nextLine().strip();
			if (input.length() != 0) {
				if (!checkInputClamp(input, 5, false)) {
					continue;
				}

				comment.rating = Integer.parseInt(input);
			}

			System.out.print("Enter a comment: ");
			comment.text = sc.nextLine().strip();
			comment.sin = currentUser.sin;

			int rows = sqlMngr.insertCommentOnListing(comment.sin, comment.rating, comment.text, listing.street,
					listing.number, listing.postalCode, listing.country);
			System.out.println("CommentOnListing rows affected: " + rows);
		}
	}

	private void commentOnUser() {
		ArrayList<User> users = sqlMngr.selectUserByListingBooked(currentUser.sin);
		if (users.isEmpty()) {
			System.out.println("Nobody has booked your listings so you cannot comment!");
			return;
		}

		String input = "";
		int choice = -1;
		Comment comment = new Comment();
		while (comment.sin == null) {
			printMethods.printUsers(users);
			System.out.printf("Choose a user to comment on [1-%d] or enter 0 to exit: ", users.size());
			input = sc.nextLine().strip();
			if (!checkInputClamp(input, users.size(), true)) {
				continue;
			}
			choice = Integer.parseInt(input);
			if (choice == 0) {
				break;
			}

			User user = users.get(choice - 1);
			System.out.print("Enter a rating (1-5): ");
			input = sc.nextLine().strip();
			if (input.length() != 0) {
				if (!checkInputClamp(input, 5, false)) {
					continue;
				}

				comment.rating = Integer.parseInt(input);
			}

			System.out.print("Enter a comment: ");
			comment.text = sc.nextLine().strip();
			comment.sin = currentUser.sin;

			int rows = sqlMngr.insertCommentOnUser(comment.sin, comment.rating, comment.text, user.sin);
			System.out.println("CommentOnUser rows affected: " + rows);
		}
	}

	private void viewComments() {
		String input = "";
		while (input.equals("")) {
			printMethods.printViewCommentOptions();
			input = sc.nextLine().strip();
			if (input.equals("1") || input.equals("2") || input.equals("3")) {
				break;
			} else {
				System.out.println("That's not a valid option, please try again!");
				input = "";
			}
		}

		ArrayList<Comment> comments = null;

		switch (input) {
			case "1":
				comments = sqlMngr.selectCommentByUser(currentUser.sin);
				if (comments.isEmpty()) {
					System.out.println("You have no comments!");
					return;
				}
				printMethods.printComments(comments);
				break;
			case "2":
				comments = sqlMngr.selectCommentMadeByUser(currentUser.sin);
				if (comments.isEmpty()) {
					System.out.println("You have written no comments!");
					return;
				}
				printMethods.printCommentsUserListing(comments);
				break;
			case "3":
				input = "";
				ArrayList<Listing> listings = null;
				while (input.equals("")) {
					listings = sqlMngr.selectHostsBySIN(currentUser.sin);
					if (listings.isEmpty()) {
						System.out.println("You have no listings!");
						return;
					}
					printMethods.printHostedListings(listings);
					System.out.printf("Choose a listing to view comments on [1-%d] or enter 0 to exit: ",
							listings.size());
					input = sc.nextLine().strip();
					if (!checkInputClamp(input, listings.size(), true)) {
						input = "";
					}
				}
				int choice = Integer.parseInt(input);
				if (choice == 0) {
					return;
				}
				Listing listing = listings.get(choice - 1);
				comments = sqlMngr.selectCommentByListing(listing.street, listing.number, listing.postalCode,
						listing.country);
				if (comments.isEmpty()) {
					System.out.println("This listing has no comments!");
					return;
				}
				printMethods.printComments(comments);
				break;
		}

		System.out.print("Enter to continue. ");
		sc.nextLine();
	}

	private void deleteUser() {
		String input = "";
		System.out.println();
		while (!input.equalsIgnoreCase("y")) {
			System.out.print("Are you sure you want to delete your account? [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("n")) {
				break;
			} else if (input.equalsIgnoreCase("y")) {
				int rows = sqlMngr.deleteUser(currentUser.sin);
				System.out.println("Rows affected: " + rows);
				this.currentUser = null;
			} else {
				System.out.println("That's not proper input, please try again!");
			}
		}
	}

	private void reportNumBookings() {
		String input = null;
		LocalDate[] dateFromTo = null;
		ArrayList<String> result = null;

		System.out.println();
		while (dateFromTo == null) {
			System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
					+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
			input = sc.nextLine().replaceAll("\\s", "");

			dateFromTo = checkFromToDates(input);
			input = null;
		}

		while (input == null) {
			System.out.print("Choose the report by city or postal code [c/p]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("c")) {
				input = "City";
				result = sqlMngr.reportNumBookingsCity(dateFromTo[0], dateFromTo[1]);
				System.out.println("\nTotal number of bookings from " + dateFromTo[0].toString() + " to "
						+ dateFromTo[1].toString() + " by city:");
				break;
			} else if (input.equalsIgnoreCase("p")) {
				input = "Postal Code";
				result = sqlMngr.reportNumBookingsPostalCode(dateFromTo[0], dateFromTo[1]);
				System.out.println("\nTotal number of bookings from " + dateFromTo[0].toString() + " to "
						+ dateFromTo[1].toString() + " by postal code:");
				break;
			} else {
				System.out.println("That's not a proper input, please try again!");
				input = null;
				continue;
			}
		}
		printMethods.printNumBookings(input, result);
	}

	private void reportRenterBookings() {
		String input = null;
		LocalDate[] dateFromTo = null;
		ArrayList<String> result = null;

		System.out.println();
		while (dateFromTo == null) {
			System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas."
					+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
			input = sc.nextLine().replaceAll("\\s", "");

			dateFromTo = checkFromToDates(input);
			input = null;
		}

		while (input == null) {
			System.out.print("Do you want the report by city [y/n]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("y")) {
				result = sqlMngr.reportRenterBookingCity(dateFromTo[0], dateFromTo[1]);
				System.out.println("\nRenters ranked by total number of bookings from "
						+ dateFromTo[0].toString() + " to " + dateFromTo[1].toString() + " by city:");

				printMethods.printRenterBookingsCity(result);
				return;
			} else if (input.equalsIgnoreCase("n")) {
				result = sqlMngr.reportRenterBooking(dateFromTo[0], dateFromTo[1]);
				System.out.println("\nRenters ranked by total number of bookings from "
						+ dateFromTo[0].toString() + " to " + dateFromTo[1].toString() + ":");

				printMethods.printRenterBookings(result);
				return;
			} else {
				System.out.println("That's not a proper input, please try again!");
				input = null;
				continue;
			}
		}
	}

	private void reportNumListings() {
		String input = null;
		ArrayList<String> result = null;

		System.out.println();
		while (input == null) {
			System.out.print("Choose the report by country, by country and city, or by country,"
					+ " city, and postal code [c/cc/ccp]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("c")) {
				result = sqlMngr.reportNumListingsCount();
				System.out.println("\nTotal number of listings by country:");

				printMethods.printNumListingsCount(result);
				return;
			} else if (input.equalsIgnoreCase("cc")) {
				result = sqlMngr.reportNumListingsCountCity();
				System.out.println("\nTotal number of listings from by country and city:");

				printMethods.printNumListingsCountCity(result);
				return;
			} else if (input.equalsIgnoreCase("ccp")) {
				result = sqlMngr.reportNumListingsCountCityPost();
				System.out.println("\nTotal number of listings from by country, city, and postal code:");

				printMethods.printNumListingsCountCityPost(result);
				return;
			} else {
				System.out.println("That's not a proper input, please try again!");
				input = null;
				continue;
			}
		}
	}

	private void reportRankHost() {
		String input = null;
		ArrayList<String> result = null;

		System.out.println();
		while (input == null) {
			System.out.print("Choose the report by country or by country and city [c/cc]: ");
			input = sc.nextLine().strip();
			if (input.equalsIgnoreCase("c")) {
				result = sqlMngr.reportRankHostCount();
				System.out.println("\nHosts ranked by total number of listings per country:");

				printMethods.printRankHostCount(result);
				return;
			} else if (input.equalsIgnoreCase("cc")) {
				result = sqlMngr.reportRankHostCountCity();
				System.out.println("\nHosts ranked by total number of listings per country and city:");

				printMethods.printRankHostCountCity(result);
				return;
			} else {
				System.out.println("That's not a proper input, please try again!");
				input = null;
				continue;
			}
		}
	}

	private void reportNumCancelled() {
		String input = null;
		int year;
		ArrayList<String> result = null;

		System.out.println();
		while (input == null) {
			try {
				System.out.print("Enter a year: ");
				input = sc.nextLine().strip();
				year = Integer.parseInt(input);
				result = sqlMngr.reportNumCancelled(year);

				System.out.println(
						"\nHosts and renters with the largest number of cancellations within the year " + year + ":");
				printMethods.printNumCancelled(result);
			} catch (NumberFormatException e) {
				System.out.println("That's not a number, please try again!");
				input = null;
			}
		}
	}

	private void reportHost10Percent() {
		ArrayList<String> result = sqlMngr.reportHost10Percent();
		System.out.println(
				"\nHosts with more than 10% of the total number of listings in each city and country:");
		printMethods.printHost10Percent(result);
	}
}
