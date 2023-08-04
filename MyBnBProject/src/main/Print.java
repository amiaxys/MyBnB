package main;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Print helper class
public class Print {
	DecimalFormat coordinatesDf = new DecimalFormat("#.####");

	// Print not signed in menu options
	public void menu() {
		System.out.println("=========MENU=========");
		System.out.println(" 0. Exit.");
		System.out.println(" 1. Create an account.");
		System.out.println(" 2. Sign in.");
		System.out.println(" 3. Run reports.");
		System.out.println("---------------------------");
		System.out.print("Choose one of the previous options [0-3]: ");
	}

	public void reportOptions() {
		System.out.println("\n*********REPORT OPTIONS*********");
		System.out.println(" 0. Back.");
		System.out.println(" 1. Report total number of bookings in a specific date range.");
		System.out.println(" 2. Report renters ranked by the number of bookings in a specific date range.");
		System.out.println(" 3. Report total number of listings.");
		System.out.println(" 4. Report hosts ranked by total number of listings.");
		System.out
				.println(" 5. Report hosts that have a number of listings that is more than 10% of the total number of"
						+ "\n\tlistings for every country and city.");
		System.out.println(
				" 6. Report hosts and renters with the largest number of cancellations within a year.");
		System.out.println(
				" 7. Report the set of most popular noun phrases (in comments) for each listing. [not implemented]");
		System.out.println("---------------------------");
		System.out.print("Choose one of the previous options [0-7]: ");
	}

	// Print signed in menu (user menu) options
	public void userMenu(String name, boolean updated) {
		System.out.println("\n=========USER MENU=========");
		System.out.printf("Welcome back%s" + name + "!\n", name.equals("") ? "" : ", ");
		System.out.println("---------------------------");
		if (updated) {
			System.out.println("Your bookings have been updated!");
			System.out.println("To view your updated bookings, enter \"u\".");
			System.out.println("---------------------------");
		}
		int count = 0;
		System.out.printf(" %d. Exit.\n", count++);
		System.out.println("---------------------------");
		System.out.printf(" %d. Create a listing.\n", count++);
		System.out.printf(" %d. Add availabilities to listings.\n", count++);
		System.out.printf(" %d. Change price of existing availabilities.\n", count++);
		System.out.printf(" %d. Delete a listing.\n", count++);
		System.out.println("---------------------------");
		System.out.printf(" %d. Search for listings (to book).\n", count++);
		System.out.printf(" %d. View your bookings.\n", count++);
		System.out.printf(" %d. Cancel a booking.\n", count++);
		System.out.println("---------------------------");
		System.out.printf(" %d. View your listings' bookings.\n", count++);
		System.out.printf(" %d. Cancel a listing's booking.\n", count++);
		System.out.println("---------------------------");
		System.out.printf(" %d. Comment on a listing.\n", count++);
		System.out.printf(" %d. Comment on a user.\n", count++);
		System.out.printf(" %d. View your comments.\n", count++);
		System.out.println("---------------------------");
		System.out.printf(" %d. Sign out.\n", count++);
		System.out.printf(" %d. Delete your account.\n", count++);
		System.out.println("---------------------------");
		System.out.print("Choose one of the previous options [0-14]: ");
	}

	public void searchOptions() {
		System.out.println("\n*********SEARCH OPTIONS*********");
		System.out.println("0. Back.");
		System.out.println("1. Search by exact address.");
		System.out.println("2. Search by latitude and longitude.");
		System.out.println("3. Search by postal code.");
		System.out.print("Choose one of the previous options [0-3]: ");
	}

	public void printAmenities() {
		System.out.println("Wifi\t\t\t\tKitchen\t\t\tWasher\nDryer"
				+ "\t\t\t\tAir conditioning\tHeating\nDedicated workspace\t\tTV"
				+ "\t\t\tHair dryer\nIron\t\t\t\tPool\t\t\tHot tub\nFree parking"
				+ "\t\t\tEV charger\t\tCrib\nGym\t\t\t\tBBQ grill\t\tBreakfast"
				+ "\nIndoor fireplace\t\tSmoking allowed\t\tBeachfront"
				+ "\nWaterfront\t\t\tSki-in/ski-out\t\tSmoke alarm\nCarbon monoxide alarm");
	}

