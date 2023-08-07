package main;

import java.math.BigDecimal;
import java.time.LocalDate;

public class Booked {
    protected int bid;
    protected String sin;
    protected String street;
    protected int number;
    protected String postalCode;
    protected String country;
    protected LocalDate fromDate;
    protected LocalDate toDate;
    protected String paymentMethod;
    protected BigDecimal price;
    protected boolean canceled;

    public Booked() {
        bid = -1;
        sin = null;
        street = null;
        number = 0;
        postalCode = null;
        country = null;
        fromDate = null;
        toDate = null;
        paymentMethod = null;
        price = null;
        canceled = false;
    }
}
