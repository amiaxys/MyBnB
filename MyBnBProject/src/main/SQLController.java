package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.Reader;
import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import org.apache.ibatis.jdbc.ScriptRunner;

public class SQLController {
	private static final String dbClassName = "com.mysql.cj.jdbc.Driver";// "com.mysql.jdbc.Driver";
	private static final String CONNECTION = "jdbc:mysql://127.0.0.1/";

	// Object that establishes and keeps the state of our application's
	// connection with the MySQL backend.
	private Connection conn = null;

	// Objects which communicates with the SQL backend delivering to it the
	// desired statement/query from our application and returning the results
	// of this execution the same way that are received from the SQL backend.
	// User statements
	private PreparedStatement insertUser = null;
	private PreparedStatement selectUserBySIN = null;
	private PreparedStatement selectUserByListingBooked = null;
	private PreparedStatement deleteUser = null;
	// Listing statements
	private PreparedStatement insertListing = null;
	private PreparedStatement selectAllListing = null;
	private PreparedStatement selectListingAddr = null;
	private PreparedStatement selectListingPostalCode = null;
	private PreparedStatement selectListingBooked = null;
	private PreparedStatement deleteListing = null;
	private PreparedStatement deleteListingHostedBySIN = null;
	// Hosts statements
	private PreparedStatement insertHosts = null;
	private PreparedStatement selectHostsBySIN = null;
	// Availability statements
	private PreparedStatement insertAvailability = null;
	private PreparedStatement insertOrUpdateAvailability = null;
	private PreparedStatement updateAvailability = null;
	private PreparedStatement updateAvailabilityPrice = null;
	private PreparedStatement updateAvailabilityBetweenDate = null;
	private PreparedStatement selectAllAvailBetweenDate = null;
	private PreparedStatement selectAvailBetweenDate = null;
	private PreparedStatement sumAvailBetweenDate = null;
	private PreparedStatement deleteAvailBetweenDate = null;
	private PreparedStatement getMinMaxPrice = null;
	// Booked statements
	private PreparedStatement insertBooked = null;
	private PreparedStatement updateBookedPriceByBIN = null;
	private PreparedStatement updateBookedUpdatedBySIN = null;
	private PreparedStatement cancelBooked = null;
	private PreparedStatement cancelBookedByListing = null;
	private PreparedStatement cancelBookedByHostedListing = null;
	private PreparedStatement selectAllBookedBySIN = null;
	private PreparedStatement selectBookedBySIN = null;
	private PreparedStatement selectAllBookedByHostListings = null;
	private PreparedStatement selectBookedByHostListings = null;
	private PreparedStatement selectBookedByAddress = null;
	private PreparedStatement selectBookedByAddressDate = null;
	private PreparedStatement selectBookedUpdatedBySIN = null;
	// Cancellation statements
	private PreparedStatement insertCancellation = null;
	// Report statements
	private PreparedStatement reportNumBookingsCity = null;
	private PreparedStatement reportNumBookingsPostalCode = null;
	private PreparedStatement reportRenterBooking = null;
	private PreparedStatement reportRenterBookingCity = null;
	private PreparedStatement reportNumListingsCount = null;
	private PreparedStatement reportNumListingsCountCity = null;
	private PreparedStatement reportNumListingsCountCityPost = null;
	private PreparedStatement reportRankHostCount = null;
	private PreparedStatement reportRankHostCountCity = null;
	private PreparedStatement reportNumCancelled = null;
	private PreparedStatement reportHost10Percent = null;
	// View statments
	PreparedStatement createLargestCancelView = null;
	// Comment statements
	private PreparedStatement insertComment = null;
	private PreparedStatement insertCommentOnListing = null;
	private PreparedStatement insertCommentOnUser = null;
	private PreparedStatement selectCommentByListing = null;
	private PreparedStatement selectCommentByUser = null;
	private PreparedStatement selectCommentMadeByUser = null;
	private PreparedStatement selectAllListingWComments = null;

	// Initialize current instance of this class.
	public boolean connect(String[] cred) throws ClassNotFoundException {
		Class.forName(dbClassName);
		boolean success = true;
		String user = cred[0];
		String pass = cred[1];
		String connection = CONNECTION + "mybnb";
		try {
			conn = DriverManager.getConnection(CONNECTION, user, pass);
			success = createDatabase();
			conn.close();
			conn = DriverManager.getConnection(connection, user, pass);
			success = prepareStatements();
			success = createTables();

		} catch (SQLException e) {
			success = false;
			System.err.println("Connection could not be established!");
			e.printStackTrace();
		}
		return success;
	}

	// Destroy the private objects/fields of current instance of this class.
	// Acts like a destructor.
	public void disconnect() {
		try {
			insertUser.close();
			selectUserBySIN.close();
			conn.close();
		} catch (SQLException e) {
			System.err.println("Exception occured while disconnecting!");
			e.printStackTrace();
		} finally {
			insertUser = null;
			selectUserBySIN = null;
			conn = null;
		}
	}

	private boolean createDatabase() {
		boolean success = true;
		try {
			PreparedStatement createDb = conn.prepareStatement("create database mybnb");
			createDb.executeUpdate();
			createDb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("Database already exists!");
			// e.printStackTrace();
		}
		return success;
	}

