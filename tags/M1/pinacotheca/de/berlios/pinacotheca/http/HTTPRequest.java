package de.berlios.pinacotheca.http;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.StringTokenizer;

import sun.misc.BASE64Decoder;

import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;
import de.berlios.pinacotheca.http.exceptions.HTTPUnauthorizedException;

public class HTTPRequest extends HTTPMessage {
	public static final String TYPE_GET = "GET";

	public static final String TYPE_POST = "POST";

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

	public boolean hasPostVars() {
		if (!containsHeaderField("Content-Type"))
			return false;

		return getHeaderField("Content-Type").getToken().equals("application/x-www-form-urlencoded");
	}

	public HashMap<String, String> getPostVars() throws HTTPException {
		int contentLength;
		byte[] buffer;
		String payLoad;
		StringTokenizer tokenizer;
		HashMap<String, String> postVars = new HashMap<String, String>();

		if (!containsHeaderField("Content-Length"))
			throw new HTTPBadRequestException();
		try {
			contentLength = new Integer(getHeaderField("Content-Length").getToken());
			buffer = new byte[contentLength];
			if (getPayloadStream().read(buffer) != contentLength)
				throw new HTTPBadRequestException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		}

		payLoad = new String(buffer);
		tokenizer = new StringTokenizer(payLoad, "&");

		while (tokenizer.hasMoreTokens()) {
			String token = tokenizer.nextToken(), key = null, value = null;
			int delim = token.indexOf('=');
			if (delim == -1)
				throw new HTTPBadRequestException();
			try {
				key = URLDecoder.decode(token.substring(0, delim), "UTF-8");
				value = URLDecoder.decode(token.substring(delim + 1), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				throw new HTTPServerErrorException();
			}

			postVars.put(key, value);
		}
		return postVars;
	}

	public boolean hasMultipleParts() {
		if (!containsHeaderField("Content-Type"))
			return false;

		return getHeaderField("Content-Type").getToken().equals("multipart/form-data");
	}

	public HashMap<String, HTTPRequestPart> getParts() throws HTTPException {
		HTTPMessageHeaderValue contentType;
		HTTPRequestMultipartReader multipartReader = null;
		String boundary;
		Integer contentLength;
		byte[] buffer;

		contentType = getHeaderField("Content-Type");

		if (!containsHeaderField("Content-Length"))
			throw new HTTPBadRequestException();

		if (!contentType.containsAttribute("boundary"))
			throw new HTTPBadRequestException();

		boundary = contentType.getAttribute("boundary");

		try {
			contentLength = new Integer(getHeaderField("Content-Length").getToken());
			buffer = new byte[contentLength];
			readPayload(buffer, contentLength);
			multipartReader = new HTTPRequestMultipartReader(buffer, boundary);
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return multipartReader.getParts();
	}

	private void readPayload(byte[] buffer, Integer contentLength) throws HTTPException, IOException {
		int bytesRead = 0;
		int totalBytesRead = 0;
		
		while(totalBytesRead < contentLength) {
			bytesRead = getPayloadStream().read(buffer, totalBytesRead, contentLength - totalBytesRead);
			if(bytesRead == -1)
				throw new HTTPBadRequestException();
			totalBytesRead += bytesRead;
		}
	}

	public HTTPAuthorizationCredentials getCredentials() throws HTTPException {
		String token, userPass, userid, password;
		int delim;
		
		if(!containsHeaderField("Authorization"))
			throw new HTTPUnauthorizedException();
		
		token = getHeaderField("Authorization").getToken();
		
		if(!token.startsWith("Basic "))
			throw new HTTPUnauthorizedException();
		
		try {
			userPass = token.substring("Basic ".length());
			userPass = new String(new BASE64Decoder().decodeBuffer(userPass));
			
			delim = userPass.indexOf(':');
			if(delim == -1) throw new HTTPUnauthorizedException();
			userid = userPass.substring(0, delim);
			password = userPass.substring(delim + 1);
		} catch (IOException e) {
			throw new HTTPUnauthorizedException();
		}
		
		return new HTTPAuthorizationCredentials(userid, password);
	}
}
