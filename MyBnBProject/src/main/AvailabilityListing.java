package main;

import java.math.BigDecimal;
import java.time.LocalDate;

public class AvailabilityListing {
  
  protected String type;
	protected String street;
	protected int number;
	protected String postalCode;
	protected String country;
	protected String city;
	protected BigDecimal latitude;
	protected BigDecimal longitude;
	protected String amenities;
  protected LocalDate date;
  protected boolean available;
  protected BigDecimal price;

	public AvailabilityListing() {
		type = null;
		street = null;
		number = 0;
		postalCode = null;
		country = null;
		city = null;
		latitude = null;
		longitude = null;
		amenities = null;
    date = null;
    available = false;
    price = null;
	}
}
