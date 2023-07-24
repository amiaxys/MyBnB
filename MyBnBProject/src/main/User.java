package main;

import java.time.LocalDate;

public class User {
	protected String sin;
	protected String password;
	protected byte[] salt;
	protected String name;
	protected String address;
	protected LocalDate birthdate;
	protected String occupation;
	
	public User() {
		sin = null;
		password = null;
		salt = null;
		name = null;
		address = null;
		birthdate = null;
		occupation = null;
	}
}
