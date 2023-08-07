package main;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Availability {
    protected String street;
    protected int number;
    protected String postalCode;
    protected String country;
    protected LocalDate date;
    protected boolean available;
    protected BigDecimal price;

    public Availability() {
        street = null;
        number = 0;
        postalCode = null;
        country = null;
        date = null;
        available = false;
        price = null;
    }
}
