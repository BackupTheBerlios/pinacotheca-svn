package de.berlios.pinacotheca;

import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPException;

public interface PTModule {

	public HTTPResponse getResponse();

	public void handleRequest() throws HTTPException;

	public void setSecureConnection(boolean isSecure);
	
	public void setRequest(HTTPRequest request);
}
