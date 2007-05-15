package de.berlios.pinacotheca.http;

public class HTTPResponse extends HTTPMessage {

	/** 
	 * @uml.property name="responseCode"
	 */
	private Short responseCode = null;

	/** 
	 * Getter of the property <tt>responseCode</tt>
	 * @return  Returns the responseCode.
	 * @uml.property  name="responseCode"
	 */
	public Short getResponseCode() {
		return responseCode;
	}

	/** 
	 * @uml.property name="responseMessage"
	 */
	private String responseMessage = "null";

	/** 
	 * Getter of the property <tt>responseMessage</tt>
	 * @return  Returns the responseMessage.
	 * @uml.property  name="responseMessage"
	 */
	public String getResponseMessage() {
		return responseMessage;
	}

	/** 
	 * Setter of the property <tt>responseMessage</tt>
	 * @param responseMessage  The responseMessage to set.
	 * @uml.property  name="responseMessage"
	 */
	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	/** 
	 * Setter of the property <tt>responseCode</tt>
	 * @param responseCode  The responseCode to set.
	 * @uml.property  name="responseCode"
	 */
	public void setResponseCode(Short responseCode) {
		this.responseCode = responseCode;
	}

}
