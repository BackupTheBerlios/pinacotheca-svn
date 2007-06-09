package de.berlios.pinacotheca;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPForbiddenException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;

public abstract class PTResponder {
	private HTTPResponse response;

	protected PTResponder() throws HTTPException {
		response = new HTTPResponse("HTTP/1.1", (short) 200, "OK");
		response.setHeaderField("Content-Length", "0");
	}

	protected abstract HTTPRequest getRequest();

	protected void setHeaderField(String fieldName, String fieldValue) throws HTTPException {
		response.setHeaderField(fieldName, fieldValue);
	}
	
	private void returnFile(String path) throws HTTPException {
		try {
			File file = new File(PTConfiguration.getServerRoot(), path);
			assertReadable(file);
			response.setHeaderField("Content-Length", String.valueOf(file.length()));
			response.setPayloadStream(new FileInputStream(file));
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void assertReadable(File file) throws HTTPException {
		if (!file.isFile())
			throw new HTTPNotFoundException(getRequest().getRequestURL());
		if (!file.canRead())
			throw new HTTPForbiddenException();
	}

	protected void returnHTMLFile(String path) throws HTTPException {
		response.setHeaderField("Content-Type", "text/html");
		returnFile(path);
	}

	protected void returnCSSStylesheet(String path) throws HTTPException {
		response.setHeaderField("Content-Type", "text/css");
		returnFile(path);
	}

	protected void returnXMLFile(String path) throws HTTPException {
		response.setHeaderField("Content-Type", "text/xml");
		returnFile(path);
	}

	protected void returnImageFile(String path) throws HTTPException {
		response.setHeaderField("Content-Type", "image/jpeg");
		returnFile(path);
	}
	
	protected void returnXMLStream(InputStream stream) throws HTTPException {
		response.setHeaderField("Content-Type", "text/xml");
		returnStream(stream);
	}
	
	protected void returnStream(InputStream stream) throws HTTPException {
		try {
			response.setHeaderField("Content-Length", String.valueOf(stream.available()));
			response.setPayloadStream(stream);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}
	
	protected void returnRedirect(String url) throws HTTPException {
		response.setResponseCode((short) 303);
		response.setResponseMessage("See Other");
		response.setHeaderField("Location", url);
		response.setHeaderField("Content-Length", "0");
	}

	public HTTPResponse getResponse() {
		return response;
	}
}
