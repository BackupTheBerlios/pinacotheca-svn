package de.berlios.pinacotheca.http;

import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotImplementedException;
import de.berlios.pinacotheca.http.exceptions.HTTPVersionNotSupportedException;

public class HTTPRequestBuilder {
	
	private final static String[] REQUESTS_SUPPORTED = { "HEAD", "GET", "POST" }; 
	private final static String[] VERSIONS_SUPPORTED = { "1.0", "1.1" };
	private final static String[] PARAMETER_HEADERS = { "Content-Disposition", "Content-Type" }; 
	private HTTPMessage message;
	
	public HTTPRequestBuilder(String messageStart) throws HTTPException {
		parseRequest(messageStart);
	}

	private void parseRequest(String messageStart) throws HTTPException {
		int delim, offset = 0;
		String method, url, version;
		
		delim = messageStart.indexOf(" ");
		if(delim == -1) throw new HTTPBadRequestException();
		method = messageStart.substring(offset, delim);
		checkRequestMethod(method);
		offset = delim + 1;
		
		delim = messageStart.indexOf(" ", offset);
		if(delim == -1) throw new HTTPBadRequestException();
		url = messageStart.substring(offset, delim);
		if(!url.startsWith("/")) throw new HTTPBadRequestException();
		offset = delim + 1;
		
		version = messageStart.substring(offset).trim();
		checkVersion(version);
		
		message = new HTTPRequest();
		((HTTPRequest) message).setRequestMethod(method);
		((HTTPRequest) message).setRequestURL(url);
		((HTTPRequest) message).setHttpVersion(version);
	}

	private void checkVersion(String version) throws HTTPVersionNotSupportedException {
		boolean isSupported = false;
		for(String supportedVersion : VERSIONS_SUPPORTED) {
			if(version.equals("HTTP/" + supportedVersion)) {
				isSupported = true;
				break;
			}
		}
		if(!isSupported) throw new HTTPVersionNotSupportedException();
	}
	
	private void checkRequestMethod(String method) throws HTTPNotImplementedException {
		boolean isSupported = false;
		for(String supportedRequest : REQUESTS_SUPPORTED) {
			if(method.equals(supportedRequest)) {
				isSupported = true;
				break;
			}
		}
		if(!isSupported) throw new HTTPNotImplementedException();
	}

	public void parseHeaderLine(String header) throws HTTPException {
		int delim;
		String fieldName, fieldValue;
		HTTPMessageHeaderValue headerValue;
		
		delim = header.indexOf(':');
		if(delim == -1) throw new HTTPBadRequestException();
		fieldName = header.substring(0, delim).trim();
		fieldValue = header.substring(delim + 1).trim();
		headerValue = (isParameterField(fieldName)) ? HTTPMessageHeaderValue.parseHeaderValue(fieldValue) : new HTTPMessageHeaderValue(fieldValue);
		message.setHeaderField(fieldName, headerValue);
	}


	private boolean isParameterField(String fieldName) {
		for(int i = 0; i < PARAMETER_HEADERS.length; i++) {
			if(PARAMETER_HEADERS[i].equals(fieldName)) return true;
		}
		return false;
	}

	public HTTPMessage getMessage() {
		return message;
	}
}
