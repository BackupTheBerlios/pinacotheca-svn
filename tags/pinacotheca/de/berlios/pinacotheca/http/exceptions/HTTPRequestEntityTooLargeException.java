package de.berlios.pinacotheca.http.exceptions;


public class HTTPRequestEntityTooLargeException extends HTTPException {

	private static final long serialVersionUID = -4574547480797418022L;

	public HTTPRequestEntityTooLargeException() {
		super((short) 413, "Request Entity Too Large");
	}

}
