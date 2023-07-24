package main;

import java.math.BigDecimal;

public class Listing {
	protected String type;
	protected String street;
	protected int number;
	protected String postalCode;
	protected String country;
	protected String city;
	protected BigDecimal latitude;
	protected BigDecimal longitude;
	protected String amenities;

	public Listing() {
		type = null;
		street = null;
		number = 0;
		postalCode = null;
		country = null;
		city = null;
		latitude = null;
		longitude = null;
		amenities = null;
	}
}
