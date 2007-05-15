package de.berlios.pinacotheca.http;

public class HTTPBadRequestException extends HTTPException {

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
