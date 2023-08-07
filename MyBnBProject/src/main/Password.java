package main;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

// Password helper class
public class Password {
	// Code from:
	// https://www.javaguides.net/2020/02/java-sha-256-hash-with-salt-example.html
	public byte[] getSalt() {
		SecureRandom random = new SecureRandom();
		byte[] salt = new byte[16];
		random.nextBytes(salt);
		return salt;
	}

	public String getSaltHashedPassword(String password, byte[] salt) {
		String shPassword = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(salt);
			byte[] bytes = md.digest(password.getBytes());
			StringBuilder sb = new StringBuilder();
			for (int i = 0; i < bytes.length; i++) {
				sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
			}
			shPassword = sb.toString();
		} catch (NoSuchAlgorithmException e) {
			System.out.println("Error salt and hashing password!");
			e.printStackTrace();
		}
		return shPassword;
	}
}
