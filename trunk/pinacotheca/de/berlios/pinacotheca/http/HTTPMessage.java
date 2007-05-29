package de.berlios.pinacotheca.http;

import java.io.InputStream;
import java.util.HashMap;
import java.util.Set;

public abstract class HTTPMessage {
	private HashMap<String, String> headerFields = new HashMap<String, String>();

	private String httpVersion = null;

	private InputStream payloadStream = null;

	public boolean containsHeaderField(String fieldName) {
		return headerFields.containsKey(fieldName);
	}

	public String getHeaderField(String fieldName) {
		return headerFields.get(fieldName);
	}

	public void setHeaderField(String fieldName, String fieldValue) {
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
