package main;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Print helper class
public class Print {
    DecimalFormat coordinatesDf = new DecimalFormat("#.####");

	  public void menu() {    // Print not signed in menu options
		  System.out.println("=========MENU=========");
		  System.out.println(" 0. Exit.");
		  System.out.println(" 1. Create an account.");
		  System.out.println(" 2. Sign in.");
		  System.out.println(" 3. Run reports.");
		  System.out.print("Choose one of the previous options [0-3]: ");
	  }

    public void reportOptions() {
      System.out.println("\n*********REPORT OPTIONS*********");
      System.out.println(" 0. Back.");
      System.out.println(" 1. Report total number of bookings in a specific date range.");
      System.out.println(" 2. Report renters ranked by the number of bookings in a specific date range.");
      System.out.println(" 3. Report total number of listings.");
      System.out.println(" 4. Report hosts ranked by total number of listings.");
      System.out.println(" 5. Report hosts that have a number of listings that is more than 10% of the number of"
          + "\n\tlistings for every country and city. [not implemented]");
      System.out.println(
          " 6. Report hosts and renters with the largest number of cancellations within a year.");
      System.out.println(" 7. Report the set of most popular noun phrases (in comments) for each listing. [not implemented]");
      System.out.print("Choose one of the previous options [0-7]: ");
    }

	  public void userMenu(String name) { // Print signed in menu (user menu) options
		  System.out.println("\n=========USER MENU=========");
      System.out.println("Welcome back "+name+"!");
		  System.out.println(" 0. Exit.");
		  System.out.println(" 1. Create a listing.");
		  System.out.println(" 2. Add availabilities to listings.");
		  System.out.println(" 3. Search for listings.");
		  System.out.println(" 4. View your bookings.");
		  System.out.println(" 5. Cancel a booking.");
		  System.out.println(" 6. View your listings' bookings.");
		  System.out.println(" 7. Cancel a listing's booking.");
		  System.out.println(" 8. Delete a listing.");
		  System.out.println(" 10. Sign out.");
		  // add delete account later
		  System.out.print("Choose one of the previous options [0-10]: ");
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
      System.out.println(" 1. Active bookings");
      System.out.println(" 2. Canceled bookings");
      System.out.println(" 3. All bookings");
      System.out.print("Enter an option [1-3]: ");
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
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+");
		System.out.printf(
				"|  #  | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s |\n",
				"Street", "Number", "Postal Code", "Country", "From", "To", "Payment");
		System.out.println(
				"+=====+================================+============+=============+==========================+============+============+=============+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s |%n", count,
					booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod);
		}
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+\n");
	}

    public void printBookedWithCanceled(ArrayList<Booked> booked) {
		System.out.println("Your bookings:");
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+");
		System.out.printf(
				"|  #  | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |\n",
				"Street", "Number", "Postal Code", "Country", "From", "To", "Payment", "Canceled");
		System.out.println(
				"+=====+================================+============+=============+==========================+============+============+=============+============+");
		int count = 0;
		for (Booked booking : booked) {
			count++;
			System.out.printf("| %-3s | %-30s | %-10s | %-11s | %-24s | %-10s | %-10s | %-11s | %-10s |%n", count,
					booking.street, booking.number, booking.postalCode, booking.country,
					booking.fromDate.toString(), booking.toDate.toString(), booking.paymentMethod,
					booking.canceled);
		}
		System.out.println(
				"+-----+--------------------------------+------------+-------------+--------------------------+------------+------------+-------------+------------+\n");
	}

  public void printNumBookings(String attr, ArrayList<Object> result) {
    System.out.println("+---------------------------+---------------+");
		System.out.printf("| %-25s | %-13s |%n", attr, "Total booking");
		System.out.println("+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-25s | %-13s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+---------------------------+---------------+\n");
  }

  public void printRenterBookingsCity(ArrayList<Object> result) {
    System.out.println("+----------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-13s |%n", "Name", "City", "Total booking");
		System.out.println("+======================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
		}
		System.out.println("+----------------------+---------------------------+---------------+\n");
  }

  public void printRenterBookings(ArrayList<Object> result) {
    System.out.println("+----------------------+---------------+");
		System.out.printf("| %-20s | %-13s |%n", "Name", "Total booking");
		System.out.println("+======================+===============+");
		for (int i = 0; i < result.size(); i++) {
			System.out.printf("| %-20s | %-13s |%n", result.get(i), result.get(++i));
		}
		System.out.println("+----------------------+---------------+\n");
  }

  public void printNumListingsCount(ArrayList<Object> result) {
    System.out.println("+---------------------------+---------------+");
		System.out.printf("| %-25s | %-13s |%n", "Country", "Total listing");
    System.out.println("+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-25s | %-13s |%n", result.get(i), result.get(++i));
    }
    System.out.println("+---------------------------+---------------+\n");
  }

  public void printNumListingsCountCity(ArrayList<Object> result) {
    System.out.println("+---------------------------+---------------------------+---------------+");
		System.out.printf("| %-25s | %-25s | %-13s |%n", "Country", "City", "Total listing");
    System.out.println("+===========================+===========================+===============+");
    for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-25s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
	  }
    System.out.println("+---------------------------+---------------------------+---------------+\n");
  }

  public void printNumListingsCountCityPost(ArrayList<Object> result) {
    System.out.println("+---------------------------+---------------------------+-------------+---------------+");
		System.out.printf("| %-25s | %-25s | %-11s | %-13s |%n", "Country", "City", "Postal code", "Total listing");
    System.out.println("+===========================+===========================+=============+===============+");
		for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-25s | %-25s | %-11s | %-13s |%n", result.get(i), result.get(++i), result.get(++i), result.get(++i));
    }
    System.out.println("+---------------------------+---------------------------+-------------+---------------+\n");
  }

  public void printRankHostCount(ArrayList<Object> result) {
    System.out.println("+----------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-13s |%n", "Name", "Country", "Total listing");
    System.out.println("+======================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-20s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i));
    }
    System.out.println("+----------------------+---------------------------+---------------+\n");
  }

  public void printRankHostCountCity(ArrayList<Object> result) {
    System.out.println("+----------------------+---------------------------+---------------------------+---------------+");
		System.out.printf("| %-20s | %-25s | %-25s | %-13s |%n", "Name", "Country", "City", "Total listing");
    System.out.println("+======================+===========================+===========================+===============+");
		for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-20s | %-25s | %-25s | %-13s |%n", result.get(i), result.get(++i), result.get(++i), result.get(++i));
    }
    System.out.println("+----------------------+---------------------------+---------------------------+---------------+\n");
  }

  public void printNumCancelled(ArrayList<Object> result) {
    System.out.println("+----------------------+--------------------+");
		System.out.printf("| %-20s | %-18s |%n", "Name", "Total cancellation");
    System.out.println("+======================+====================+");
		for (int i = 0; i < result.size(); i++) {
	    System.out.printf("| %-20s | %-18s |%n", result.get(i), result.get(++i));
    }
    System.out.println("+----------------------+--------------------+\n");
  }
}
