package de.berlios.pinacotheca.http;

import java.io.InputStream;
import java.util.HashMap;

public abstract class HTTPMessage {

	public String getHeaderField(String fieldName) throws HTTPException {

		return "";
	}

	public void setHeaderField(String fieldName, String fieldValue)
			throws HTTPException {
	}

	/**
	 * @uml.property name="headerFields"
	 */
	private HashMap<String, String> headerFields;

	/**
	 * @uml.property name="httpVersion"
	 */
	private String httpVersion = "null";

	/**
	 * Getter of the property <tt>httpVersion</tt>
	 * 
	 * @return Returns the httpVersion.
	 * @uml.property name="httpVersion"
	 */
	public String getHttpVersion() {
		return httpVersion;
	}

	/**
	 * Setter of the property <tt>httpVersion</tt>
	 * 
	 * @param httpVersion
	 *            The httpVersion to set.
	 * @uml.property name="httpVersion"
	 */
	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	/**
	 * @uml.property name="payloadStream"
	 */
	private InputStream payloadStream = null;

	/**
	 * Getter of the property <tt>payloadStream</tt>
	 * 
	 * @return Returns the payloadStream.
	 * @uml.property name="payloadStream"
	 */
	public InputStream getPayloadStream() {
		return payloadStream;
	}

	/**
	 * Setter of the property <tt>payloadStream</tt>
	 * 
	 * @param payloadStream
	 *            The payloadStream to set.
	 * @uml.property name="payloadStream"
	 */
	public void setPayloadStream(InputStream payloadStream) {
		this.payloadStream = payloadStream;
	}
}
