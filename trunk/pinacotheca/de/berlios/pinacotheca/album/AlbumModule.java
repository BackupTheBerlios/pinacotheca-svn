package de.berlios.pinacotheca.album;

import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;

public class AlbumModule extends PTResponder implements PTModule {
	
	public AlbumModule() throws HTTPException {
		super();
	}

	private HTTPRequest request;
	
	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/album/".length());
		
		if(reqURL.equals("")) {
			returnXMLFile("album/index.xml");
		} else if(reqURL.startsWith("template/")) {
			returnXMLFile("templates/album/" + reqURL.substring("template/".length()));
		} else if(reqURL.startsWith("show/photo/")) {
			returnHTMLFile("templates/album/photo.htm");
		} else if(reqURL.startsWith("show/")) {
			returnAlbum(reqURL.substring("show/".length()));
		} else if(reqURL.startsWith("photo/")) {
			returnPhoto(reqURL.substring("photo/".length()));
		} else if(reqURL.equals("stylesheet")) {
			returnCSSStylesheet("stylesheet.css");
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}
	
	private void returnAlbum(String album) throws HTTPException {
		returnXMLFile("album/album_" + album + ".xml");
	}

	private void returnPhoto(String photo) throws HTTPException {
		if(photo.startsWith("thumb/"))
			photo = "th_" + photo.substring("thumb/".length());
		returnImageFile("photos/photo_" + photo + ".jpg");
	}


	public void setSecureConnection(boolean isSecure) {
		// Do nothing, not needed here
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}

	protected HTTPRequest getRequest() {
		return request;
	}
}
