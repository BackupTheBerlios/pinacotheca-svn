package de.berlios.pinacotheca.http;

import java.util.HashMap;
import java.util.StringTokenizer;

import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;

public class HTTPMessageHeaderValue {
	
	
	private String token;
	private HashMap<String, String> parameters = new HashMap<String, String>();
	
	public HTTPMessageHeaderValue(String token) {
		this.token = token;
	}
	
	@Override
	public String toString() {
		StringBuffer out = new StringBuffer(token);
		
		for(String key : parameters.keySet()) {
			out.append(";");
			out.append(key);
			out.append("=");
			out.append(parameters.get(key));
		}
		return out.toString();
	}
	
	public String getToken() {
		return token;
	}
	
	public void setParameter(String attribute, String value) {
		this.parameters.put(attribute, value);
	}
	
	public boolean containsAttribute(String attribute) {
		return parameters.containsKey(attribute);
	}
	
	public String getAttribute(String attribute) {
		return parameters.get(attribute);
	}
	
	public static HTTPMessageHeaderValue parseHeaderValue(String headerValue) throws HTTPException {
		StringTokenizer tokenizer = new StringTokenizer(headerValue, ";");
		String token = tokenizer.nextToken().trim();
		HTTPMessageHeaderValue parsedHeaderValue = new HTTPMessageHeaderValue(token);
		
		while(tokenizer.hasMoreTokens()) {
			String headerToken = tokenizer.nextToken();
			int delim = headerToken.indexOf('=');
			if(delim == -1) throw new HTTPBadRequestException();
			String attribute = headerToken.substring(0, delim).trim();
			String value = headerToken.substring(delim + 1).trim();
			if(value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"')
				value = value.substring(1, value.length() - 1);
			parsedHeaderValue.setParameter(attribute, value);
		}
		
		return parsedHeaderValue;
	}
}
