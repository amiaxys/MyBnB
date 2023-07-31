package main;

import java.text.DecimalFormat;
import java.util.ArrayList;

// Print helper class
public class Print {
    DecimalFormat coordinatesDf = new DecimalFormat("#.####");

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
}
