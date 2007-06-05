package de.berlios.pinacotheca.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

import de.berlios.pinacotheca.http.exceptions.HTTPException;

public abstract class HTTPMessage {
	private HashMap<String, HTTPMessageHeaderValue> headerFields = new HashMap<String, HTTPMessageHeaderValue>();

	private String httpVersion = null;

	private InputStream payloadStream = null;

	public boolean containsHeaderField(String fieldName) {
		return headerFields.containsKey(fieldName);
	}

	public HTTPMessageHeaderValue getHeaderField(String fieldName) {
		return headerFields.get(fieldName);
	}

	public void setHeaderField(String fieldName, HTTPMessageHeaderValue fieldValue) throws HTTPException {
		headerFields.put(fieldName, fieldValue);
	}

	public Set<String> getHeaderFieldNames() {
		return headerFields.keySet();
	}

	public String getHttpVersion() {
		return httpVersion;
	}

	public void setHttpVersion(String httpVersion) {
		this.httpVersion = httpVersion;
	}

	public InputStream getPayloadStream() {
		return payloadStream;
	}

	public void setPayloadStream(InputStream payloadStream) {
		this.payloadStream = payloadStream;
	}
}
