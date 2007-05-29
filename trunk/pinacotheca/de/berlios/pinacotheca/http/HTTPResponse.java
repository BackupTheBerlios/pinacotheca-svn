package de.berlios.pinacotheca.http;

public class HTTPResponse extends HTTPMessage {

	private Short responseCode = null;
	private String responseMessage = null;
	
	public HTTPResponse(String version, Short responseCode, String responseMessage) {
		setHttpVersion(version);
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}

	public Short getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

	public void setResponseCode(Short responseCode) {
		this.responseCode = responseCode;
	}

}
