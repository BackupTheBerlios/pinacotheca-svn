package de.berlios.pinacotheca.album;

import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.DatabaseException;
import de.berlios.pinacotheca.db.DatabaseHandler;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;

public class AlbumModule extends PTResponder implements PTModule {

	public AlbumModule() throws HTTPException {
		super();
	}

	private HTTPRequest request;

	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/album/".length());

		if (reqURL.equals("")) {
			returnXMLFile("album/index.xml");
		} else if (reqURL.equals("tagfilter")) {
			returnXMLFile("album/tags.xml");
		} else if (reqURL.startsWith("template/")) {
			returnXMLFile("templates/album/" + reqURL.substring("template/".length()));
		} else if (reqURL.startsWith("show/photo/")) {
			returnHTMLFile("templates/album/photo.htm");
		} else if (reqURL.startsWith("diashow/")) {
			returnHTMLFile("templates/album/diashow.htm");
		} else if (reqURL.startsWith("show/simplephoto/")) {
			returnHTMLFile("templates/album/simplephoto.htm");
		} else if (reqURL.startsWith("show/")) {
			returnAlbum(reqURL.substring("show/".length()));
		} else if (reqURL.startsWith("photo/")) {
			returnPhoto(reqURL.substring("photo/".length()));
		} else if (reqURL.equals("stylesheet")) {
			returnCSSStylesheet("stylesheet.css");
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void returnAlbum(String album) throws HTTPException {
		try {
			DatabaseHandler dbHandler = DatabaseHandler.getInstance();
			AOAlbum albumObj;
			Integer albumId;

			if (album.length() == 20) {
				albumObj = dbHandler.getAlbumByAuthkey(album);
				if(albumObj == null)
					throw new HTTPNotFoundException(request.getRequestURL());
			} else {
				albumId = new Integer(album);
				albumObj = dbHandler.getAlbumById(albumId);
				if(albumObj.getAuthkey().trim().length() != 0) {
					returnHTMLFile("templates/album/key.htm");
					return;
				}
			}

			returnXMLFile("album/album_" + albumObj.getAId() + ".xml");
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnPhoto(String photo) throws HTTPException {
		if (photo.startsWith("thumb/"))
			photo = "th_" + photo.substring("thumb/".length());
		if(photo.startsWith("small/"))
			photo = "small_" + photo.substring("small/".length());
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
