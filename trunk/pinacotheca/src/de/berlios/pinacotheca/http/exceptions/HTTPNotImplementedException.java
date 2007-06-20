package de.berlios.pinacotheca.http.exceptions;


public class HTTPNotImplementedException extends HTTPException {

	private static final long serialVersionUID = 4769279765733482878L;
	
	public HTTPNotImplementedException() {
		super((short) 501, "Not Implemented");
	}
}
