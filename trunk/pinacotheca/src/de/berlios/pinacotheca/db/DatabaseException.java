package de.berlios.pinacotheca.db;

public class DatabaseException extends Exception {

	private static final long serialVersionUID = -5810169103058809361L;

	public DatabaseException(Exception cause) {
		super(cause);
	}
}
