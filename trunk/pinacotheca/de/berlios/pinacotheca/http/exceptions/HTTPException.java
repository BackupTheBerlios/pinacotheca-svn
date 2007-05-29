package de.berlios.pinacotheca.http.exceptions;

import java.io.ByteArrayInputStream;
import java.io.InputStream;

import de.berlios.pinacotheca.http.HTTPResponse;

public abstract class HTTPException extends Exception {
	private static final String PAGE_START = "<html>\n<head>\n<title>";
	
	private static final String PAGE_HEADER_END = "</title>\n</head>\n<body>\n<h1>";
	
	private static final String PAGE_HEADING_END = "</h1>\n<p>";

	private static final String PAGE_BETWEEN_START = "<p>";
	
	private static final String PAGE_BETWEEN_END = "</p>\n";
	
	private static final String PAGE_END  = "</body>\n</html>\n";

	private HTTPResponse response;
	
	private String responsePayoad = PAGE_START;

	protected HTTPException(Short responseCode, String responseMessage) {
		response = new HTTPResponse("HTTP/1.1", responseCode, responseMessage);
		
		if(responseCode < 200) {
			responsePayoad += "Information";
		} else if(responseCode < 300) {
			responsePayoad += "Successful";
		} else if(responseCode < 400) {
			responsePayoad += "Redirection";
		} else if(responseCode < 500) {
			responsePayoad += "Client Error";
		} else {
			responsePayoad += "Server Error";
		}
		
		responsePayoad += PAGE_HEADER_END;
		responsePayoad += responseCode + " " + responseMessage + PAGE_HEADING_END;
	}
	
	protected void addAdditionalInfo(String info) {
		responsePayoad += PAGE_BETWEEN_START + info + PAGE_BETWEEN_END;
	}
	
	protected void addHeader(String fieldName, String fieldValue) {
		response.setHeaderField(fieldName, fieldValue);
	}
	
	public HTTPResponse getResponse() {
		byte[] buf = (responsePayoad + PAGE_END).getBytes();
		InputStream payloadStream = new ByteArrayInputStream(buf);

		response.setHeaderField("Content-Length", String.valueOf(buf.length));
		response.setPayloadStream(payloadStream);

		return response;
	}
}