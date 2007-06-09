package de.berlios.pinacotheca.http.exceptions;


public class HTTPBadRequestException extends HTTPException {

	private static final long serialVersionUID = -494252332036118824L;
	
	public HTTPBadRequestException() {
		super((short) 400, "Bad Request");
	}
}