	private boolean createTables() {
		boolean success = true;
		try {
			// @formatter:off
			PreparedStatement createUserTb = conn.prepareStatement("CREATE TABLE User (" 
					+ "	SIN CHAR(9) PRIMARY KEY,"
					+ " Password VARCHAR(250) NOT NULL," 
					+ " Salt BINARY(16) NOT NULL," 
					+ "	Name VARCHAR(250),"
					+ "	Address VARCHAR(250)," 
					+ "	Birthdate DATE," 
					+ "	Occupation VARCHAR(100)" 
					+ ")");
			// @formatter:on
			createUserTb.executeUpdate();
			createUserTb.close();

			// insert rows to table
			try {
				int count = 0, rows = 0;
				Password passMethods = new Password();

				LocalDate birthDate = LocalDate.parse("1998-04-16", CommandLine.format);
				byte[] salt = passMethods.getSalt();
				String password = passMethods.getSaltHashedPassword("password", salt);
				insertUser.setString(++count, "111111111");
				insertUser.setString(++count, password);
				insertUser.setBytes(++count, salt);
				insertUser.setString(++count, "Andy");
				insertUser.setString(++count, "823 London Ave");
				insertUser.setObject(++count, birthDate);
				insertUser.setString(++count, "Lawyer");
				rows += insertUser.executeUpdate();

				count = 0;
				birthDate = LocalDate.parse("1971-04-19", CommandLine.format);
				salt = passMethods.getSalt();
				password = passMethods.getSaltHashedPassword("password", salt);
				insertUser.setString(++count, "123456789");
				insertUser.setString(++count, password);
				insertUser.setBytes(++count, salt);
				insertUser.setString(++count, "Hoppy Molly");
				insertUser.setString(++count, "5 Some street");
				insertUser.setObject(++count, birthDate);
				insertUser.setString(++count, "Teacher");
				rows += insertUser.executeUpdate();

				count = 0;
				birthDate = LocalDate.parse("2000-12-12", CommandLine.format);
				salt = passMethods.getSalt();
				password = passMethods.getSaltHashedPassword("password", salt);
				insertUser.setString(++count, "555555555");
				insertUser.setString(++count, password);
				insertUser.setBytes(++count, salt);
				insertUser.setString(++count, "Bobby Smith");
				insertUser.setString(++count, "123 Street");
				insertUser.setObject(++count, birthDate);
				insertUser.setString(++count, "Student");
				rows += insertUser.executeUpdate();

				System.out.println("Successfully inserted " + rows + " sample users!");
			} catch (SQLException e) {
				System.err.println("Exception triggered when inserting sample users!");
			}
		} catch (SQLException e) {
			// success = false;
			System.err.println("User table already exists!");
			// e.printStackTrace();
		}

		try {
			// @formatter:off
			PreparedStatement createListingTb = conn.prepareStatement("CREATE TABLE Listing ("
					+ " Type VARCHAR(15),"
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " City VARCHAR(20),"
					+ " Latitude DECIMAL(6,4),"
					+ " Longitude DECIMAL(7,4),"
					+ " Amenities SET('Wifi','Kitchen','Washer','Dryer',"
					+ "'Air conditioning','Heating','Dedicated workspace', 'TV',"
					+ "'Hair dryer','Iron','Pool','Hot tub','Free parking',"
					+ "'EV charger','Crib','Gym','BBQ grill','Breakfast',"
					+ "'Indoor fireplace','Smoking allowed','Beachfront',"
					+ "'Waterfront','Ski-in/ski-out','Smoke alarm','Carbon monoxide alarm'),"
					+ " PRIMARY KEY(Street, Number, PostalCode, Country)"
					+ ")");
			// @formatter:on
			createListingTb.executeUpdate();
			createListingTb.close();

			// insert rows to table
			try {
				int count = 0, rows = 0;
				BigDecimal latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("51.123")));
				BigDecimal longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("108.3")));
				insertListing.setString(++count, "apartment");
				insertListing.setString(++count, "Expensive Road");
				insertListing.setInt(++count, 858);
				insertListing.setString(++count, "B3C3B2");
				insertListing.setString(++count, "United States");
				insertListing.setString(++count, "LA");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Dryer,TV,Iron,Smoke alarm");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("12.2")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("34.661")));
				insertListing.setString(++count, "room");
				insertListing.setString(++count, "Main Street");
				insertListing.setInt(++count, 122);
				insertListing.setString(++count, "A1A1A1");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Wifi,Kitchen,Washer");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("12.3451")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("34.589")));
				insertListing.setString(++count, "room");
				insertListing.setString(++count, "Main Street");
				insertListing.setInt(++count, 125);
				insertListing.setString(++count, "A1A1A1");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Heating,Free parking");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("12")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("23.5411")));
				insertListing.setString(++count, "house");
				insertListing.setString(++count, "Second Main Street");
				insertListing.setInt(++count, 43);
				insertListing.setString(++count, "A1A1A3");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Air conditioning,TV,Pool");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-30.453")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-70.71")));
				insertListing.setString(++count, "house");
				insertListing.setString(++count, "Some Street");
				insertListing.setInt(++count, 4);
				insertListing.setString(++count, "P3CC");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "City");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Wifi,Gym,Beachfront");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-2.1234")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("100")));
				insertListing.setString(++count, "apartment");
				insertListing.setString(++count, "Sesame Street");
				insertListing.setInt(++count, 1131);
				insertListing.setString(++count, "C1C1C1");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Waterfront");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-2.1")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("99.9812")));
				insertListing.setString(++count, "room");
				insertListing.setString(++count, "Sesame Street");
				insertListing.setInt(++count, 1134);
				insertListing.setString(++count, "C1C1C1");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "Gym,Heating");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("76.2")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("65")));
				insertListing.setString(++count, "house");
				insertListing.setString(++count, "ABC Road");
				insertListing.setInt(++count, 123);
				insertListing.setString(++count, "ABCD");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "ski-in/ski-out,indoor fireplace");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-60.4")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("120.43")));
				insertListing.setString(++count, "house");
				insertListing.setString(++count, "Real Street");
				insertListing.setInt(++count, 89);
				insertListing.setString(++count, "L1A1A2");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "bbq grill,smoking allowed,TV");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("43.78")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("45.1")));
				insertListing.setString(++count, "apartment");
				insertListing.setString(++count, "Not Scam Avenue");
				insertListing.setInt(++count, 8);
				insertListing.setString(++count, "Q1Q1Q1");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "carbon monoxide alarm,smoke alarm");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("66.564")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("10.6")));
				insertListing.setString(++count, "house");
				insertListing.setString(++count, "Flower Road");
				insertListing.setInt(++count, 456);
				insertListing.setString(++count, "M1M2M3");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "Toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "hair dryer,pool,crib,wifi");
				rows += insertListing.executeUpdate();

				count = 0;
				latitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("45.3462")));
				longitude = new BigDecimal(CommandLine.coordinatesDf.format(Double.parseDouble("-45.2")));
				insertListing.setString(++count, "room");
				insertListing.setString(++count, "Not Free Circle");
				insertListing.setInt(++count, 19);
				insertListing.setString(++count, "C12C3");
				insertListing.setString(++count, "Canada");
				insertListing.setString(++count, "toronto");
				insertListing.setBigDecimal(++count, latitude);
				insertListing.setBigDecimal(++count, longitude);
				insertListing.setString(++count, "ev charger,free parking");
				rows += insertListing.executeUpdate();

				System.out.println("Successfully inserted " + rows + " sample listings!");
			} catch (SQLException e) {
				System.err.println("Exception triggered when inserting sample listings!");
			}
		} catch (SQLException e) {
			// success = false;
			System.err.println("Listing table already exists!");
			// e.printStackTrace();
		}

		try {
			// @formatter:off
			PreparedStatement createHostsTb = conn.prepareStatement("CREATE TABLE Hosts ("
					+ " SIN CHAR(9) NOT NULL,"
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " PRIMARY KEY(Street, Number, PostalCode, Country),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN) ON DELETE CASCADE,"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country) ON DELETE CASCADE"
					+ ")");
			// @formatter:on
			createHostsTb.executeUpdate();
			createHostsTb.close();

			// insert rows to table
			try {
				int count = 0, rows = 0;
				insertHosts.setString(++count, "111111111");
				insertHosts.setString(++count, "Expensive Road");
				insertHosts.setInt(++count, 858);
				insertHosts.setString(++count, "B3C3B2");
				insertHosts.setString(++count, "United States");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "111111111");
				insertHosts.setString(++count, "Main Street");
				insertHosts.setInt(++count, 122);
				insertHosts.setString(++count, "A1A1A1");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "111111111");
				insertHosts.setString(++count, "Some Street");
				insertHosts.setInt(++count, 4);
				insertHosts.setString(++count, "P3CC");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Main Street");
				insertHosts.setInt(++count, 125);
				insertHosts.setString(++count, "A1A1A1");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Second Main Street");
				insertHosts.setInt(++count, 43);
				insertHosts.setString(++count, "A1A1A3");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Sesame Street");
				insertHosts.setInt(++count, 1131);
				insertHosts.setString(++count, "C1C1C1");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Sesame Street");
				insertHosts.setInt(++count, 1134);
				insertHosts.setString(++count, "C1C1C1");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "ABC Road");
				insertHosts.setInt(++count, 123);
				insertHosts.setString(++count, "ABCD");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Real Street");
				insertHosts.setInt(++count, 89);
				insertHosts.setString(++count, "L1A1A2");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Not Scam Avenue");
				insertHosts.setInt(++count, 8);
				insertHosts.setString(++count, "Q1Q1Q1");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Flower Road");
				insertHosts.setInt(++count, 456);
				insertHosts.setString(++count, "M1M2M3");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				count = 0;
				insertHosts.setString(++count, "555555555");
				insertHosts.setString(++count, "Not Free Circle");
				insertHosts.setInt(++count, 19);
				insertHosts.setString(++count, "C12C3");
				insertHosts.setString(++count, "Canada");
				rows += insertHosts.executeUpdate();

				System.out.println("Successfully inserted " + rows + " sample hosts!");
			} catch (SQLException e) {
				System.err.println("Exception triggered when inserting sample hosts!");
			}
		} catch (SQLException e) {
			// success = false;
			System.err.println("Hosts table already exists!");
			// e.printStackTrace();
		}

		try {
			// @formatter:off
			PreparedStatement createAvailabilityTb = conn.prepareStatement("CREATE TABLE Availability ("
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " Date DATE,"
					+ " Available BOOL NOT NULL,"
					+ " Price DECIMAL(10,2),"
					+ " PRIMARY KEY(Street, Number, PostalCode, Country, Date),"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country) ON DELETE CASCADE"
					+ ")");
			// @formatter:on
			createAvailabilityTb.executeUpdate();
			createAvailabilityTb.close();

			// insert rows to table
			try {
				int count = 0, rows = 0;
				LocalDate date = LocalDate.parse("2023-09-10", CommandLine.format);
				BigDecimal price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("756.5")));
				insertAvailability.setString(++count, "Expensive Road");
				insertAvailability.setInt(++count, 858);
				insertAvailability.setString(++count, "B3C3B2");
				insertAvailability.setString(++count, "United States");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-11", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("756.5")));
				insertAvailability.setString(++count, "Expensive Road");
				insertAvailability.setInt(++count, 858);
				insertAvailability.setString(++count, "B3C3B2");
				insertAvailability.setString(++count, "United States");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-12", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("756.5")));
				insertAvailability.setString(++count, "Expensive Road");
				insertAvailability.setInt(++count, 858);
				insertAvailability.setString(++count, "B3C3B2");
				insertAvailability.setString(++count, "United States");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-28", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("120.13")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 122);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-29", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("120.13")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 122);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, false);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-30", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("120.13")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 122);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-14", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("85")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 125);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, false);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-18", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("85")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 125);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-19", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("85")));
				insertAvailability.setString(++count, "Main Street");
				insertAvailability.setInt(++count, 125);
				insertAvailability.setString(++count, "A1A1A1");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, false);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-20", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("510.34")));
				insertAvailability.setString(++count, "Second Main Street");
				insertAvailability.setInt(++count, 43);
				insertAvailability.setString(++count, "A1A1A3");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-21", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("510.34")));
				insertAvailability.setString(++count, "Second Main Street");
				insertAvailability.setInt(++count, 43);
				insertAvailability.setString(++count, "A1A1A3");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, false);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-22", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("510.34")));
				insertAvailability.setString(++count, "Second Main Street");
				insertAvailability.setInt(++count, 43);
				insertAvailability.setString(++count, "A1A1A3");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-16", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("723.4")));
				insertAvailability.setString(++count, "Some Street");
				insertAvailability.setInt(++count, 4);
				insertAvailability.setString(++count, "P3CC");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-17", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("723.4")));
				insertAvailability.setString(++count, "Some Street");
				insertAvailability.setInt(++count, 4);
				insertAvailability.setString(++count, "P3CC");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, true);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();
				count = 0;
				date = LocalDate.parse("2023-09-18", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("723.4")));
				insertAvailability.setString(++count, "Some Street");
				insertAvailability.setInt(++count, 4);
				insertAvailability.setString(++count, "P3CC");
				insertAvailability.setString(++count, "Canada");
				insertAvailability.setObject(++count, date);
				insertAvailability.setBoolean(++count, false);
				insertAvailability.setBigDecimal(++count, price);
				rows += insertAvailability.executeUpdate();

				System.out.println("Successfully inserted " + rows + " sample availability!");
			} catch (SQLException e) {
				System.err.println("Exception triggered when inserting sample availability!");
			}
		} catch (SQLException e) {
			// success = false;
			System.err.println("Availability table already exists!");
			// e.printStackTrace();
		}

		try {
			// @formatter:off
			PreparedStatement createBookedTb = conn.prepareStatement("CREATE TABLE Booked ("
					+ " BID INT NOT NULL AUTO_INCREMENT,"
					+ " SIN CHAR(9) NOT NULL,"
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " FromDate DATE,"
					+ " ToDate DATE,"
					+ " PaymentMethod SET('Credit card', 'Debit card', 'Cash'),"
					+ " Price DECIMAL(10,2),"
					+ " Canceled BOOL NOT NULL DEFAULT 0,"
					+ " Updated BOOL NOT NULL DEFAULT 0,"
					+ " PRIMARY KEY (BID),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN) ON DELETE CASCADE,"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country) ON DELETE SET NULL"
					+ ")");
			// @formatter:on
			createBookedTb.executeUpdate();
			createBookedTb.close();

			// insert rows to table
			try {
				int count = 0, rows = 0;
				LocalDate date = LocalDate.parse("2023-09-29", CommandLine.format);
				BigDecimal price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("120.13")));
				insertBooked.setString(++count, "555555555");
				insertBooked.setString(++count, "Main Street");
				insertBooked.setInt(++count, 122);
				insertBooked.setString(++count, "A1A1A1");
				insertBooked.setString(++count, "Canada");
				insertBooked.setObject(++count, date);
				insertBooked.setObject(++count, date);
				insertBooked.setString(++count, "cash");
				insertBooked.setBigDecimal(++count, price);
				rows += insertBooked.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-14", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("85")));
				insertBooked.setString(++count, "555555555");
				insertBooked.setString(++count, "Main Street");
				insertBooked.setInt(++count, 125);
				insertBooked.setString(++count, "A1A1A1");
				insertBooked.setString(++count, "Canada");
				insertBooked.setObject(++count, date);
				insertBooked.setObject(++count, date);
				insertBooked.setString(++count, "credit card");
				insertBooked.setBigDecimal(++count, price);
				rows += insertBooked.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-19", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("85")));
				insertBooked.setString(++count, "123456789");
				insertBooked.setString(++count, "Main Street");
				insertBooked.setInt(++count, 125);
				insertBooked.setString(++count, "A1A1A1");
				insertBooked.setString(++count, "Canada");
				insertBooked.setObject(++count, date);
				insertBooked.setObject(++count, date);
				insertBooked.setString(++count, "debit card");
				insertBooked.setBigDecimal(++count, price);
				rows += insertBooked.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-18", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("723.4")));
				insertBooked.setString(++count, "123456789");
				insertBooked.setString(++count, "Some Street");
				insertBooked.setInt(++count, 4);
				insertBooked.setString(++count, "P3CC");
				insertBooked.setString(++count, "Canada");
				insertBooked.setObject(++count, date);
				insertBooked.setObject(++count, date);
				insertBooked.setString(++count, "credit card");
				insertBooked.setBigDecimal(++count, price);
				rows += insertBooked.executeUpdate();

				count = 0;
				date = LocalDate.parse("2023-09-21", CommandLine.format);
				price = new BigDecimal(CommandLine.priceDf.format(Double.parseDouble("510.34")));
				insertBooked.setString(++count, "123456789");
				insertBooked.setString(++count, "Second Main Street");
				insertBooked.setInt(++count, 43);
				insertBooked.setString(++count, "A1A1A3");
				insertBooked.setString(++count, "Canada");
				insertBooked.setObject(++count, date);
				insertBooked.setObject(++count, date);
				insertBooked.setString(++count, "credit card");
				insertBooked.setBigDecimal(++count, price);
				rows += insertBooked.executeUpdate();

				System.out.println("Successfully inserted " + rows + " sample booked!");
			} catch (SQLException e) {
				System.err.println("Exception triggered when inserting sample booked!");
			}
		} catch (SQLException e) {
			// success = false;
			System.err.println("Booked table already exists!");
			// e.printStackTrace();
		}

		try {
			// @formatter:off
			PreparedStatement createCancellationTb = conn.prepareStatement("CREATE TABLE Cancellation ("
					+ " BID INT NOT NULL,"
					+ " SIN CHAR(9) NOT NULL,"
					+ " PRIMARY KEY (BID),"
					+ " FOREIGN KEY (BID) REFERENCES Booked(BID),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN) ON DELETE CASCADE"
					+ ")");
			// @formatter:on
			createCancellationTb.executeUpdate();
			createCancellationTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("Cancellation table already exists!");
			// e.printStackTrace();
		}

		try {
			PreparedStatement createCommentTb = conn.prepareStatement("CREATE TABLE Comment ("
					+ " CID INT NOT NULL AUTO_INCREMENT,"
					+ " SIN CHAR(9),"
					+ " Rating TINYINT,"
					+ " Text TEXT(65000),"
					+ " Date DATETIME DEFAULT CURRENT_TIMESTAMP,"
					+ " PRIMARY KEY(CID),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN) ON DELETE SET NULL"
					+ ")");
			createCommentTb.executeUpdate();
			createCommentTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("Comment table already exists!");
			// e.printStackTrace();
		}

		try {
			PreparedStatement createCommentUserTb = conn.prepareStatement("CREATE TABLE CommentOnUser ("
					+ " CID INT,"
					+ " SINComment CHAR(9),"
					+ " PRIMARY KEY(CID),"
					+ " FOREIGN KEY (CID) REFERENCES Comment(CID),"
					+ " FOREIGN KEY (SINComment) REFERENCES User(SIN) ON DELETE CASCADE"
					+ ")");
			createCommentUserTb.executeUpdate();
			// createCommentUserTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("CommentOnUser table already exists!");
			// e.printStackTrace();
		}

		try {
			PreparedStatement createCommentListingTb = conn.prepareStatement("CREATE TABLE CommentOnListing ("
					+ " CID INT,"
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " PRIMARY KEY(CID),"
					+ " FOREIGN KEY (CID) REFERENCES Comment(CID),"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES Listing(Street, Number, PostalCode, Country) ON DELETE CASCADE"
					+ ")");
			createCommentListingTb.executeUpdate();
			createCommentListingTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("CommentOnListing table already exists!");
			// e.printStackTrace();
		}

		try {
			ScriptRunner sr = new ScriptRunner(conn);
			Reader reader = new BufferedReader(new FileReader("MyBnBProject/src/main/sampleRecords.sql"));
			sr.setLogWriter(null);
			sr.setErrorLogWriter(null);
			sr.runScript(reader);
			reader.close();
		} catch (Exception e) {
			success = false;
			System.err.println("Inserting sample records failed!");
			e.printStackTrace();
		}

		return success;
	}

	private boolean prepareStatements() {
		boolean success = true;
		try {
			// User statements
			insertUser = conn.prepareStatement("INSERT INTO User"
					+ " (SIN, Password, Salt, Name, Address, Birthdate, Occupation)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			selectUserBySIN = conn.prepareStatement("SELECT * FROM User WHERE SIN=?");
			selectUserByListingBooked = conn.prepareStatement("SELECT DISTINCT User.* FROM User"
					+ " INNER JOIN Booked ON User.SIN=Booked.SIN"
					+ " INNER JOIN Hosts ON Booked.Street=Hosts.Street AND Booked.Number=Hosts.Number"
					+ " AND Booked.PostalCode=Hosts.PostalCode AND Booked.Country=Hosts.Country WHERE Hosts.SIN=?");
			deleteUser = conn.prepareStatement("DELETE FROM User WHERE SIN=?");
			// Listing statements
			insertListing = conn.prepareStatement("INSERT INTO Listing"
					+ " (Type, Street, Number, PostalCode, Country, City, Latitude, Longitude, Amenities)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			selectAllListing = conn.prepareStatement("SELECT * FROM Listing NATURAL JOIN Availability"
					+ " WHERE Available=true");
			selectListingAddr = conn.prepareStatement("SELECT * FROM Listing NATURAL JOIN Availability"
					+ " WHERE Street=? AND Number=? AND PostalCode=? AND Country=? AND City=? AND Available=true");
			selectListingPostalCode = conn.prepareStatement("SELECT * FROM Listing NATURAL JOIN Availability"
					+ " WHERE PostalCode LIKE ? AND Available=true");
			selectListingBooked = conn.prepareStatement("SELECT Listing.* FROM Listing INNER JOIN Booked"
					+ " ON Listing.Street=Booked.Street AND Listing.Number=Booked.Number"
					+ " AND Listing.PostalCode=Booked.PostalCode AND Listing.Country=Booked.Country WHERE Booked.SIN=?");
			deleteListing = conn
					.prepareStatement("DELETE FROM Listing WHERE Street=? AND Number=? AND PostalCode=? AND Country=?");
			deleteListingHostedBySIN = conn.prepareStatement("DELETE Listing FROM Listing INNER JOIN Hosts"
					+ " ON Listing.Street=Hosts.Street AND Listing.Number=Hosts.Number AND Listing.PostalCode=Hosts.PostalCode"
					+ " AND Listing.Country=Hosts.Country WHERE Hosts.SIN=?");
			// Hosts statements
			insertHosts = conn.prepareStatement("INSERT INTO Hosts"
					+ " (SIN, Street, Number, PostalCode, Country)"
					+ " VALUES (?, ?, ?, ?, ?)");
			selectHostsBySIN = conn.prepareStatement("SELECT * FROM Hosts NATURAL JOIN Listing WHERE SIN=?");
			// Availability statements
			insertAvailability = conn.prepareStatement("INSERT INTO Availability"
					+ " (Street, Number, PostalCode, Country, Date, Available, Price)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			insertOrUpdateAvailability = conn.prepareStatement("INSERT INTO Availability"
					+ " (Street, Number, PostalCode, Country, Date, Available, Price)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE Available=?, Price=?");
			updateAvailability = conn.prepareStatement("UPDATE Availability SET Available=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND Date=?");
			updateAvailabilityPrice = conn.prepareStatement("UPDATE Availability SET Price=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND Date=?");
			updateAvailabilityBetweenDate = conn.prepareStatement("UPDATE Availability SET Available=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?)");
			selectAllAvailBetweenDate = conn.prepareStatement("SELECT * FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?)");
			selectAvailBetweenDate = conn.prepareStatement("SELECT * FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			sumAvailBetweenDate = conn.prepareStatement("SELECT SUM(Price) AS TotalPrice FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?)");
			deleteAvailBetweenDate = conn.prepareStatement("DELETE FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			getMinMaxPrice = conn.prepareStatement("SELECT Type, MIN(Price), MAX(Price) FROM Listing NATURAL JOIN"
					+ " Availability WHERE Type=? GROUP BY Type ORDER BY MIN(Price), MAX(Price)");
			// Booked statements
			insertBooked = conn.prepareStatement("INSERT INTO Booked"
					+ " (SIN, Street, Number, PostalCode, Country, FromDate, ToDate, PaymentMethod, Price)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)");
			updateBookedPriceByBIN = conn.prepareStatement("UPDATE Booked SET Price=?, Updated=? WHERE BID=?");
			updateBookedUpdatedBySIN = conn.prepareStatement("UPDATE Booked SET Updated=0 WHERE SIN=?");
			cancelBooked = conn.prepareStatement("UPDATE Booked SET Canceled=1, Updated=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " FromDate=? AND ToDate=?");
			cancelBookedByListing = conn.prepareStatement("UPDATE Booked SET Canceled=1, Updated=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=?");
			cancelBookedByHostedListing = conn.prepareStatement("UPDATE Booked INNER JOIN Hosts ON"
					+ " Booked.Street=Hosts.Street AND Booked.Number=Hosts.Number AND Booked.PostalCode=Hosts.PostalCode"
					+ " AND Booked.Country=Hosts.Country SET Booked.Canceled=1, Booked.Updated=1 WHERE Hosts.SIN=?");
			selectAllBookedBySIN = conn.prepareStatement("SELECT * FROM Booked WHERE SIN=?");
			selectBookedBySIN = conn.prepareStatement("SELECT * FROM Booked WHERE SIN=? AND Canceled=?");
			selectAllBookedByHostListings = conn.prepareStatement("SELECT A.* FROM Booked AS A NATURAL JOIN"
					+ " Listing INNER JOIN Hosts AS B ON A.Street=B.Street AND A.Number=B.Number AND"
					+ " A.PostalCode=B.PostalCode AND A.Country=B.Country WHERE B.SIN=?");
			selectBookedByHostListings = conn.prepareStatement("SELECT A.* FROM Booked AS A NATURAL JOIN"
					+ " Listing INNER JOIN Hosts AS B ON A.Street=B.Street AND A.Number=B.Number AND"
					+ " A.PostalCode=B.PostalCode AND A.Country=B.Country WHERE B.SIN=? AND A.Canceled=?");
			selectBookedByAddress = conn.prepareStatement("SELECT * FROM BOOKED WHERE Street=? AND"
					+ " Number=? AND PostalCode=? AND Country=?");
			selectBookedByAddressDate = conn.prepareStatement("SELECT * FROM BOOKED WHERE Street=? AND"
					+ " Number=? AND PostalCode=? AND Country=? AND"
					+ " ((FromDate>=? AND FromDate<=?) OR (ToDate>=? AND ToDate<=?)) AND Canceled=?");
			selectBookedUpdatedBySIN = conn.prepareStatement("SELECT * FROM BOOKED WHERE SIN=? AND Updated=1");
			// Cancellation statements
			insertCancellation = conn
					.prepareStatement("INSERT INTO Cancellation (BID, SIN) VALUES (?, ?) ON DUPLICATE KEY"
							+ " UPDATE SIN=SIN");
			// Report statements
			reportNumBookingsCity = conn.prepareStatement("SELECT City, COUNT(*) AS TotalBooking"
					+ " from Booked NATURAL JOIN Listing where (FromDate BETWEEN ? AND ?) AND (ToDate BETWEEN ? AND ?)"
					+ " AND Canceled=0 GROUP BY CITY");
			reportNumBookingsPostalCode = conn.prepareStatement("SELECT PostalCode, COUNT(*) AS TotalBooking"
					+ " from Booked where (FromDate BETWEEN ? AND ?) AND (ToDate BETWEEN ? AND ?) AND Canceled=0 GROUP BY PostalCode");
			reportRenterBooking = conn.prepareStatement("SELECT Name, COUNT(SIN) AS TotalBooking"
					+ " from Booked NATURAL JOIN User where (FromDate BETWEEN ? AND ?) AND (ToDate BETWEEN ? AND ?)"
					+ " AND Canceled=0 GROUP BY Name ORDER BY TotalBooking DESC");
			reportRenterBookingCity = conn.prepareStatement("SELECT Name, City, COUNT(SIN) AS TotalBooking"
					+ " from Booked NATURAL JOIN Listing NATURAL JOIN User where (FromDate BETWEEN ? AND ?) AND"
					+ " (ToDate BETWEEN ? AND ?) AND Canceled=0 GROUP BY Name, City ORDER BY TotalBooking DESC");
			reportNumListingsCount = conn.prepareStatement("SELECT Country, COUNT(*) AS TotalListing FROM Listing"
					+ " GROUP BY Country");
			reportNumListingsCountCity = conn.prepareStatement("SELECT Country, City, COUNT(*) AS TotalListing FROM"
					+ " Listing GROUP BY Country, City");
			reportNumListingsCountCityPost = conn.prepareStatement("SELECT Country, City, PostalCode, COUNT(*) AS"
					+ " TotalListing FROM Listing GROUP BY Country, City, PostalCode");
			reportRankHostCount = conn.prepareStatement("SELECT Name, Country, COUNT(*) AS TotalListing FROM Listing"
					+ " NATURAL JOIN Hosts NATURAL JOIN User GROUP BY Country, Name ORDER BY TotalListing DESC");
			reportRankHostCountCity = conn.prepareStatement("SELECT Name, Country, City, COUNT(*) AS TotalListing"
					+ " FROM Listing NATURAL JOIN Hosts NATURAL JOIN User GROUP BY Country, City, Name ORDER BY"
					+ " TotalListing DESC");
			reportNumCancelled = conn.prepareStatement("SELECT Name, COUNT(A.BID) AS TotalCancelled FROM User NATURAL"
					+ " JOIN Cancellation as A INNER JOIN Booked as B ON A.BID=B.BID WHERE FromDate LIKE ? OR ToDate LIKE"
					+ " ? GROUP BY Name HAVING TotalCancelled in (SELECT TotalCancelled FROM LargestCancellation)");
			reportHost10Percent = conn
					.prepareStatement("SELECT B.Name, A.City, A.Country, COUNT(*) AS NumberListing, Total AS"
							+ " TotalListing FROM Listing AS A NATURAL JOIN Hosts NATURAL JOIN User AS B NATURAL JOIN (SELECT L.City,"
							+ " L.Country, COUNT(*) AS Total FROM Listing AS L NATURAL JOIN Hosts GROUP BY L.Country, L.City ORDER BY"
							+ " Total) AS T GROUP BY A.Country, A.City, B.Name HAVING NumberListing > (TotalListing * 0.1) ORDER BY TotalListing");
			// View statements
			createLargestCancelView = conn
					.prepareStatement("CREATE OR REPLACE VIEW LargestCancellation As SELECT DISTINCT"
							+ " COUNT(A.BID) AS TotalCancelled FROM User NATURAL JOIN Cancellation AS A INNER JOIN Booked AS B ON"
							+ " A.BID=B.BID WHERE FromDate LIKE ? OR ToDate LIKE ? GROUP BY Name ORDER BY TotalCancelled DESC LIMIT 1");
			// Comment statements
			insertComment = conn.prepareStatement("INSERT INTO Comment (SIN, Rating, Text) VALUES (?, ?, ?)",
					Statement.RETURN_GENERATED_KEYS);
			insertCommentOnListing = conn
					.prepareStatement("INSERT INTO CommentOnListing (CID, Street, Number, PostalCode, Country)"
							+ " VALUES (?, ?, ?, ?, ?)");
			insertCommentOnUser = conn.prepareStatement("INSERT INTO CommentOnUser (CID, SINComment) VALUES (?, ?)");
			selectCommentByListing = conn.prepareStatement(
					"SELECT * FROM User RIGHT JOIN Comment ON User.SIN=Comment.SIN INNER JOIN CommentOnListing ON Comment.CID=CommentOnListing.CID"
							+ " WHERE CommentOnListing.Street=? AND CommentOnListing.Number=?"
							+ " AND CommentOnListing.PostalCode=? AND CommentOnListing.Country=?");
			selectCommentByUser = conn
					.prepareStatement("SELECT * FROM User RIGHT JOIN Comment ON User.SIN=Comment.SIN INNER JOIN"
							+ " CommentOnUser ON Comment.CID=CommentOnUser.CID WHERE CommentOnUser.SINComment=?");
			selectCommentMadeByUser = conn
					.prepareStatement("SELECT * FROM Comment LEFT JOIN CommentOnUser ON Comment.CID=CommentOnUser.CID"
							+ " LEFT JOIN CommentOnListing ON Comment.CID=CommentOnListing.CID WHERE Comment.SIN=?");
			selectAllListingWComments = conn
					.prepareStatement(
							"SELECT DISTINCT Street, Number, PostalCode, Country FROM Comment NATURAL JOIN CommentOnListing");
		} catch (SQLException e) {
			success = false;
			System.err.println("Prepared statements could not be created!");
			e.printStackTrace();
		}
		return success;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a user.
	public int insertUser(String sin, String password, byte[] salt, String name, String address, LocalDate birthdate,
			String occupation) {
		int rows = 0;
		try {
			int count = 0;
			insertUser.setString(++count, sin);
			insertUser.setString(++count, password);
			insertUser.setBytes(++count, salt);
			insertUser.setString(++count, name);
			insertUser.setString(++count, address);
			insertUser.setObject(++count, birthdate);
			insertUser.setString(++count, occupation);
			rows = insertUser.executeUpdate();
		} catch (SQLException e) {
			System.err.println(
					"Exception triggered when inserting user! Your SIN may already be attached to an account.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select a user by SIN.
	public User selectUserBySIN(String sin) {
		User user = new User();
		try {
			selectUserBySIN.setString(1, sin);
			ResultSet rs = selectUserBySIN.executeQuery();

			while (rs.next()) {
				user.sin = rs.getString("SIN");
				user.password = rs.getString("Password");
				user.salt = rs.getBytes("Salt");
				user.name = rs.getString("Name");
				user.address = rs.getString("Address");
				user.birthdate = rs.getObject("Birthdate", LocalDate.class);
				user.occupation = rs.getString("Occupation");
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting user by SIN!");
			e.printStackTrace();
		}

		if (sin == null || user.sin == null) {
			user = null;
		}

		return user;
	}

	// Controls the execution of a select query.
	// Functionality: Select users by listing booked.
	public ArrayList<User> selectUserByListingBooked(String sin) {
		ArrayList<User> users = new ArrayList<User>();
		try {
			int count = 0;
			selectUserByListingBooked.setString(++count, sin);
			ResultSet rs = selectUserByListingBooked.executeQuery();

			while (rs.next()) {
				User user = new User();
				user.sin = rs.getString("SIN");
				user.password = rs.getString("Password");
				user.salt = rs.getBytes("Salt");
				user.name = rs.getString("Name");
				user.address = rs.getString("Address");
				user.birthdate = rs.getObject("Birthdate", LocalDate.class);
				user.occupation = rs.getString("Occupation");
				users.add(user);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting users by listing booked!");
			e.printStackTrace();
		}

		return users;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a listing.
	public int insertListing(String type, String street, int number, String postalCode, String country, String city,
			BigDecimal latitude, BigDecimal longitude, String amenities) {
		int rows = 0;
		try {
			int count = 0;
			insertListing.setString(++count, type);
			insertListing.setString(++count, street);
			insertListing.setInt(++count, number);
			insertListing.setString(++count, postalCode);
			insertListing.setString(++count, country);
			insertListing.setString(++count, city);
			insertListing.setBigDecimal(++count, latitude);
			insertListing.setBigDecimal(++count, longitude);
			insertListing.setString(++count, amenities);
			rows = insertListing.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting listing! "
					+ "Another listing with the same type and address may already exist.");
			// e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a delete query.
	// Functionality: Delete a listing.
	public int deleteListing(String street, int number, String postalCode, String country) {
		int rows = 0;
		try {
			int count = 0;
			deleteListing.setString(++count, street);
			deleteListing.setInt(++count, number);
			deleteListing.setString(++count, postalCode);
			deleteListing.setString(++count, country);
			rows = deleteListing.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when deleting listing!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a hosts record.
	public int insertHosts(String sin, String street, int number, String postalCode, String country) {
		int rows = 0;
		try {
			int count = 0;
			insertHosts.setString(++count, sin);
			insertHosts.setString(++count, street);
			insertHosts.setInt(++count, number);
			insertHosts.setString(++count, postalCode);
			insertHosts.setString(++count, country);
			rows = insertHosts.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting hosts! "
					+ "Another host with the same address may already exist.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select all hosts records by SIN.
	public ArrayList<Listing> selectHostsBySIN(String sin) {
		ArrayList<Listing> hostedListings = new ArrayList<>();
		try {
			selectHostsBySIN.setString(1, sin);
			ResultSet rs = selectHostsBySIN.executeQuery();

			while (rs.next()) {
				Listing temp = new Listing();
				temp.type = rs.getString("Type");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				hostedListings.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting hosts by SIN!");
			e.printStackTrace();
		}
		return hostedListings;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert an availability.
	public int insertAvailability(String street, int number, String postalCode, String country, LocalDate date,
			boolean available, BigDecimal price) {
		int rows = 0;
		try {
			int count = 0;
			insertAvailability.setString(++count, street);
			insertAvailability.setInt(++count, number);
			insertAvailability.setString(++count, postalCode);
			insertAvailability.setString(++count, country);
			insertAvailability.setObject(++count, date);
			insertAvailability.setBoolean(++count, available);
			insertAvailability.setBigDecimal(++count, price);
			rows = insertAvailability.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting availability! "
					+ "Another availiability with the same date and address may already exist.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an insert or update query.
	// Functionality: Insert or update an availability.
	public int insertOrUpdateAvailability(String street, int number, String postalCode, String country, LocalDate date,
			boolean available, BigDecimal price) {
		int rows = 0;
		try {
			int count = 0;
			insertOrUpdateAvailability.setString(++count, street);
			insertOrUpdateAvailability.setInt(++count, number);
			insertOrUpdateAvailability.setString(++count, postalCode);
			insertOrUpdateAvailability.setString(++count, country);
			insertOrUpdateAvailability.setObject(++count, date);
			insertOrUpdateAvailability.setBoolean(++count, available);
			insertOrUpdateAvailability.setBigDecimal(++count, price);
			insertOrUpdateAvailability.setBoolean(++count, available);
			insertOrUpdateAvailability.setBigDecimal(++count, price);
			rows = insertOrUpdateAvailability.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting or updating availability!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update an availability's availability.
	public int updateAvailability(String street, int number, String postalCode, String country, LocalDate date,
			boolean available) {
		int rows = 0;
		try {
			int count = 0;
			updateAvailability.setBoolean(++count, available);
			updateAvailability.setString(++count, street);
			updateAvailability.setInt(++count, number);
			updateAvailability.setString(++count, postalCode);
			updateAvailability.setString(++count, country);
			updateAvailability.setObject(++count, date);
			rows = updateAvailability.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when updating availability!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update an availability's availability between dates.
	public int updateAvailability(String street, int number, String postalCode, String country, LocalDate from,
			LocalDate to, boolean available) {
		int rows = 0;
		try {
			int count = 0;
			updateAvailabilityBetweenDate.setBoolean(++count, available);
			updateAvailabilityBetweenDate.setString(++count, street);
			updateAvailabilityBetweenDate.setInt(++count, number);
			updateAvailabilityBetweenDate.setString(++count, postalCode);
			updateAvailabilityBetweenDate.setString(++count, country);
			updateAvailabilityBetweenDate.setObject(++count, from);
			updateAvailabilityBetweenDate.setObject(++count, to);
			rows = updateAvailabilityBetweenDate.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when updating availability!");
			e.printStackTrace();
		}

		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update an availability's price.
	public int updateAvailability(String street, int number, String postalCode, String country, LocalDate date,
			BigDecimal price) {
		int rows = 0;
		try {
			int count = 0;
			updateAvailabilityPrice.setBigDecimal(++count, price);
			updateAvailabilityPrice.setString(++count, street);
			updateAvailabilityPrice.setInt(++count, number);
			updateAvailabilityPrice.setString(++count, postalCode);
			updateAvailabilityPrice.setString(++count, country);
			updateAvailabilityPrice.setObject(++count, date);
			rows = updateAvailabilityPrice.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when updating availability!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update an availability.
	/*
	 * public int updateAvailability(String street, int number, String postalCode,
	 * String country, LocalDate date,
	 * boolean available) {
	 * int rows = 0;
	 * try {
	 * int count = 0;
	 * selectAllAvailBetweenDate.setString(++count, street);
	 * selectAllAvailBetweenDate.setInt(++count, number);
	 * selectAllAvailBetweenDate.setString(++count, postalCode);
	 * selectAllAvailBetweenDate.setString(++count, country);
	 * selectAllAvailBetweenDate.setObject(++count, to);
	 * selectAllAvailBetweenDate.setObject(++count, from);
	 */

	// ResultSet rs = selectAllAvailBetweenDate.executeQuery();

	// count = 0;
	// while (rs.next()) {
	// Availability temp = new Availability();
	// temp.street = rs.getString("Street");
	// temp.number = rs.getInt("Number");
	// temp.postalCode = rs.getString("PostalCode");
	// temp.country = rs.getString("Country");
	// temp.date = rs.getObject("Date", LocalDate.class);
	// temp.available = rs.getBoolean("Available");
	// temp.price = rs.getBigDecimal("Price");
	// availabilities.add(temp);
	// }

	/*
	 * rs.close();
	 * } catch (SQLException e) {
	 * System.err.println("Exception triggered when updating availability!");
	 * e.printStackTrace();
	 * }
	 * return rows;
	 * }
	 */

	// Controls the execution of a select query.
	// Functionality: Select availabilities by date.

	public ArrayList<Availability> selectAvailBetweenDate(String street, int number, String postalCode, String country,
			LocalDate to, LocalDate from) {
		ArrayList<Availability> availabilities = new ArrayList<>();
		try {
			int count = 0;
			selectAllAvailBetweenDate.setString(++count, street);
			selectAllAvailBetweenDate.setInt(++count, number);
			selectAllAvailBetweenDate.setString(++count, postalCode);
			selectAllAvailBetweenDate.setString(++count, country);
			selectAllAvailBetweenDate.setObject(++count, to);
			selectAllAvailBetweenDate.setObject(++count, from);

			ResultSet rs = selectAllAvailBetweenDate.executeQuery();

			count = 0;
			while (rs.next()) {
				Availability temp = new Availability();
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.date = rs.getObject("Date", LocalDate.class);
				temp.available = rs.getBoolean("Available");
				temp.price = rs.getBigDecimal("Price");
				availabilities.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting availabilities by date!");
			e.printStackTrace();
		}
		return availabilities;
	}

	// Controls the execution of a select query.
	// Functionality: Select availabilities by date and available.
	public ArrayList<Availability> selectAvailBetweenDate(String street, int number, String postalCode, String country,
			LocalDate from, LocalDate to, boolean available) {
		ArrayList<Availability> availabilities = new ArrayList<>();
		try {
			int count = 0;
			selectAvailBetweenDate.setString(++count, street);
			selectAvailBetweenDate.setInt(++count, number);
			selectAvailBetweenDate.setString(++count, postalCode);
			selectAvailBetweenDate.setString(++count, country);
			selectAvailBetweenDate.setObject(++count, from);
			selectAvailBetweenDate.setObject(++count, to);
			selectAvailBetweenDate.setBoolean(++count, available);

			ResultSet rs = selectAvailBetweenDate.executeQuery();

			count = 0;
			while (rs.next()) {
				Availability temp = new Availability();
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.date = rs.getObject("Date", LocalDate.class);
				temp.available = rs.getBoolean("Available");
				temp.price = rs.getBigDecimal("Price");
				availabilities.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting availabilities by date and availability!");
			e.printStackTrace();
		}
		return availabilities;
	}

	// Controls the execution of a select query.
	// Functionality: Select sum availabilities's price by date.
	public BigDecimal sumAvailBetweenDate(String street, int number, String postalCode, String country,
			LocalDate from, LocalDate to) {
		BigDecimal sum = null;
		try {
			int count = 0;
			sumAvailBetweenDate.setString(++count, street);
			sumAvailBetweenDate.setInt(++count, number);
			sumAvailBetweenDate.setString(++count, postalCode);
			sumAvailBetweenDate.setString(++count, country);
			sumAvailBetweenDate.setObject(++count, from);
			sumAvailBetweenDate.setObject(++count, to);

			ResultSet rs = sumAvailBetweenDate.executeQuery();

			count = 0;
			while (rs.next()) {
				sum = rs.getBigDecimal("TotalPrice");
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when summing availabilities's price by date!");
			e.printStackTrace();
		}
		return sum;
	}

	// Controls the execution of a delete query.
	// Functionality: Delete availabilities by date and available.
	public int deleteAvailBetweenDate(String street, int number, String postalCode, String country, LocalDate from,
			LocalDate to, boolean available) {
		int rows = 0;
		try {
			int count = 0;
			deleteAvailBetweenDate.setString(++count, street);
			deleteAvailBetweenDate.setInt(++count, number);
			deleteAvailBetweenDate.setString(++count, postalCode);
			deleteAvailBetweenDate.setString(++count, country);
			deleteAvailBetweenDate.setObject(++count, from);
			deleteAvailBetweenDate.setObject(++count, to);
			deleteAvailBetweenDate.setBoolean(++count, available);
			rows = deleteAvailBetweenDate.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when deleting availabilities by date and availability!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select listings by address.
	public ArrayList<AvailabilityListing> searchListingAddr(String street, int number, String postalCode,
			String country,
			String city) {
		ArrayList<AvailabilityListing> listings = new ArrayList<>();
		try {
			int count = 0;
			selectListingAddr.setString(++count, street);
			selectListingAddr.setInt(++count, number);
			selectListingAddr.setString(++count, postalCode);
			selectListingAddr.setString(++count, country);
			selectListingAddr.setString(++count, city);
			ResultSet rs = selectListingAddr.executeQuery();

			initialAvailListings(rs, listings);
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listing by address!");
			e.printStackTrace();
		}
		return listings;
	}

	// Controls the execution of a select query.
	// Functionality: Select all listings.
	public ArrayList<AvailabilityListing> searchAllListing() {
		ArrayList<AvailabilityListing> listings = new ArrayList<>();
		try {
			ResultSet rs = selectAllListing.executeQuery();

			initialAvailListings(rs, listings);
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when all selecting listing!");
			e.printStackTrace();
		}
		return listings;
	}

	// Controls the execution of a select query.
	// Functionality: Select listings by postal code.
	public ArrayList<AvailabilityListing> searchListingPostalCode(String pattern) {
		ArrayList<AvailabilityListing> listings = new ArrayList<>();
		try {
			selectListingPostalCode.setString(1, pattern);
			ResultSet rs = selectListingPostalCode.executeQuery();

			initialAvailListings(rs, listings);
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listing by postal code!");
			e.printStackTrace();
		}
		return listings;
	}

	// Controls the execution of a select query.
	// Functionality: Select listings by SIN's booked records.
	public ArrayList<Listing> selectListingByBooked(String sin) {
		ArrayList<Listing> listings = new ArrayList<>();
		try {
			selectListingBooked.setString(1, sin);
			ResultSet rs = selectListingBooked.executeQuery();

			while (rs.next()) {
				Listing temp = new Listing();
				temp.type = rs.getString("Type");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.city = rs.getString("City");
				temp.latitude = rs.getBigDecimal("Latitude");
				temp.longitude = rs.getBigDecimal("Longitude");
				listings.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listing by user's booked records!");
			e.printStackTrace();
		}
		return listings;
	}

	public void initialAvailListings(ResultSet rs, ArrayList<AvailabilityListing> listings) throws SQLException {
		while (rs.next()) {
			String tempType = rs.getString("Type");
			String tempStreet = rs.getString("Street");
			int tempNumber = rs.getInt("Number");
			String tempPostalCode = rs.getString("PostalCode");
			String tempCountry = rs.getString("Country");
			String tempCity = rs.getString("City");
			BigDecimal tempLatitude = rs.getBigDecimal("Latitude");
			BigDecimal tempLongitude = rs.getBigDecimal("Longitude");
			String tempAmenities = rs.getString("Amenities");
			LocalDate date = rs.getObject("Date", LocalDate.class);
			boolean available = rs.getBoolean("Available");
			BigDecimal price = rs.getBigDecimal("Price");
			AvailabilityListing temp = new AvailabilityListing(tempType, tempStreet, tempNumber, tempPostalCode,
					tempCountry,
					tempCity, tempLatitude, tempLongitude, tempAmenities, date, available, price);
			listings.add(temp);
		}
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a booked record.
	public int insertBooked(String sin, String street, int number, String postalCode, String country, LocalDate from,
			LocalDate to, String paymentMethod, BigDecimal price) {
		int rows = 0;
		try {
			int count = 0;
			insertBooked.setString(++count, sin);
			insertBooked.setString(++count, street);
			insertBooked.setInt(++count, number);
			insertBooked.setString(++count, postalCode);
			insertBooked.setString(++count, country);
			insertBooked.setObject(++count, from);
			insertBooked.setObject(++count, to);
			insertBooked.setString(++count, paymentMethod);
			insertBooked.setBigDecimal(++count, price);
			rows = insertBooked.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting booked record!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update a booked record's price.
	public int updateBookedPrice(int bid, BigDecimal price, boolean update) {
		int rows = 0;
		try {
			int count = 0;
			updateBookedPriceByBIN.setBigDecimal(++count, price);
			updateBookedPriceByBIN.setBoolean(++count, update);
			updateBookedPriceByBIN.setInt(++count, bid);
			rows = updateBookedPriceByBIN.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when updating booked record!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Update a booked record's status (updated) by SIN.
	public int updateBookedStatusBySIN(String sin) {
		int rows = 0;
		try {
			int count = 0;
			updateBookedUpdatedBySIN.setString(++count, sin);
			rows = updateBookedUpdatedBySIN.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when updating booked record!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Cancel a booked record.
	public int cancelBooked(String street, int number, String postalCode, String country, LocalDate from,
			LocalDate to, boolean update) {
		int rows = 0;
		try {
			int count = 0;
			cancelBooked.setBoolean(++count, update);
			cancelBooked.setString(++count, street);
			cancelBooked.setInt(++count, number);
			cancelBooked.setString(++count, postalCode);
			cancelBooked.setString(++count, country);
			cancelBooked.setObject(++count, from);
			cancelBooked.setObject(++count, to);
			rows = cancelBooked.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when cancelling booked record!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Cancel a booked record by listing.
	public int cancelBookedByListing(String street, int number, String postalCode, String country, boolean update) {
		int rows = 0;
		try {
			int count = 0;
			cancelBookedByListing.setBoolean(++count, update);
			cancelBookedByListing.setString(++count, street);
			cancelBookedByListing.setInt(++count, number);
			cancelBookedByListing.setString(++count, postalCode);
			cancelBookedByListing.setString(++count, country);
			rows = cancelBookedByListing.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when cancelling booked record by listing!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Cancel a booked record by host SIN.
	public int cancelBookedByHostedListing(String sin) {
		int rows = 0;
		try {
			int count = 0;
			cancelBookedByHostedListing.setString(++count, sin);
			rows = cancelBookedByHostedListing.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when cancelling booked record by host SIN!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select booked records by SIN.
	public ArrayList<Booked> selectBookedBySIN(String sin) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectAllBookedBySIN.setString(1, sin);
			ResultSet rs = selectAllBookedBySIN.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting booked record by SIN!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select booked records by SIN and canceled.
	public ArrayList<Booked> selectBookedBySIN(String sin, boolean canceled) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectBookedBySIN.setString(1, sin);
			selectBookedBySIN.setBoolean(2, canceled);
			ResultSet rs = selectBookedBySIN.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting active booked records by SIN!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select all booked records by host listings.
	public ArrayList<Booked> selectBookedByHostListings(String sin) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectAllBookedByHostListings.setString(1, sin);
			ResultSet rs = selectAllBookedByHostListings.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting active booked records by host listings!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select booked records by host listings and canceled.
	public ArrayList<Booked> selectBookedByHostListings(String sin, boolean canceled) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectBookedByHostListings.setString(1, sin);
			selectBookedByHostListings.setBoolean(2, canceled);
			ResultSet rs = selectBookedByHostListings.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting canceled booked records by host listings!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select booked records by address.
	public ArrayList<Booked> selectBookedByAddress(String street, int number, String postalCode, String country) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			int count = 0;
			selectBookedByAddress.setString(++count, street);
			selectBookedByAddress.setInt(++count, number);
			selectBookedByAddress.setString(++count, postalCode);
			selectBookedByAddress.setString(++count, country);
			ResultSet rs = selectBookedByAddress.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting canceled booked records by address!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select booked records by address and date.
	public ArrayList<Booked> selectBookedByAddressDate(String street, int number, String postalCode, String country,
			LocalDate from, LocalDate to, boolean canceled) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			int count = 0;
			selectBookedByAddressDate.setString(++count, street);
			selectBookedByAddressDate.setInt(++count, number);
			selectBookedByAddressDate.setString(++count, postalCode);
			selectBookedByAddressDate.setString(++count, country);
			selectBookedByAddressDate.setObject(++count, from);
			selectBookedByAddressDate.setObject(++count, to);
			selectBookedByAddressDate.setObject(++count, from);
			selectBookedByAddressDate.setObject(++count, to);
			selectBookedByAddressDate.setBoolean(++count, canceled);
			ResultSet rs = selectBookedByAddressDate.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting canceled booked records by address and date!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a select query.
	// Functionality: Select updated booked records by SIN.
	public ArrayList<Booked> selectUpdatedBookedBySIN(String sin) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectBookedUpdatedBySIN.setString(1, sin);
			ResultSet rs = selectBookedUpdatedBySIN.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
				temp.bid = rs.getInt("BID");
				temp.sin = rs.getString("SIN");
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				temp.fromDate = rs.getObject("FromDate", LocalDate.class);
				temp.toDate = rs.getObject("ToDate", LocalDate.class);
				temp.paymentMethod = rs.getString("PaymentMethod");
				temp.price = rs.getBigDecimal("Price");
				temp.canceled = rs.getBoolean("Canceled");
				booked.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting updated booked records by SIN!");
			e.printStackTrace();
		}
		return booked;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of bookings in a date range by city.
	public ArrayList<String> reportNumBookingsCity(LocalDate from, LocalDate to) {
		ArrayList<String> result = new ArrayList<>();
		try {
			int count = 0;
			reportNumBookingsCity.setObject(++count, from);
			reportNumBookingsCity.setObject(++count, to);
			reportNumBookingsCity.setObject(++count, from);
			reportNumBookingsCity.setObject(++count, to);
			ResultSet rs = reportNumBookingsCity.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("City"));
				result.add(rs.getString("TotalBooking"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of bookings by city!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of bookings in a date range by postal
	// code.
	public ArrayList<String> reportNumBookingsPostalCode(LocalDate from, LocalDate to) {
		ArrayList<String> result = new ArrayList<>();
		try {
			int count = 0;
			reportNumBookingsPostalCode.setObject(++count, from);
			reportNumBookingsPostalCode.setObject(++count, to);
			reportNumBookingsPostalCode.setObject(++count, from);
			reportNumBookingsPostalCode.setObject(++count, to);
			ResultSet rs = reportNumBookingsPostalCode.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("PostalCode"));
				result.add(rs.getString("TotalBooking"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of bookings by postal code!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of bookings in a date range by renter.
	public ArrayList<String> reportRenterBooking(LocalDate from, LocalDate to) {
		ArrayList<String> result = new ArrayList<>();
		try {
			int count = 0;
			reportRenterBooking.setObject(++count, from);
			reportRenterBooking.setObject(++count, to);
			reportRenterBooking.setObject(++count, from);
			reportRenterBooking.setObject(++count, to);
			ResultSet rs = reportRenterBooking.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("TotalBooking"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of bookings by renter!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of bookings in a date range by renter and
	// city.
	public ArrayList<String> reportRenterBookingCity(LocalDate from, LocalDate to) {
		ArrayList<String> result = new ArrayList<>();
		try {
			int count = 0;
			reportRenterBookingCity.setObject(++count, from);
			reportRenterBookingCity.setObject(++count, to);
			reportRenterBookingCity.setObject(++count, from);
			reportRenterBookingCity.setObject(++count, to);
			ResultSet rs = reportRenterBookingCity.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("City"));
				result.add(rs.getString("TotalBooking"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of bookings by renter and city!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of listings by country.
	public ArrayList<String> reportNumListingsCount() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportNumListingsCount.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Country"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of listings by country!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of listings by country and city.
	public ArrayList<String> reportNumListingsCountCity() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportNumListingsCountCity.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Country"));
				result.add(rs.getString("City"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting total number of listings by country and city!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the total number of listings by country, city and postal
	// code.
	public ArrayList<String> reportNumListingsCountCityPost() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportNumListingsCountCityPost.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Country"));
				result.add(rs.getString("City"));
				result.add(rs.getString("PostalCode"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println(
					"Exception triggered when getting total number of listings by country, city and postal code!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the hosts ranked by total number of listing per country.
	public ArrayList<String> reportRankHostCount() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportRankHostCount.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("Country"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting hosts by total number of listing per country!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the hosts ranked by total number of listing per country
	// and city.
	public ArrayList<String> reportRankHostCountCity() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportRankHostCountCity.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("Country"));
				result.add(rs.getString("City"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err
					.println("Exception triggered when getting hosts by total number of listing per country and city!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a cancellation.
	public int insertCancellation(String sinCancel, ArrayList<Booked> bookings) {
		int rows = 0;
		try {
			for (Booked booking : bookings) {
				int count = 0;
				insertCancellation.setInt(++count, booking.bid);
				insertCancellation.setString(++count, sinCancel);
				rows += insertCancellation.executeUpdate();
			}
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting cancellation record.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a report query.
	// Functionality: Get the users with the largest total number of cancellations
	// within a year.
	public ArrayList<String> reportNumCancelled(int year) {
		ArrayList<String> result = new ArrayList<>();
		try {
			String date = year + "-__-__";
			int count = 0;
			try {
				// create LargestCancellation view
				createLargestCancelView.setString(++count, date);
				createLargestCancelView.setString(++count, date);
				createLargestCancelView.executeUpdate();
			} catch (SQLException e) {
				System.err.println("Exception triggered when creating or updating LargestCancellation view!");
				e.printStackTrace();
				return null;
			}
			count = 0;
			reportNumCancelled.setString(++count, date);
			reportNumCancelled.setString(++count, date);
			ResultSet rs = reportNumCancelled.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("TotalCancelled"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println(
					"Exception triggered when getting users with the largest total number of cancellations within a year!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a report query.
	// Functionality: Get the hosts that have number of listings that is more than
	// 10% of the total number of listings in each city and country
	public ArrayList<String> reportHost10Percent() {
		ArrayList<String> result = new ArrayList<>();
		try {
			ResultSet rs = reportHost10Percent.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
				result.add(rs.getString("City"));
				result.add(rs.getString("Country"));
				result.add(rs.getString("NumberListing"));
				result.add(rs.getString("TotalListing"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err
					.println(
							"Exception triggered when getting hosts with more than 10% of total listings in each country and city!");
			e.printStackTrace();
		}
		return result;
	}

	// Controls the execution of a select query.
	// Functionality: Get the minimum and maximum prices for a type of listing.
	public ArrayList<Double> getPriceRange(String type) {
		ArrayList<Double> result = new ArrayList<>();
		try {
			getMinMaxPrice.setObject(1, type);
			ResultSet rs = getMinMaxPrice.executeQuery();

			while (rs.next()) {
				result.add(rs.getDouble("MIN(Price)"));
				result.add(rs.getDouble("MAX(Price)"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err
					.println("Exception triggered when getting the minimum and maximum prices for a type of listing!");
			e.printStackTrace();
		}
		if (result.size() == 0) {
			if (type.equalsIgnoreCase("apartment")) {
				result.add(300.0);
				result.add(800.0);
			} else if (type.equalsIgnoreCase("house")) {
				result.add(400.0);
				result.add(1000.0);
			} else {
				result.add(85.0);
				result.add(200.0);
			}
		}

		return result;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a comment.
	private int insertComment(String sin, int rating, String text) {
		int cid = -1;
		try {
			int count = 0;
			insertComment.setString(++count, sin);
			if (rating == 0) {
				insertComment.setNull(++count, java.sql.Types.TINYINT);
			} else {
				insertComment.setInt(++count, rating);
			}
			insertComment.setString(++count, text);
			insertComment.executeUpdate();
			ResultSet rs = insertComment.getGeneratedKeys();
			if (rs.next()) {
				cid = rs.getInt(1);
			}
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting comment.");
			e.printStackTrace();
		}
		return cid;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a comment on a listing.
	public int insertCommentOnListing(String sin, int rating, String text, String street, int number,
			String postalCode, String country) {
		int rows = 0;
		try {
			int cid = insertComment(sin, rating, text);
			int count = 0;
			insertCommentOnListing.setInt(++count, cid);
			insertCommentOnListing.setString(++count, street);
			insertCommentOnListing.setInt(++count, number);
			insertCommentOnListing.setString(++count, postalCode);
			insertCommentOnListing.setString(++count, country);
			rows += insertCommentOnListing.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting comment on listing.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a comment on a user.
	public int insertCommentOnUser(String sin, int rating, String text, String sinComment) {
		int rows = 0;
		try {
			int cid = insertComment(sin, rating, text);
			int count = 0;
			insertCommentOnUser.setInt(++count, cid);
			insertCommentOnUser.setString(++count, sinComment);
			rows += insertCommentOnUser.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting comment on user.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select the comments on a listing.
	public ArrayList<Comment> selectCommentByListing(String street, int number, String postalCode, String country) {
		ArrayList<Comment> comments = new ArrayList<>();
		try {
			int count = 0;
			selectCommentByListing.setString(++count, street);
			selectCommentByListing.setInt(++count, number);
			selectCommentByListing.setString(++count, postalCode);
			selectCommentByListing.setString(++count, country);
			ResultSet rs = selectCommentByListing.executeQuery();

			while (rs.next()) {
				Comment temp = new Comment();
				temp.sin = rs.getString("SIN");
				temp.rating = rs.getInt("Rating");
				temp.text = rs.getString("Text");
				temp.date = rs.getObject("Date", LocalDateTime.class);
				User tempUser = new User();
				tempUser.sin = rs.getString("SIN");
				tempUser.name = rs.getString("Name");
				temp.commentedByUser = tempUser;
				Listing tempListing = new Listing();
				tempListing.street = rs.getString("Street");
				tempListing.number = rs.getInt("Number");
				tempListing.postalCode = rs.getString("PostalCode");
				tempListing.country = rs.getString("Country");
				temp.commentedOnListing = tempListing;
				comments.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting comments on listing!");
			e.printStackTrace();
		}
		return comments;
	}

	// Controls the execution of a select query.
	// Functionality: Select the comments on a user.
	public ArrayList<Comment> selectCommentByUser(String sin) {
		ArrayList<Comment> comments = new ArrayList<>();
		try {
			int count = 0;
			selectCommentByUser.setString(++count, sin);
			ResultSet rs = selectCommentByUser.executeQuery();

			while (rs.next()) {
				Comment temp = new Comment();
				temp.sin = rs.getString("SIN");
				temp.rating = rs.getInt("Rating");
				temp.text = rs.getString("Text");
				temp.date = rs.getObject("Date", LocalDateTime.class);
				User tempUser = new User();
				tempUser.sin = rs.getString("SIN");
				tempUser.name = rs.getString("Name");
				temp.commentedByUser = tempUser;
				User tempUser2 = new User();
				tempUser2.sin = rs.getString("SINComment");
				temp.commentedOnUser = tempUser2;
				comments.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting comments on user!");
			e.printStackTrace();
		}
		return comments;
	}

	// Controls the execution of a select query.
	// Functionality: Select the comments made by a user.
	public ArrayList<Comment> selectCommentMadeByUser(String sin) {
		ArrayList<Comment> comments = new ArrayList<>();
		try {
			int count = 0;
			selectCommentMadeByUser.setString(++count, sin);
			ResultSet rs = selectCommentMadeByUser.executeQuery();

			while (rs.next()) {
				Comment temp = new Comment();
				temp.sin = rs.getString("SIN");
				temp.rating = rs.getInt("Rating");
				temp.text = rs.getString("Text");
				temp.date = rs.getObject("Date", LocalDateTime.class);
				User tempUser = new User();
				tempUser.sin = rs.getString("SINComment");
				temp.commentedOnUser = tempUser;
				Listing tempListing = new Listing();
				tempListing.street = rs.getString("Street");
				tempListing.number = rs.getInt("Number");
				tempListing.postalCode = rs.getString("PostalCode");
				tempListing.country = rs.getString("Country");
				temp.commentedOnListing = tempListing;
				comments.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting comments made by user!");
			e.printStackTrace();
		}
		return comments;
	}

	public int deleteUser(String sin) {
		int rows = 0;
		try {
			ArrayList<Booked> booked = selectBookedBySIN(sin, false);
			for (Booked booking : booked) {
				rows += updateAvailability(booking.street, booking.number, booking.postalCode, booking.country,
						booking.fromDate, booking.toDate, true);
			}
			rows += cancelBookedByHostedListing(sin);
			int count = 0;
			deleteListingHostedBySIN.setString(++count, sin);
			rows += deleteListingHostedBySIN.executeUpdate();
			count = 0;
			deleteUser.setString(++count, sin);
			rows += deleteUser.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when deleting user.");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of a select query.
	// Functionality: Select the listings that have comments.
	public ArrayList<Listing> getAllListingsWComments() {
		ArrayList<Listing> listings = new ArrayList<>();
		try {
			ResultSet rs = selectAllListingWComments.executeQuery();

			while (rs.next()) {
				Listing temp = new Listing();
				temp.street = rs.getString("Street");
				temp.number = rs.getInt("Number");
				temp.postalCode = rs.getString("PostalCode");
				temp.country = rs.getString("Country");
				listings.add(temp);
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listings with comments!");
			e.printStackTrace();
		}
		return listings;
	}
}
