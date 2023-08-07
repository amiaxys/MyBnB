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

  public Listing(String type, String street, int number, String postalCode, String country,
      String city, BigDecimal latitude, BigDecimal longitude, String amenities) {
    this.type = type;
    this.street = street;
    this.number = number;
    this.postalCode = postalCode;
    this.country = country;
    this.city = city;
    this.latitude = latitude;
    this.longitude = longitude;
    this.amenities = amenities;
  }

  public Listing getListing() {
    return this;
  }
}
