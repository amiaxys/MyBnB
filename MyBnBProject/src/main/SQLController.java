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
	private PreparedStatement insertUser = null;
	private PreparedStatement selectUserBySIN = null;
	private PreparedStatement insertListing = null;
	private PreparedStatement selectListingAddr = null;
	private PreparedStatement selectAllListing = null;
	private PreparedStatement insertHosts = null;
	private PreparedStatement selectHostsBySIN = null;
	private PreparedStatement insertAvailability = null;
	private PreparedStatement selectAllAvailBetweenDate = null;
	private PreparedStatement selectAvailBetweenDate = null;
	private PreparedStatement deleteAvailBetweenDate = null;
	private PreparedStatement insertBooked = null;

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
					+ " Listing(Street, Number, PostalCode, Country)"
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
					+ " Listing(Street, Number, PostalCode, Country)"
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
					+ " SIN CHAR(9) NOT NULL,"
					+ " Street VARCHAR(40),"
					+ " Number INT,"
					+ " PostalCode VARCHAR(10),"
					+ " Country VARCHAR(56),"
					+ " FromDate DATE,"
					+ " ToDate DATE,"
					+ " PaymentMethod SET('Credit card', 'Debit card', 'PayPal'),"
					+ " PRIMARY KEY (Street, Number, PostalCode, Country, FromDate, ToDate),"
					+ " FOREIGN KEY (SIN) REFERENCES User(SIN),"
					+ " FOREIGN KEY (Street, Number, PostalCode, Country) REFERENCES"
					+ " Listing(Street, Number, PostalCode, Country)"
					+ ")");
			// @formatter:on
			createBookedTb.executeUpdate();
			createBookedTb.close();
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
			selectListingAddr = conn.prepareStatement("SELECT * FROM Listing WHERE Street=? AND"
					+ " Number=? AND PostalCode=? AND Country=? AND City=?");
			selectAllListing = conn.prepareStatement("SELECT * FROM Listing");
			// Hosts statements
			insertHosts = conn.prepareStatement("INSERT INTO Hosts"
					+ " (SIN, Street, Number, PostalCode, Country)" 
					+ " VALUES (?, ?, ?, ?, ?)");
			selectHostsBySIN = conn.prepareStatement("SELECT * FROM Hosts WHERE SIN=?");
			// Availability statements
			insertAvailability = conn.prepareStatement("INSERT INTO Availability"
					+ " (Street, Number, PostalCode, Country, Date, Available, Price)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			selectAllAvailBetweenDate = conn.prepareStatement("SELECT * FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND Date BETWEEN ? AND ?");
			selectAvailBetweenDate = conn.prepareStatement("SELECT * FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			deleteAvailBetweenDate = conn.prepareStatement("DELETE FROM Availability WHERE"
					+ " Street=? AND Number=? AND PostalCode=? AND Country=? AND"
					+ " (Date BETWEEN ? AND ?) AND Available=?");
			// Booked statements
			insertBooked = conn.prepareStatement("INSERT INTO Booked"
					+ " (SIN, Street, Number, PostalCode, Country, From, To, PaymentMethod)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?, ?)");
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

		if (sin == null) {
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
			LocalDate to, LocalDate from, boolean available) {
		ArrayList<Availability> availabilities = new ArrayList<>();
		try {
			int count = 0;
			selectAvailBetweenDate.setString(++count, street);
			selectAvailBetweenDate.setInt(++count, number);
			selectAvailBetweenDate.setString(++count, postalCode);
			selectAvailBetweenDate.setString(++count, country);
			selectAvailBetweenDate.setObject(++count, to);
			selectAvailBetweenDate.setObject(++count, from);
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
	public int deleteAvailBetweenDate(String street, int number, String postalCode, String country, LocalDate to,
			LocalDate from, boolean available) {
		int rows = 0;
		try {
			int count = 0;
			deleteAvailBetweenDate.setString(++count, street);
			deleteAvailBetweenDate.setInt(++count, number);
			deleteAvailBetweenDate.setString(++count, postalCode);
			deleteAvailBetweenDate.setString(++count, country);
			deleteAvailBetweenDate.setObject(++count, to);
			deleteAvailBetweenDate.setObject(++count, from);
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
	public ArrayList<Listing> searchListingAddr(String street, int number, String postalCode, String country,
			String city) {
		ArrayList<Listing> listings = new ArrayList<>();
		try {
			int count = 0;
			selectListingAddr.setString(++count, street);
			selectListingAddr.setInt(++count, number);
			selectListingAddr.setString(++count, postalCode);
			selectListingAddr.setString(++count, country);
			selectListingAddr.setString(++count, city);

			ResultSet rs = selectListingAddr.executeQuery();

			count = 0;
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
				temp.amenities = rs.getString("Amenities");
				listings.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listing by address!");
			e.printStackTrace();
		}
		return listings;
	}

	// Controls the execution of a select query.
	// Functionality: Select all listings.
	public ArrayList<Listing> searchAllListing() {
		ArrayList<Listing> listings = new ArrayList<>();
		try {
			ResultSet rs = selectAllListing.executeQuery();

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
				temp.amenities = rs.getString("Amenities");
				listings.add(temp);
			}

			rs.close();
		} catch (SQLException e) {
			System.err.println("Exception triggered when selecting listing by address!");
			e.printStackTrace();
		}
		return listings;
	}

	/*
	 * // Controls the execution of functionality: "3. Print schema." public
	 * ArrayList<String> getSchema() { ArrayList<String> output = new
	 * ArrayList<String>(); try { DatabaseMetaData meta = conn.getMetaData();
	 * ResultSet schemas = meta.getTables(null, null, "%", null); // ResultSet
	 * catalogs = meta.getCatalogs(); while (schemas.next()) {
	 * output.add(schemas.getString("TABLE_NAME")); } schemas.close(); } catch
	 * (SQLException e) { System.err.println("Retrieval of Schema Info failed!");
	 * e.printStackTrace(); output.clear(); } return output; }
	 * 
	 * // Controls the execution of functionality: "4. Print table schema." public
	 * ArrayList<String> colSchema(String tableName) { ArrayList<String> result =
	 * new ArrayList<String>(); try { DatabaseMetaData meta = conn.getMetaData();
	 * ResultSet rs = meta.getColumns(null, null, tableName, null); while
	 * (rs.next()) { result.add(rs.getString(4)); result.add(rs.getString(6)); }
	 * rs.close(); } catch (SQLException e) {
	 * System.err.println("Retrieval of Table Info failed!"); e.printStackTrace();
	 * result.clear(); } return result; }
	 * 
	 * // Controls the execution of a select query. // Functionality:
	 * "2. Select a record." public void selectOp(String query) { try { ResultSet rs
	 * = st.executeQuery(query); ResultSetMetaData rsmd = rs.getMetaData(); int
	 * colNum = rsmd.getColumnCount(); System.out.println(""); for (int i = 0; i <
	 * colNum; i++) { System.out.print(rsmd.getColumnLabel(i + 1) + "\t"); }
	 * System.out.println(""); while (rs.next()) { for (int i = 0; i < colNum; i++)
	 * { System.out.print(rs.getString(i + 1) + "\t"); } System.out.println(""); }
	 * rs.close(); } catch (SQLException e) {
	 * System.err.println("Exception triggered during Select execution!");
	 * e.printStackTrace(); } System.out.println(); }
	 * 
	 * // Controls the execution of an insert query. // Functionality:
	 * "1. Insert a record." public int insertOp(String query) { int rows = 0; try {
	 * rows = st.executeUpdate(query); } catch (SQLException e) {
	 * System.err.println("Exception triggered during Insert execution!");
	 * e.printStackTrace(); } return rows; }
	 */
}
