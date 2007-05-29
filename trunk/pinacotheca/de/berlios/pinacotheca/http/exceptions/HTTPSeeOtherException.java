package de.berlios.pinacotheca.http.exceptions;


public class HTTPSeeOtherException extends HTTPException {

	private static final long serialVersionUID = -8893465237270442420L;

	public HTTPSeeOtherException(String location) {
		super((short) 303, "See Other");
		addHeader("Location", location);
		addAdditionalInfo("See <strong>" + location + "</strong>.");
	}
}
