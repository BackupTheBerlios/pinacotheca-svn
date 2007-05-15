package de.berlios.pinacotheca.http;


public class HTTPRequest extends HTTPMessage {

	/** 
	 * @uml.property name="requestURL"
	 */
	private String requestURL = "null";

	/** 
	 * Getter of the property <tt>requestURL</tt>
	 * @return  Returns the requestURL.
	 * @uml.property  name="requestURL"
	 */
	public String getRequestURL() {
		return requestURL;
	}

	/** 
	 * Setter of the property <tt>requestURL</tt>
	 * @param requestURL  The requestURL to set.
	 * @uml.property  name="requestURL"
	 */
	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	/** 
	 * @uml.property name="requestType"
	 */
	private String requestType = "null";

	/** 
	 * Getter of the property <tt>requestType</tt>
	 * @return  Returns the requestType.
	 * @uml.property  name="requestType"
	 */
	public String getRequestType() {
		return requestType;
	}

	/** 
	 * Setter of the property <tt>requestType</tt>
	 * @param requestType  The requestType to set.
	 * @uml.property  name="requestType"
	 */
	public void setRequestType(String requestType) {
		this.requestType = requestType;
	}
}
