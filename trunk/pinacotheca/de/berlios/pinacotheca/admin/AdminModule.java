package de.berlios.pinacotheca.admin;

import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPException;

public class AdminModule implements PTModule {
	private HTTPRequest request;

	private boolean secureConnection;

	public HTTPResponse getResponse() {
		return null;
	}

	public void handleRequest() throws HTTPException {
	}

	public void setSecureConnection(boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}
}
