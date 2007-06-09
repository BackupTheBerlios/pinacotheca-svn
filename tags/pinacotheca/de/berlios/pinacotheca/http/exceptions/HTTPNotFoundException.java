package de.berlios.pinacotheca.http.exceptions;


public class HTTPNotFoundException extends HTTPException {

	private static final long serialVersionUID = 366557705791869096L;
	
	public HTTPNotFoundException(String location) {
		super((short) 404, "Not Found");
		addAdditionalInfo("Could not find <strong>" + location + "</strong>.");
	}
}
