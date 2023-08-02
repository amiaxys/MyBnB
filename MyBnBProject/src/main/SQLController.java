package main;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

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
	// Listing statements
	private PreparedStatement insertListing = null;
	private PreparedStatement selectAllListing = null;
	private PreparedStatement selectListingAddr = null;
	private PreparedStatement selectListingPostalCode = null;
	private PreparedStatement deleteListing = null;
	// Hosts statements
	private PreparedStatement insertHosts = null;
	private PreparedStatement selectHostsBySIN = null;
	// Availability statements
	private PreparedStatement insertAvailability = null;
	private PreparedStatement insertOrUpdateAvailability = null;
	private PreparedStatement updateAvailability = null;
	// private PreparedStatement selectAllAvailBetweenDate = null;
	private PreparedStatement selectAvailBetweenDate = null;
	private PreparedStatement deleteAvailBetweenDate = null;
	// Booked statements
	private PreparedStatement insertBooked = null;
	private PreparedStatement cancelBooked = null;
	private PreparedStatement cancelBookedByListing = null;
	private PreparedStatement selectAllBookedBySIN = null;
	private PreparedStatement selectBookedBySIN = null;
	private PreparedStatement selectAllBookedByHostListings = null;
	private PreparedStatement selectBookedByHostListings = null;
  private PreparedStatement selectBookedID = null;
  private PreparedStatement selectBookedByAddress = null;
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
			success = createTables();
			success = prepareStatements();

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
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN),"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country) ON DELETE CASCADE"
					+ ")");
			// @formatter:on
			createHostsTb.executeUpdate();
			createHostsTb.close();
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
					+ " Canceled BOOL NOT NULL DEFAULT 0,"
					+ " PRIMARY KEY (BID),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN),"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country) ON DELETE SET NULL"
					+ ")");
			// @formatter:on
			createBookedTb.executeUpdate();
			createBookedTb.close();
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
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN)"
					+ ")");
			// @formatter:on
			createCancellationTb.executeUpdate();
			createCancellationTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("Booked table already exists!");
			// e.printStackTrace();
		}
		return success;
	}

	private boolean prepareStatements() {
		boolean success = true;
		try {
			// @formatter:off
			// User statements
			insertUser = conn.prepareStatement("INSERT INTO User"
					+ " (SIN, Password, Salt, Name, Address, Birthdate, Occupation)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			selectUserBySIN = conn.prepareStatement("SELECT * FROM User WHERE SIN=?");
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
			deleteListing = conn.prepareStatement("DELETE FROM Listing WHERE Street=? AND Number=? AND PostalCode=? AND Country=?");
			// Hosts statements
			insertHosts = conn.prepareStatement("INSERT INTO Hosts"
					+ " (SIN, Street, Number, PostalCode, Country)" 
					+ " VALUES (?, ?, ?, ?, ?)");
			selectHostsBySIN = conn.prepareStatement("SELECT * FROM Hosts WHERE SIN=?");
			// Availability statements
			insertAvailability = conn.prepareStatement("INSERT INTO Availability"
					+ " (Street, Number, PostalCode, Country, Date, Available, Price)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			insertOrUpdateAvailability = conn.prepareStatement("INSERT INTO Availability"
					+ " (Street, Number, PostalCode, Country, Date, Available, Price)"
					+ " VALUES (?, ?, ?, ?, ?, ?, ?) ON DUPLICATE KEY UPDATE Available=?, Price=?");
			updateAvailability = conn.prepareStatement("UPDATE Availability SET Available=? WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND Date=?");
			selectAvailBetweenDate = conn.prepareStatement("SELECT * FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			deleteAvailBetweenDate = conn.prepareStatement("DELETE FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			// Booked statements
			insertBooked = conn.prepareStatement("INSERT INTO Booked"
					+ " (SIN, Street, Number, PostalCode, Country, FromDate, ToDate, PaymentMethod)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
			cancelBooked = conn.prepareStatement("UPDATE Booked SET Canceled=1 WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " FromDate=? AND ToDate=?");
			cancelBookedByListing = conn.prepareStatement("UPDATE Booked SET Canceled=1 WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=?");
			selectAllBookedBySIN = conn.prepareStatement("SELECT * FROM Booked WHERE SIN=?");
			selectBookedBySIN = conn.prepareStatement("SELECT * FROM Booked WHERE SIN=? AND Canceled=?");
			selectAllBookedByHostListings = conn.prepareStatement("SELECT A.SIN, A.Street, A.Number, A.PostalCode,"
          + " A.Country, A.FromDate, A.ToDate, A.PaymentMethod, A.Canceled FROM Booked AS A NATURAL JOIN"
					+ " Listing INNER JOIN Hosts AS B ON A.Street=B.Street AND A.Number=B.Number AND"
          + " A.PostalCode=B.PostalCode AND A.Country=B.Country WHERE B.SIN=?");
			selectBookedByHostListings = conn.prepareStatement("SELECT A.SIN, A.Street, A.Number, A.PostalCode,"
          + " A.Country, A.FromDate, A.ToDate, A.PaymentMethod, A.Canceled FROM Booked AS A NATURAL JOIN"
					+ " Listing INNER JOIN Hosts AS B ON A.Street=B.Street AND A.Number=B.Number AND"
          + " A.PostalCode=B.PostalCode AND A.Country=B.Country WHERE B.SIN=? AND A.Canceled=?");
      selectBookedID = conn.prepareStatement("SELECT BID FROM BOOKED WHERE SIN=? AND Street=? AND"
          + " Number=? AND PostalCode=? AND Country=? AND FromDate=? AND ToDate=?");
      selectBookedByAddress = conn.prepareStatement("SELECT * FROM BOOKED WHERE Street=? AND"
          + " Number=? AND PostalCode=? AND Country=?");
      // Cancellation statements
      insertCancellation = conn.prepareStatement("INSERT INTO Cancellation (BID, SIN) VALUES (?, ?) ON DUPLICATE KEY"
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
          + " AND Canceled=0 (ToDate BETWEEN ? AND ?) GROUP BY Name, City ORDER BY TotalBooking DESC");
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
          + " ? GROUP BY Name ORDER BY TotalCancelled DESC");
			// @formatter:on
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
	// Functionality: Update an availability.
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
	// public ArrayList<Availability> selectAvailBetweenDate(String street, int
	// number, String postalCode, String country,
	// LocalDate to, LocalDate from) {
	// ArrayList<Availability> availabilities = new ArrayList<>();
	// try {
	// int count = 0;
	// selectAllAvailBetweenDate.setString(++count, street);
	// selectAllAvailBetweenDate.setInt(++count, number);
	// selectAllAvailBetweenDate.setString(++count, postalCode);
	// selectAllAvailBetweenDate.setString(++count, country);
	// selectAllAvailBetweenDate.setObject(++count, to);
	// selectAllAvailBetweenDate.setObject(++count, from);

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

	// rs.close();
	// } catch (SQLException e) {
	// System.err.println("Exception triggered when selecting availabilities by
	// date!");
	// e.printStackTrace();
	// }
	// return availabilities;
	// }

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
			LocalDate to, String paymentMethod) {
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
			rows = insertBooked.executeUpdate();
		} catch (SQLException e) {
			System.err.println("Exception triggered when inserting booked record!");
			e.printStackTrace();
		}
		return rows;
	}

	// Controls the execution of an update query.
	// Functionality: Cancel a booked record.
	public int cancelBooked(String street, int number, String postalCode, String country, LocalDate from,
			LocalDate to) {
		int rows = 0;
		try {
			int count = 0;
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
	public int cancelBookedByListing(String street, int number, String postalCode, String country) {
		int rows = 0;
		try {
			int count = 0;
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

	// Controls the execution of a select query.
	// Functionality: Select booked records by SIN.
	public ArrayList<Booked> selectBookedBySIN(String sin) {
		ArrayList<Booked> booked = new ArrayList<>();
		try {
			selectAllBookedBySIN.setString(1, sin);
			ResultSet rs = selectAllBookedBySIN.executeQuery();

			while (rs.next()) {
				Booked temp = new Booked();
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

	// Controls the execution of a report query.
	// Functionality: Get the total number of bookings in a date range by city.
	public ArrayList<Object> reportNumBookingsCity(LocalDate from, LocalDate to) {
		ArrayList<Object> result = new ArrayList<>();
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
	public ArrayList<Object> reportNumBookingsPostalCode(LocalDate from, LocalDate to) {
		ArrayList<Object> result = new ArrayList<>();
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
	public ArrayList<Object> reportRenterBooking(LocalDate from, LocalDate to) {
		ArrayList<Object> result = new ArrayList<>();
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
	public ArrayList<Object> reportRenterBookingCity(LocalDate from, LocalDate to) {
		ArrayList<Object> result = new ArrayList<>();
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
	public ArrayList<Object> reportNumListingsCount() {
		ArrayList<Object> result = new ArrayList<>();
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
	public ArrayList<Object> reportNumListingsCountCity() {
		ArrayList<Object> result = new ArrayList<>();
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
	// Functionality: Get the total number of listings by country, city and postal code.
	public ArrayList<Object> reportNumListingsCountCityPost() {
		ArrayList<Object> result = new ArrayList<>();
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
			System.err.println("Exception triggered when getting total number of listings by country, city and postal code!");
			e.printStackTrace();
		}
		return result;
	}

  // Controls the execution of a report query.
	// Functionality: Get the hosts ranked by total number of listing per country.
	public ArrayList<Object> reportRankHostCount() {
		ArrayList<Object> result = new ArrayList<>();
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
	// Functionality: Get the hosts ranked by total number of listing per country and city.
  public ArrayList<Object> reportRankHostCountCity() {
		ArrayList<Object> result = new ArrayList<>();
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
			System.err.println("Exception triggered when getting hosts by total number of listing per country and city!");
			e.printStackTrace();
		}
		return result;
	}

  // Controls the execution of an insert query.
	// Functionality: Insert a cancellation.
	public int insertCancellation(String sinCancel, ArrayList<Booked> bookings) {
		int rows = 0;
		try {
      for (Booked booking: bookings) {
        int count = 0;
        selectBookedID.setString(++count, booking.sin);
        selectBookedID.setString(++count, booking.street);
        selectBookedID.setInt(++count, booking.number);
        selectBookedID.setString(++count, booking.postalCode);
        selectBookedID.setString(++count, booking.country);
        selectBookedID.setObject(++count, booking.fromDate);
        selectBookedID.setObject(++count, booking.toDate);
        ResultSet rs = selectBookedID.executeQuery();
        rs.next();
        int bid = rs.getInt("BID");
        rs.close();

        count = 0;
		  	insertCancellation.setInt(++count, bid);
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
	// Functionality: Get the users ranked by total number of cancellations in a year.
  public ArrayList<Object> reportNumCancelled(int year) {
		ArrayList<Object> result = new ArrayList<>();
		try {
      int count = 0;
			reportNumCancelled.setString(++count, year+"-__-__");
			reportNumCancelled.setString(++count, year+"-__-__");
			ResultSet rs = reportNumCancelled.executeQuery();

			while (rs.next()) {
				result.add(rs.getString("Name"));
        result.add(rs.getString("TotalCancelled"));
			}
			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when getting users by total number of cancellations in a year!");
			e.printStackTrace();
		}
		return result;
	}
}
