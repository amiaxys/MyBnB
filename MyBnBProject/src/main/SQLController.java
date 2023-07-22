package main;

import java.sql.*;

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
			PreparedStatement createUserTb = conn
					.prepareStatement("CREATE TABLE User (" 
							+ "	SIN CHAR(9) PRIMARY KEY,"
							+ " Password VARCHAR(250) NOT NULL," 
							+ " Salt BINARY(16) NOT NULL," 
							+ "	Name VARCHAR(250),"
							+ "	Address VARCHAR(250)," 
							+ "	Birthdate CHAR(10)," 
							+ "	Occupation VARCHAR(100)" 
							+ ")");
			// @formatter:on
			createUserTb.executeUpdate();
			createUserTb.close();
		} catch (SQLException e) {
			// success = false;
			System.err.println("Tables already exist!");
			// e.printStackTrace();
		}
		return success;
	}

	private boolean prepareStatements() {
		boolean success = true;
		try {
			// @formatter:off
			insertUser = conn.prepareStatement("INSERT INTO User"
					+ " (SIN, Password, Salt, Name, Address, Birthdate, Occupation)" 
					+ " VALUES (?, ?, ?, ?, ?, ?, ?)");
			// @formatter:on
			selectUserBySIN = conn.prepareStatement("SELECT * FROM User WHERE SIN=?");
		} catch (SQLException e) {
			success = false;
			System.err.println("Prepared statements could not be created!");
			e.printStackTrace();
		}
		return success;
	}

	// Controls the execution of an insert query.
	// Functionality: Insert a user.
	public int insertUser(String sin, String password, byte[] salt, String name, String address, String birthdate,
			String occupation) {
		int rows = 0;
		try {
			insertUser.setString(1, sin);
			insertUser.setString(2, password);
			insertUser.setBytes(3, salt);
			insertUser.setString(4, name);
			insertUser.setString(5, address);
			insertUser.setString(6, birthdate);
			insertUser.setString(7, occupation);
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

			rs.next();
			user.sin = rs.getString("SIN");
			user.password = rs.getString("Password");
			user.salt = rs.getBytes("Salt");
			user.name = rs.getString("Name");
			user.address = rs.getString("Address");
			user.birthdate = rs.getString("Birthdate");
			user.occupation = rs.getString("Occupation");

			rs.close();
		} catch (SQLException e) {
			System.err.println(
					"Exception triggered during selecting user by SIN! This SIN may not be attached to an account.");
			e.printStackTrace();
		}
		return user;
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
