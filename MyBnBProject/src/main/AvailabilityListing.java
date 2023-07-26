package main;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AvailabilityListing extends Listing {
  
  protected Listing listing;
  protected LocalDate date;
  protected boolean available;
  protected BigDecimal price;

	public AvailabilityListing() {
    super();
    date = null;
    available = false;
    price = null;
	}

  public AvailabilityListing(String type, String street, int number, String postalCode, String country, String city,
  BigDecimal latitude, BigDecimal longitude, String amenities, LocalDate date, boolean available, BigDecimal price) {
    super(type, street, number, postalCode, country, city, latitude, longitude, amenities);
    this.date = date;
    this.available = available;
    this.price = price;
  }
}
