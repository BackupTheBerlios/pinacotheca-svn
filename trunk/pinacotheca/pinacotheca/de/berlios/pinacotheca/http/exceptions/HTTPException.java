package de.berlios.pinacotheca.http.exceptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import de.berlios.pinacotheca.http.HTTPMessageHeaderValue;
import de.berlios.pinacotheca.http.HTTPResponse;

public abstract class HTTPException extends Exception {
	private static final String PAGE_START = "<html>\n<head>\n<title>";
	
	private static final String PAGE_HEADER_END = "</title>\n</head>\n<body>\n<h1>";
	
	private static final String PAGE_HEADING_END = "</h1>\n";

	private static final String PAGE_BETWEEN_START = "<p>";
	
	private static final String PAGE_BETWEEN_END = "</p>\n";
	
	private static final String PAGE_END  = "</body>\n</html>\n";

	private HTTPResponse response;
	
	private String responsePayload = PAGE_START;

	protected HTTPException(Short responseCode, String responseMessage) {
		response = new HTTPResponse("HTTP/1.1", responseCode, responseMessage);
		
		if(responseCode < 200) {
			responsePayload += "Information";
		} else if(responseCode < 300) {
			responsePayload += "Successful";
		} else if(responseCode < 400) {
			responsePayload += "Redirection";
		} else if(responseCode < 500) {
			responsePayload += "Client Error";
		} else {
			responsePayload += "Server Error";
		}
		
		responsePayload += PAGE_HEADER_END;
		responsePayload += responseCode + " " + responseMessage + PAGE_HEADING_END;
	}
	
	protected void addAdditionalInfo(String info) {
		responsePayload += PAGE_BETWEEN_START + info + PAGE_BETWEEN_END;
	}
	
	protected void addHeader(String fieldName, String fieldValue) throws HTTPException {
		response.setHeaderField(fieldName, HTTPMessageHeaderValue.parseHeaderValue(fieldValue));
	}
	
	public HTTPResponse getResponse() {
		byte[] buf = (responsePayload + PAGE_END).getBytes();
		InputStream payloadStream = new ByteArrayInputStream(buf);

		try {
			response.setHeaderField("Content-Length", HTTPMessageHeaderValue.parseHeaderValue(String.valueOf(buf.length)));
			response.setPayloadStream(payloadStream);
		} catch(HTTPException e) {
		}

		return response;
	}
}