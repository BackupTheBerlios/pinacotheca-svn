package de.berlios.pinacotheca.admin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.HTTPMessageHeaderValue;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPRequestPart;
import de.berlios.pinacotheca.http.HTTPResponse;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPForbiddenException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;

public class AdminModule implements PTModule {
	private HTTPRequest request;

	private boolean secureConnection;

	private HTTPResponse response;

	public HTTPResponse getResponse() {
		return response;
	}

	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/admin".length());

//		if (!secureConnection)
//			throw new HTTPNotFoundException(request.getRequestURL());

		response = new HTTPResponse("HTTP/1.1", (short) 200, "OK");
		response.setHeaderField("Content-Length", "0");

		if (reqURL.equals("/")) {
			returnIndex();
		} else if (reqURL.startsWith("/template/")) {
			returnTemplate(reqURL.substring("/template/".length()));
		} else if (reqURL.startsWith("/album/")) {
			handleAlbumAction(reqURL.substring("/album/".length()));
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleAlbumAction(String action) throws HTTPException {
		if (action.startsWith("edit/")) {
			String album = action.substring("edit/".length());

			if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
				handleAlbumEdit(album);
			else
				returnEditForm(album);
		} else if (action.startsWith("add/")) {
			String album = action.substring("add/".length());

			if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
				handleAlbumAdd(album);
			else
				returnEditForm(album);
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleAlbumAdd(String album) throws HTTPException {
		HashMap<String, HTTPRequestPart> parts;
		HTTPRequestPart part;
		HTTPMessageHeaderValue headerValue;
		String photoDescription, photoFileName;
		byte[] payloadBuffer, strBuffer;
		int payloadStart, payloadLength, photoId;

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
		
		photoId = 3;
		
		try {
			savePhoto("photo_" + photoId + ".jpg", part.getPayloadBuffer(), part.getPayloadStart(), part.getPayloadLength());
		} catch (IOException e) {
			e.printStackTrace();
		}

		return;
	}

	private void savePhoto(String photoFileName, byte[] payloadBuffer, int payloadStart, int payloadLength) throws HTTPException, IOException {
		FileOutputStream oStream;
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/" + photoFileName);
		
		if(!photoFile.exists() && !photoFile.createNewFile())
			throw new HTTPServerErrorException();
		
		oStream = new FileOutputStream(photoFile);
		oStream.write(payloadBuffer, payloadStart, payloadLength);
	}

	private void handleAlbumEdit(String album) throws HTTPException {
		HashMap<String, String> postVars;

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();
		returnEditForm(album);
	}

	private void returnEditForm(String album) throws HTTPException {
		File albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + album + ".xml");

		assertReadable(albumFile);
		returnXMLFile(albumFile);
	}

	private void returnTemplate(String template) throws HTTPException {
		File templateFile = new File(PTConfiguration.getServerRoot(), "templates/admin/" + template);

		assertReadable(templateFile);
		returnXMLFile(templateFile);
	}

	private void returnIndex() throws HTTPException {
		File indexFile = new File(PTConfiguration.getServerRoot(), "admin/index.xml");

		assertReadable(indexFile);
		returnXMLFile(indexFile);
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
