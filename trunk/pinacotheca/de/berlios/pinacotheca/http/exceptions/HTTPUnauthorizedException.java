package de.berlios.pinacotheca.http.exceptions;


public class HTTPUnauthorizedException extends HTTPException {

	private static final long serialVersionUID = 8544916350126842117L;
	
	public HTTPUnauthorizedException() {
		super((short) 401, "Unauthorized");
	}
}