	public void printViewBookingOptions() {
		System.out.println("Do you want to view only active bookings, canceled bookings or all bookings?");
		System.out.println(" 1. Active bookings.");
		System.out.println(" 2. Canceled bookings.");
		System.out.println(" 3. All bookings.");
		System.out.print("Enter an option [1-3]: ");
	}

	public void printViewCommentOptions() {
		System.out.println("Do you want to view comments received, comments made or comments on your listings?");
		System.out.println(" 1. Comments you have received.");
		System.out.println(" 2. Comments you have written.");
		System.out.println(" 3. Comments on your listings.");
		System.out.print("Enter an option [1-3]: ");
	}

	public void printUsers(ArrayList<User> users) {
		int count = 0;
		System.out.println("\nResult: " + users.size() + " users\n");
		System.out.println("+-----+-----------+----------------------+");
		System.out.printf("| %-3s | %-9s | %-20s |%n", "#", "SIN", "Name");
		System.out.println("+=====+===========+======================+");
		for (User user : users) {
			count++;
			System.out.printf("| %-3d | %-9s | %-20s |%n", count, user.sin, user.name);
		}
		System.out.println("+-----+-----------+----------------------+");
	}

	public void printFilteredListings(ArrayList<AvailabilityListing> listings) {
		int count = 0;
		System.out.println("\nResult: " + listings.size() + " listings\n");
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+------------+------------+");
		System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s | %-10s | %-10s |%n",
				"#", "Type", "Address", "Latitude", "Longitude", "Date", "Price");
		System.out.println(
				"+=====+===========+==================================================================================+==========+===========+============+============+");
		for (AvailabilityListing listing : listings) {
			count++;
			String lat = coordinatesDf.format(listing.latitude.doubleValue());
			String lon = coordinatesDf.format(listing.longitude.doubleValue());
			System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s | %-10s | %-10s |%n", count, listing.type,
					listing.number + " " + listing.street + ", " +
							listing.city + ", " + listing.country + " " + listing.postalCode,
					lat, lon,
					listing.date.toString(), listing.price);
		}
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+------------+------------+\n");
	}

	public void printListings(ArrayList<Listing> listings) {
		int count = 0;
		System.out.println("\n Result: " + listings.size() + " listings\n");
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+");
		System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s |%n",
				"#", "Type", "Address", "Latitude", "Longitude");
		System.out.println(
				"+=====+===========+==================================================================================+==========+===========+");
		for (Listing listing : listings) {
			count++;
			String lat = coordinatesDf.format(listing.latitude.doubleValue());
			String lon = coordinatesDf.format(listing.longitude.doubleValue());
			System.out.printf("| %-3s | %-9s | %-80s | %-8s | %-9s |%n", count, listing.type,
					listing.number + " " + listing.street + ", " + listing.city + ", " + listing.country + " "
							+ listing.postalCode,
					lat, lon);
		}
		System.out.println(
				"+-----+-----------+----------------------------------------------------------------------------------+----------+-----------+\n");
	}

	public void printHostedListings(ArrayList<Listing> hostedListings) {
		int count = 0;
		System.out.println("Listings You Host: " + hostedListings.size() + " listings\n");
		System.out.println("+-----+----------------------------------------------------------------"
				+ "-----------------------------------+");
		System.out.printf("| %-3s | %-97s |%n", "#", "Address of Listing");
		System.out.println("+=====+================================================================"
				+ "===================================+");
		for (Listing listing : hostedListings) {
			count++;
			System.out.printf("| %-3s | %-97s |%n", count, listing.number + " " + listing.street +
					", " + listing.country + ", " + listing.postalCode);
		}
		System.out.println("+-----+----------------------------------------------------------------"
				+ "-----------------------------------+\n");
	}

	public void printBooked(ArrayList<Booked> booked) {
		System.out.println("Your bookings:");
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+");
		System.out.printf(
				"|  #  | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |\n",
				"Street", "Number", "Postal Code", "Country", "From", "To", "Payment", "Price");
		System.out.println(
				"+=====+================================+============+=============+==========================+============+============+=============+============+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |%n", count,
					booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod, booking.price);
		}
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+\n");
	}

	public void printBookedWithCanceled(ArrayList<Booked> booked, boolean updated) {
		System.out.printf("Your %sbookings:\n", updated ? "updated " : "");
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+----------+");
		System.out.printf(
				"|  #  | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s | %-8s |\n",
				"Street", "Number", "Postal Code", "Country", "From", "To", "Payment", "Price", "Canceled");
		System.out.println(
				"+=====+================================+============+=============+==========================+============+============+=============+============+==========+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s | %-8s |%n",
					count,
					booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod, booking.price,
					booking.canceled);
		}
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+----------+\n");
	}

	public void printBookedWithSIN(ArrayList<Booked> booked) {
		System.out.println("Your listings' bookings:");
		System.out.println(
				"+-----+-----------+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+");
		System.out.printf(
				"|  #  | %-9s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |\n", 
				"SIN", "Street", "Number", "Postal Code", "Country", "From", "To", "Payment", "Price");
		System.out.println(
				"+=====+===========+================================+============+=============+==========================+============+============+=============+============+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-9s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |%n",
					count, booking.sin, booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod, booking.price);
		}
		System.out.println(
				"+-----+-----------+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+\n");
	}

	public void printBookedWithSINCanceled(ArrayList<Booked> booked, boolean updated) {
		System.out.printf("Your listings' %sbookings:\n", updated ? "updated " : "");
		System.out.println(
				"+-----+-----------+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+----------+");
		System.out.printf(
				"|  #  | %-9s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s | %-8s |\n", 
				"SIN", "Street", "Number", "Postal Code", "Country", "From", "To", "Payment", "Price", "Canceled");
		System.out.println(
				"+=====+===========+================================+============+=============+==========================+============+============+=============+============+==========+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-9s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s | %-8s |%n",
					count, booking.sin, booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod, booking.price,
					booking.canceled);
		}
		System.out.println(
				"+-----+-----------+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+----------+\n");
	}

	public void printNumBookings(String attr, ArrayList<String> result) {
		System.out.println("+---------------------------+---------------+");
		System.out.printf("| %-25s | %-13s |%n", attr, "Total booking");
		System.out.println("+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-25s | %-13s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+---------------------------+---------------+\n");
	}

	public void printRenterBookingsCity(ArrayList<String> result) {
		System.out.println("+----------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-13s |%n", "Name", "City", "Total booking");
		System.out.println("+======================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
		}
		System.out.println("+----------------------+---------------------------+---------------+\n");
	}

	public void printRenterBookings(ArrayList<String> result) {
		System.out.println("+----------------------+---------------+");
		System.out.printf("| %-20s | %-13s |%n", "Name", "Total booking");
		System.out.println("+======================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-13s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+----------------------+---------------+\n");
	}

	public void printNumListingsCount(ArrayList<String> result) {
		System.out.println("+---------------------------+---------------+");
		System.out.printf("| %-25s | %-13s |%n", "Country", "Total listing");
		System.out.println("+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-25s | %-13s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+---------------------------+---------------+\n");
	}

	public void printNumListingsCountCity(ArrayList<String> result) {
		System.out.println("+---------------------------+---------------------------+---------------+");
		System.out.printf("| %-25s | %-25s | %-13s |%n", "Country", "City", "Total listing");
		System.out.println("+===========================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-25s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
		}
		System.out.println("+---------------------------+---------------------------+---------------+\n");
	}

	public void printNumListingsCountCityPost(ArrayList<String> result) {
		System.out.println("+---------------------------+---------------------------+-------------+---------------+");
		System.out.printf("| %-25s | %-25s | %-11s | %-13s |%n", "Country", "City", "Postal code", "Total listing");
		System.out.println("+===========================+===========================+=============+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-25s | %-25s | %-11s | %-13s |%n", result.get(i), result.get(++i), result.get(++i),
					result.get(++i));
		}
		System.out.println("+---------------------------+---------------------------+-------------+---------------+\n");
	}

	public void printRankHostCount(ArrayList<String> result) {
		System.out.println("+----------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-13s |%n", "Name", "Country", "Total listing");
		System.out.println("+======================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
		}
		System.out.println("+----------------------+---------------------------+---------------+\n");
	}

	public void printRankHostCountCity(ArrayList<String> result) {
		System.out.println(
				"+----------------------+---------------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-25s | %-13s |%n", "Name", "Country", "City", "Total listing");
		System.out.println(
				"+======================+===========================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-25s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i),
					result.get(++i));
		}
		System.out.println(
				"+----------------------+---------------------------+---------------------------+---------------+\n");
	}

	public void printNumCancelled(ArrayList<String> result) {
		System.out.println("+----------------------+--------------------+");
		System.out.printf("| %-20s | %-18s |%n", "Name", "Total cancellation");
		System.out.println("+======================+====================+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-18s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+----------------------+--------------------+\n");
	}

	public void printHost10Percent(ArrayList<String> result) {
		System.out.println(
				"\n+----------------------+---------------------------+---------------------------+-------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-25s | %-17s | %-13s |%n", "Name", "City", "Country", "Number of Listing",
				"Total Listing");
		System.out.println(
				"+======================+===========================+===========================+===================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-25s | %-25s | %-17s | %-13s |%n", result.get(i), result.get(++i),
					result.get(++i), result.get(++i),
					result.get(++i));
		}
		System.out.println(
				"+----------------------+---------------------------+---------------------------+-------------------+---------------+\n");
	}

	public void printComments(ArrayList<Comment> comments) {
		System.out.println("\n+-----+-----------+----------------------+--------+----------------------+");
		System.out.printf("| %-3s | %-9s | %-20s | %-6s | %-20s |%n", "#", "SIN", "Name", "Rating", "Date");
		System.out.println("+=====+===========+======================+========+======================+");
		for (int i = 0; i < comments.size(); i++) {
			System.out.printf("| %-3s | %-9s | %-20s | %-6s | %-20s |%n", i + 1, comments.get(i).sin,
					comments.get(i).commentedByUser.name, comments.get(i).rating, comments.get(i).date);
			System.out.println("+------------------------------------------------------------------------+");
			System.out.println(comments.get(i).text);
			System.out.println("+------------------------------------------------------------------------+");
		}
		System.out.println("+-----+-----------+----------------------+--------+----------------------+\n");
	}

	public void printCommentsUserListing(ArrayList<Comment> comments) {
		System.out.println(
				"\n+-----+--------+----------------------+-------------------+----------------------------------------------------------------------------------+");
		System.out.printf("| %-3s | %-6s | %-20s | %-17s | %-80s |%n", "#", "Rating", "Date", "Commented On User",
				"Address of Listing");
		System.out.println(
				"+=====+========+======================+===================+==================================================================================+");
		String address = "";
		String sin = "";
		for (int i = 0; i < comments.size(); i++) {
			if (comments.get(i).commentedOnListing.street != null) {
				address = comments.get(i).commentedOnListing.number + " " + comments.get(i).commentedOnListing.street
						+ ", " + comments.get(i).commentedOnListing.postalCode + ", "
						+ comments.get(i).commentedOnListing.country;
			} else {
				address = "N/A";
			}

			if (comments.get(i).commentedOnUser.sin != null) {
				sin = comments.get(i).commentedOnUser.sin;
			} else {
				sin = "N/A";
			}

			System.out.printf("| %-3s | %-6s | %-20s | %-17s | %-80s |%n", i + 1, comments.get(i).rating,
					comments.get(i).date, sin, address);
			System.out.println(
					"+--------------------------------------------------------------------------------------------------------------------------------------------+");
			System.out.println(comments.get(i).text);
			System.out.println(
					"+--------------------------------------------------------------------------------------------------------------------------------------------+");
		}
		System.out.println(
				"+-----+--------+----------------------+-------------------+----------------------------------------------------------------------------------+\n");
	}
}
