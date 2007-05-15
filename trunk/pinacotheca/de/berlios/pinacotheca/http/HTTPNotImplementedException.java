package de.berlios.pinacotheca.http;

public class HTTPNotImplementedException extends HTTPException {

	/**
	 * @uml.property name="hTTPException"
	 * @uml.associationEnd inverse="hTTPNotImplementedException:de.berlios.pinacotheca.http.HTTPException"
	 */
	private HTTPException exception;

	/**
	 * Getter of the property <tt>hTTPException</tt>
	 * 
	 * @return Returns the exception.
	 * @uml.property name="hTTPException"
	 */
	public HTTPException getHTTPException() {
		return exception;
	}

	/**
	 * Setter of the property <tt>hTTPException</tt>
	 * 
	 * @param hTTPException
	 *            The exception to set.
	 * @uml.property name="hTTPException"
	 */
	public void setHTTPException(HTTPException exception) {
		this.exception = exception;
	}

	/**
	 * @uml.property name="response" readOnly="true"
	 */
	private HTTPResponse response = null;

	/**
	 * Getter of the property <tt>response</tt>
	 * 
	 * @return Returns the response.
	 * @uml.property name="response"
	 */
	public HTTPResponse getResponse() {
		return response;
	}

}
