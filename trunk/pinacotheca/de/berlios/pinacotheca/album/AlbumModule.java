package de.berlios.pinacotheca.album;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPForbiddenException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;

public class AlbumModule implements PTModule {
	private HTTPRequest request;
	
	private HTTPResponse response;

	public HTTPResponse getResponse() {
		return response;
	}

	public void handleRequest() throws HTTPException {
		String reqPath = request.getRequestURL().substring("/album".length());
		
		response = new HTTPResponse("HTTP/1.1", (short) 200, "OK");
		
		if(reqPath.equals("/")) {
			returnIndex();
		} else if(reqPath.startsWith("/template/")) {
			returnTemplate(reqPath.substring("/template/".length()));
		} else if(reqPath.startsWith("/show/")) {
			returnAlbum(reqPath.substring("/show/".length()));
		} else if(reqPath.startsWith("/photo/")) {
			returnPhoto(reqPath.substring("/photo/".length()));
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}
	
	private void returnAlbum(String album) throws HTTPException {
		int delim = album.indexOf("/");
		if(delim == -1) throw new HTTPNotFoundException(request.getRequestURL());
		album = album.substring(0, delim);
		File albumFile = new File(PTConfiguration.getServerRoot(), "album/album_" + album + ".xml");
		
		assertReadable(albumFile);
		returnXMLFile(albumFile);
	}

	private void returnPhoto(String photo) throws HTTPException {
		if(photo.startsWith("thumb/"))
			photo = "th_" + photo.substring("thumb/".length());
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + photo + ".jpg");
		
		assertReadable(photoFile);
		returnImageFile(photoFile);
	}


	private void assertReadable(File file) throws HTTPException {
		if(!file.isFile()) throw new HTTPNotFoundException(request.getRequestURL());
		if(!file.canRead()) throw new HTTPForbiddenException();
	}
	
	private void returnImageFile(File file) throws HTTPServerErrorException {
		try {
    		response.setHeaderField("Content-Length", String.valueOf(file.length()));
    		response.setHeaderField("Content-Type", "image/jpeg");
    		response.setPayloadStream(new FileInputStream(file));
		} catch(IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void returnXMLFile(File file) throws HTTPServerErrorException {
		try {
    		response.setHeaderField("Content-Length", String.valueOf(file.length()));
    		response.setHeaderField("Content-Type", "text/xml");
    		response.setPayloadStream(new FileInputStream(file));
		} catch(IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void returnTemplate(String template) throws HTTPException {
		File templateFile = new File(PTConfiguration.getServerRoot(), "templates/album/" + template);
		
		assertReadable(templateFile);
		returnXMLFile(templateFile);
	}

	private void returnIndex() throws HTTPException {
		File indexFile = new File(PTConfiguration.getServerRoot(), "album/index.xml");
		
		assertReadable(indexFile);
		returnXMLFile(indexFile);
	}

	public void setSecureConnection(boolean isSecure) {
		// Do nothing, not needed here
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}
}
