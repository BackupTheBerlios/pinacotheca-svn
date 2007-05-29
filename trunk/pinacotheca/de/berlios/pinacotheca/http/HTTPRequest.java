package de.berlios.pinacotheca.http;

public class HTTPRequest extends HTTPMessage {
	private String requestURL = null;

	private String requestType = null;

	public String getRequestURL() {
		return requestURL;
	}

	public void setRequestURL(String requestURL) {
		this.requestURL = requestURL;
	}

	public String getRequestType() {
		return requestType;
	}

	public void setRequestMethod(String requestType) {
		this.requestType = requestType;
	}
}
