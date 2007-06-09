package de.berlios.pinacotheca.http.exceptions;


public class HTTPServerErrorException extends HTTPException {

	private static final long serialVersionUID = 3308492066631031805L;
	
	public HTTPServerErrorException() {
		super((short) 500, "Internal Server Error");
	}
}
