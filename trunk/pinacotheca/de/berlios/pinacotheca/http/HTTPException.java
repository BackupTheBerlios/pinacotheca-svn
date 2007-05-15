package de.berlios.pinacotheca.http;

public abstract class HTTPException extends Exception {

	public abstract de.berlios.pinacotheca.http.HTTPResponse getResponse();

	/**
	 * @uml.property  name="hTTPNotImplementedException"
	 * @uml.associationEnd  inverse="hTTPException:de.berlios.pinacotheca.http.HTTPNotImplementedException"
	 */
	private HTTPNotImplementedException notImplementedException;

	/**
	 * Getter of the property <tt>hTTPNotImplementedException</tt>
	 * @return  Returns the notImplementedException.
	 * @uml.property  name="hTTPNotImplementedException"
	 */
	public HTTPNotImplementedException getHTTPNotImplementedException() {
		return notImplementedException;
	}

	/**
	 * Setter of the property <tt>hTTPNotImplementedException</tt>
	 * @param hTTPNotImplementedException  The notImplementedException to set.
	 * @uml.property  name="hTTPNotImplementedException"
	 */
	public void setHTTPNotImplementedException(HTTPNotImplementedException notImplementedException) {
		this.notImplementedException = notImplementedException;
	}

}
