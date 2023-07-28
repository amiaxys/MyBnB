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
		ArrayList<Listing> listings = null;
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
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+");
		System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s |%n", "#", "Type", "Address", "Latitude", "Longitude");
		System.out.println(
				"+=====+===========+==================================================================================+==========+===========+");
		for (Listing listing : listings) {
			count++;
      String lat = coordinatesDf.format(listing.latitude.doubleValue());
      String lon = coordinatesDf.format(listing.longitude.doubleValue());
			System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s |%n", count, listing.type,
					listing.number + " " + listing.street + ", " +
							listing.city + ", " + listing.country + " " + listing.postalCode, lat, lon);
		}
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+\n");
	}

  private void printFilteredListings(ArrayList<AvailabilityListing> listings) {
		int count = 0;
		System.out.println("\nResult: " + listings.size() + " listings\n");
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+------------+------------+");
		System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s | %-10s | %-10s |%n", 
        "#", "Type", "Address","Latitude", "Longitude", "Date", "Price");
		System.out.println(
				"+=====+===========+==================================================================================+==========+===========+============+============+");
		for (AvailabilityListing listing : listings) {
			count++;
      String lat = coordinatesDf.format(listing.latitude.doubleValue());
      String lon = coordinatesDf.format(listing.longitude.doubleValue());
			System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s | %-10s | %-10s |%n", count, listing.type,
					listing.number + " " + listing.street + ", " +
							listing.city + ", " + listing.country + " " + listing.postalCode, lat, lon,
              listing.date.toString(), listing.price);
		}
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+------------+------------+\n");
	}

	private ArrayList<AvailabilityListing> filterByAmenities(ArrayList<AvailabilityListing> listings, String amenities) {
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
				printAmenities();
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

  private ArrayList<Listing> convertToListing(ArrayList<AvailabilityListing> filtered) {
    ArrayList<Listing> listings = new ArrayList<>();

    filtered.forEach((availListing) -> listings.add(availListing.getListing()));

    return listings;
  }

  private ArrayList<AvailabilityListing> applyDateFilter(ArrayList<AvailabilityListing> availListings, LocalDate from, LocalDate to) {
    ArrayList<AvailabilityListing> newAvail = new ArrayList<>();

    availListings.forEach((availListing) -> {
      if (availListing.date.compareTo(from) >= 0 && availListing.date.compareTo(to) <= 0 && availListing.available) {
        newAvail.add(availListing);
      }
    });

    return newAvail;
  }
  
  private ArrayList<AvailabilityListing> applyPriceFilter(ArrayList<AvailabilityListing> availListings, BigDecimal from, BigDecimal to) {
    ArrayList<AvailabilityListing> newAvail = new ArrayList<>();

    availListings.forEach((availListing) -> {
      if (availListing.price.compareTo(from) >= 0 && availListing.price.compareTo(to) <= 0 && availListing.available) {
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

	private ArrayList<Listing> searchListingByAddress() {
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

    ArrayList<AvailabilityListing> availListings = sqlMngr.searchListingAddr(street, number, postalCode, country, city);
		LocalDate[] dateFromTo = askFilterDate();
    BigDecimal[] priceFromTo = askFilterPrice();

    if (dateFromTo == null && priceFromTo == null) { // don't apply date and price filter
      availListings = askFilterAmenities(availListings);
      ArrayList<Listing> listings = convertToListing(availListings);
		  printListings(listings);
      return listings;
    }
    else if (dateFromTo != null) { // apply date filter
      availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
    }

    if (priceFromTo != null) { // apply price filter
      availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
    }
    availListings = askFilterAmenities(availListings);
    printFilteredListings(availListings);
		return convertToListing(availListings);
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

	private ArrayList<Listing> searchListingByCoord() {
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

    if (dateFromTo == null && priceFromTo == null) { // don't apply date and price filter
      availListings = calDistance(availListings, latitude.doubleValue(), longitude.doubleValue(), distance);
      availListings = askFilterAmenities(availListings);
      ArrayList<Listing> listings = convertToListing(availListings);
		  printListings(listings);
      return listings;
    }
    else if (dateFromTo != null) { // apply date filter
      availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
    }

    if (priceFromTo != null) { // apply price filter
      availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
    }

    availListings = calDistance(availListings, latitude.doubleValue(), longitude.doubleValue(), distance);
    availListings = askFilterAmenities(availListings);
    printFilteredListings(availListings);
		return convertToListing(availListings);
	}

	private ArrayList<Listing> searchListingByPostalCode() {
		String postalCode;

		System.out.println();
		System.out.println("Search for nearby listings by entering a postal code");
		System.out.print("Enter a postal code: ");
		postalCode = sc.nextLine();

		String pattern = postalCode.substring(0, postalCode.length() - 1) + "_";
    ArrayList<AvailabilityListing> availListings = sqlMngr.searchListingPostalCode(pattern);
    LocalDate[] dateFromTo = askFilterDate();
    BigDecimal[] priceFromTo = askFilterPrice();

    if (dateFromTo == null && priceFromTo == null) { // don't apply date and price filter
      availListings = askFilterAmenities(availListings);
      ArrayList<Listing> listings = convertToListing(availListings);
		  printListings(listings);
      return listings;
    }
    else if (dateFromTo != null) { // apply date filter
      availListings = applyDateFilter(availListings, dateFromTo[0], dateFromTo[1]);
    }
    
    if (priceFromTo != null) { // apply price filter
      availListings = applyPriceFilter(availListings, priceFromTo[0], priceFromTo[1]);
    }
    availListings = askFilterAmenities(availListings);
    printFilteredListings(availListings);
		return convertToListing(availListings);
	}

	private boolean checkInputArrayList(String input, ArrayList<Listing> listings) {
		int choice = -1;
		try {
			choice = Integer.parseInt(input);
		} catch (NumberFormatException e) {
			System.out.println("That's not a number, please try again!");
			return false;
		}

		if (choice == 0) {
			return true;
		}

		if (choice < 1 || choice > listings.size()) {
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

	private void printHostedListings(ArrayList<Listing> hostedListings) {
		int count = 0;
		System.out.println("Listings You Host: " + hostedListings.size() + " listings\n");
		System.out.println("+-----+----------------------------------------------------------------"
				+ "-----------------------------------+");
		System.out.printf("| %-3s | %-97s |%n", "#", "Address of Listing");
		System.out.println("+=====+================================================================"
				+ "===================================+");
		for (Listing listing : hostedListings) {
			count++;
			System.out.printf("| %-3s | %-97s |%n", count, listing.number+" "+listing.street+
					", "+listing.country+", "+listing.postalCode);
		}
		System.out.println("+-----+----------------------------------------------------------------"
				+ "-----------------------------------+\n");
	}

	private void addAvailabilities() {
		ArrayList<Listing> hostedListings = sqlMngr.selectHostsBySIN(this.currentUser.sin);
		LocalDate[] dateFromTo = null;
		boolean available = true;
		BigDecimal price = null;
		String input;
		int choice = -1;

		while (!hostedListings.isEmpty() && dateFromTo == null) {
			System.out.println("Type \"0\" to exit");
			printHostedListings(hostedListings);
			System.out.printf("Choose a listing to add availabilities to [1-%d]: ", hostedListings.size());
			input = sc.nextLine().strip();
			if (!checkInputArrayList(input, hostedListings)) {
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

	private void printAvailabilities(ArrayList<Availability> availabilities) {
		int count = 0;
		System.out.println("Availability: " + availabilities.size() + " days available\n");
		System.out.println("+-----+------------+------------+");
		System.out.printf("| %-3s | %-10s | %-10s |%n", "#", "Date", "Price");
		System.out.println("+=====+============+============+");
		for (Availability availability : availabilities) {
			count++;
			System.out.printf("| %-3s | %-10s | %-10s |%n", count, availability.date.toString(), availability.price);
		}
		System.out.println("+-----+------------+------------+\n");
	}

	private boolean isValidPaymentMethod(String input) {
		if (paymentMethods.contains(input.toLowerCase())) {
			return true;
		}
		return false;
	}

	private void printPaymentMethods() {
		System.out.print("Payment methods: ");
		for (String method : paymentMethods) {
			System.out.print(method + ", ");
		}
		System.out.println("");
	}

	private void bookListing(ArrayList<Listing> listings) {
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
				System.out.printf("Choose a listing to book [1-%d]: ", listings.size());
				input = sc.nextLine().strip();
				if (!checkInputArrayList(input, listings)) {
					continue;
				}
				choice = Integer.parseInt(input);
				if (choice == 0) {
					break;
				}

				Listing listing = listings.get(choice - 1);

				System.out.print(
						"Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas to check for availability."
								+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
				input = sc.nextLine().replaceAll("\\s", "");

				LocalDate[] dateFromTo = checkFromToDates(input);
				if (dateFromTo == null) {
					continue;
				}

				ArrayList<Availability> availabilities = sqlMngr.selectAvailBetweenDate(listing.street, listing.number,
						listing.postalCode, listing.country, dateFromTo[0], dateFromTo[1], true);
				printAvailabilities(availabilities);

				if (availabilities.isEmpty()) {
					System.out.println("There are no availabilities for those dates, please try again!");
					continue;
				}

				System.out.print("Enter a \"from\" date and a \"to\" date (YYYY-MM-DD) seperated by commas to book."
						+ " (E.g., \"2023-09-10, 2023-09-20\"): ");
				input = sc.nextLine().replaceAll("\\s", "");

				dateFromTo = checkFromToDates(input);
				if (dateFromTo == null) {
					continue;
				}

				LocalDate tempDate = dateFromTo[0];
				ArrayList<Availability> bookedAvailabilities = new ArrayList<>();
				BigDecimal totalPrice = new BigDecimal("0");
				boolean found = false;
				while (tempDate.isBefore(dateFromTo[1]) || tempDate.isEqual(dateFromTo[1])) {
					found = false;
					for (Availability availability : availabilities) {
						if (availability.date.isEqual(tempDate)) {
							bookedAvailabilities.add(availability);
							totalPrice = totalPrice.add(availability.price);
							found = true;
							break;
						}
					}
					if (!found) {
						System.out.println("Those dates are not available, please try again!");
						break;
					}
					tempDate = tempDate.plusDays(1);
				}

				if (!found) {
					continue;
				}

				System.out.println("Total price: " + totalPrice);
				printPaymentMethods();
				System.out.print("Enter a payment method: ");
				input = sc.nextLine().strip();

				if (!isValidPaymentMethod(input)) {
					System.out.println("That's not a valid payment method, please try again!");
					continue;
				}

				rows = sqlMngr.insertBooked(currentUser.sin, listing.street, listing.number, listing.postalCode,
						listing.country, dateFromTo[0], dateFromTo[1], input);

				System.out.println("Booking rows affected: " + rows);

				rows = 0;
				for (Availability availability : bookedAvailabilities) {
					rows += sqlMngr.insertOrUpdateAvailability(listing.street, listing.number, listing.postalCode,
							listing.country, availability.date, false, availability.price);
				}
				System.out.println("Availability rows affected: " + rows);
			}
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
