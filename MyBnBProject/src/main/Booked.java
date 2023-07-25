package main;

import java.time.LocalDate;

public class Booked {
    protected String sin;
    protected String street;
    protected int number;
    protected String postalCode;
    protected String country;
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected String paymentMethod;

    public Booked() {
        sin = null;
        street = null;
        number = 0;
        postalCode = null;
        country = null;
        fromDate = null;
        toDate = null;
        paymentMethod = null;
    }
}
