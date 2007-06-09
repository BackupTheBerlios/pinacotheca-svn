package de.berlios.pinacotheca.http.exceptions;


public class HTTPVersionNotSupportedException extends HTTPException {

	private static final long serialVersionUID = 2088775625960972274L;

	public HTTPVersionNotSupportedException() {
		super((short) 505, "HTTP Version Not Supported");
	}
}
