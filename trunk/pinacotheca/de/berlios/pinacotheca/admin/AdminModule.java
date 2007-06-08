package de.berlios.pinacotheca.admin;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTMain;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.AOPhoto;
import de.berlios.pinacotheca.db.DatabaseException;
import de.berlios.pinacotheca.db.DatabaseHandler;
import de.berlios.pinacotheca.http.HTTPMessageHeaderValue;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPRequestPart;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPForbiddenException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPSeeOtherException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;
import de.berlios.pinacotheca.xml.XMLFilesHandler;

public class AdminModule implements PTModule {
	private HTTPRequest request;

	private boolean secureConnection;

	private HTTPResponse response;
	
	private DatabaseHandler dbHandler;
	
	public AdminModule() throws HTTPServerErrorException {
		try {
			dbHandler = DatabaseHandler.getInstance();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	public HTTPResponse getResponse() {
		return response;
	}

	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/admin".length());

		if (!secureConnection)
			throw new HTTPNotFoundException(request.getRequestURL());

		response = new HTTPResponse("HTTP/1.1", (short) 200, "OK");
		response.setHeaderField("Content-Length", "0");

		if (reqURL.equals("/")) {
			returnIndex();
		} else if (reqURL.startsWith("/template/")) {
			returnXMLTemplate(reqURL.substring("/template/".length()));
		} else if (reqURL.startsWith("/album/")) {
			handleAlbumAction(reqURL.substring("/album/".length()));
		} else if (reqURL.startsWith("/photo/")) {
			handlePhotoAction(reqURL.substring("/photo/".length()));
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handlePhotoAction(String action) throws HTTPException {
		try {
			if(action.startsWith("add/")) {
				Integer album = new Integer(action.substring("add/".length()));
				
				if(request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handlePhotoAddition(album);
				else
					returnAlbumEditForm(album);
			}
		} catch(NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleAlbumAction(String action) throws HTTPException {
		try {
    		if (action.startsWith("edit/")) {
    			Integer album = new Integer(action.substring("edit/".length()));
    
    			if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
    				handleAlbumEdit(album);
    			else
    				returnAlbumEditForm(album);
    		} else if(action.startsWith("delete/")) {
    			Integer album = new Integer(action.substring("delete/".length()));
    			
    			handleAlbumDelete(album);
    		} else if(action.equals("add/")) {
    			if(request.getRequestType().equals(HTTPRequest.TYPE_POST))
    				handleAlbumAddition();
    			else
    				returnHTMLTemplate("albumadd.htm");
    		} else {
    			throw new HTTPNotFoundException(request.getRequestURL());
    		}
		} catch(NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleAlbumDelete(Integer albumId) throws HTTPException {
		AOAlbum album = new AOAlbum();
		File albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + albumId + ".xml");
		XMLFilesHandler fHandler;
		
		album.setId(albumId);
		
		try {
			fHandler = new XMLFilesHandler();
			dbHandler.deleteAlbum(album);
			if(albumFile.isFile())
				albumFile.delete();
			albumFile = new File(PTConfiguration.getServerRoot(), "album/album_" + albumId + ".xml");
			if(albumFile.isFile())
				albumFile.delete();
			fHandler.generateIndexFiles();
			throw new HTTPSeeOtherException("/admin/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void handleAlbumEdit(Integer album) throws HTTPException {
		HashMap<String, String> postVars;
	
		if (!request.hasPostVars())
			throw new HTTPBadRequestException();
	
		postVars = request.getPostVars();
		returnAlbumEditForm(album);
	}

	private void handleAlbumAddition() throws HTTPException {
		HashMap<String, String> postVars;
		AOAlbum album;
		String albumName, albumDescription, url;
		XMLFilesHandler fHandler;
		int albumId;
		
		if(!request.hasPostVars())
			throw new HTTPBadRequestException();
		
		postVars = request.getPostVars();
		
		if(!postVars.containsKey("albumname"))
			throw new HTTPBadRequestException();
		
		albumName = postVars.get("albumname");
		albumDescription = (postVars.containsKey("albumdescription")) ? postVars.get("albumdescription") : "";
		album = new AOAlbum();
		album.setName(albumName);
		album.setDescription(albumDescription);
		try {
			albumId = dbHandler.addAlbum(album);
			dbHandler.commit();
			url = "/admin/album/edit/" + albumId;
			fHandler = new XMLFilesHandler();
			fHandler.generateAlbumFiles(albumId);
			fHandler.generateIndexFiles();
			
			// TODO: Programming by Exception ... really bad!
			throw new HTTPSeeOtherException(url);
		} catch (DatabaseException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoAddition(Integer album) throws HTTPException {
		HashMap<String, HTTPRequestPart> parts;
		HTTPRequestPart part;
		HTTPMessageHeaderValue headerValue;
		String photoDescription, photoFileName;
		byte[] payloadBuffer, strBuffer;
		int payloadStart, payloadLength, photoId;
		AOPhoto photo;
		XMLFilesHandler filesHandler;

		if (!request.hasMultipleParts())
			throw new HTTPBadRequestException();

		parts = request.getParts();

		if (!parts.containsKey("photodescription"))
			throw new HTTPBadRequestException();

		part = parts.get("photodescription");
		payloadBuffer = part.getPayloadBuffer();
		payloadStart = part.getPayloadStart();
		payloadLength = part.getPayloadLength();
		strBuffer = new byte[payloadLength];

		for (int i = 0; i < strBuffer.length; i++)
			strBuffer[i] = payloadBuffer[i + payloadStart];

		photoDescription = new String(strBuffer).trim();

		if (!parts.containsKey("photofile"))
			throw new HTTPBadRequestException();

		part = parts.get("photofile");
		headerValue = part.getHeaderField("Content-Disposition");

		if (!headerValue.containsAttribute("filename"))
			throw new HTTPBadRequestException();
		
		photoFileName = headerValue.getAttribute("filename");
		photo = new AOPhoto();
		photo.setAlbumId(album);
		photo.setDescription(photoDescription);
		photo.setOrigFileName(photoFileName);
		
		try {
			photoId = dbHandler.addPhoto(photo);
			savePhoto("photo_" + photoId + ".jpg", part.getPayloadBuffer(), part.getPayloadStart(), part.getPayloadLength());
			filesHandler = new XMLFilesHandler();
			filesHandler.generateAlbumFiles(album);
			throw new HTTPSeeOtherException("/admin/album/edit/" + album);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void savePhoto(String photoFileName, byte[] payloadBuffer, int payloadStart, int payloadLength) throws HTTPException, IOException {
		FileOutputStream oStream;
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/" + photoFileName);
		
		if(!photoFile.exists() && !photoFile.createNewFile())
			throw new HTTPServerErrorException();
		
		oStream = new FileOutputStream(photoFile);
		oStream.write(payloadBuffer, payloadStart, payloadLength);
	}

	private void returnIndex() throws HTTPException {
		File indexFile = new File(PTConfiguration.getServerRoot(), "admin/index.xml");
	
		assertReadable(indexFile);
		returnXMLFile(indexFile);
	}

	private void returnAlbumEditForm(Integer album) throws HTTPException {
		File albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + album + ".xml");

		assertReadable(albumFile);
		returnXMLFile(albumFile);
	}

	private void returnXMLTemplate(String template) throws HTTPException {
		File templateFile = new File(PTConfiguration.getServerRoot(), "templates/admin/" + template);
	
		assertReadable(templateFile);
		returnXMLFile(templateFile);
	}

	private void returnXMLFile(File file) throws HTTPException {
		try {
			response.setHeaderField("Content-Length", String.valueOf(file.length()));
			response.setHeaderField("Content-Type", "text/xml");
			response.setPayloadStream(new FileInputStream(file));
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void returnHTMLTemplate(String template) throws HTTPException {
		File templateFile = new File(PTConfiguration.getServerRoot(), "templates/admin/" + template);

		assertReadable(templateFile);
		returnHTMLFile(templateFile);
	}

	private void returnHTMLFile(File file) throws HTTPException {
		try {
			response.setHeaderField("Content-Length", String.valueOf(file.length()));
			response.setHeaderField("Content-Type", "text/html");
			response.setPayloadStream(new FileInputStream(file));
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void assertReadable(File file) throws HTTPException {
		if (!file.isFile())
			throw new HTTPNotFoundException(request.getRequestURL());
		if (!file.canRead())
			throw new HTTPForbiddenException();
	}

	public void setSecureConnection(boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}
}
