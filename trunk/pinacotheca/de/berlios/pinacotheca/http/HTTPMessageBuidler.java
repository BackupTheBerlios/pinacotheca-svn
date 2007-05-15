
package de.berlios.pinacotheca.http;

public class HTTPMessageBuidler {

	public HTTPMessageBuidler(String messageStart) throws HTTPException {
	}

	public void parseHeader(String header) throws HTTPException {
	}

	/**
	 * @uml.property name="message" readOnly="true"
	 */
	private HTTPMessage message;

	/**
	 * Getter of the property <tt>message</tt>
	 * 
	 * @return Returns the message.
	 * @uml.property name="message"
	 */
	public HTTPMessage getMessage() {
		return message;
	}

}
