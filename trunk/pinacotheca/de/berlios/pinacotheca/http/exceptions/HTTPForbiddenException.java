package de.berlios.pinacotheca.http.exceptions;


public class HTTPForbiddenException extends HTTPException {

	private static final long serialVersionUID = -4331664616048305019L;
	
	public HTTPForbiddenException() {
		super((short) 403, "Forbidden");
	}
}
