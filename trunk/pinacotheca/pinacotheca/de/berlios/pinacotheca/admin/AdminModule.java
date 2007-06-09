package de.berlios.pinacotheca.admin;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.AOPhoto;
import de.berlios.pinacotheca.db.AOTag;
import de.berlios.pinacotheca.db.DatabaseException;
import de.berlios.pinacotheca.db.DatabaseHandler;
import de.berlios.pinacotheca.http.HTTPAuthorizationCredentials;
import de.berlios.pinacotheca.http.HTTPMessageHeaderValue;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPRequestPart;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPForbiddenException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;
import de.berlios.pinacotheca.http.exceptions.HTTPUnauthorizedException;
import de.berlios.pinacotheca.xml.XMLFilesHandler;

public class AdminModule extends PTResponder implements PTModule {
	private HTTPRequest request;

	private boolean secureConnection;

	private DatabaseHandler dbHandler;

	private XMLFilesHandler filesHandler;

	private static boolean sessionLock = false;

	private static long lastSessionAccess = 0L;

	public AdminModule() throws HTTPException {
		try {
			dbHandler = DatabaseHandler.getInstance();
			filesHandler = new XMLFilesHandler();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/admin".length());
		HTTPAuthorizationCredentials credentials;

		if (!secureConnection)
			throw new HTTPNotFoundException(request.getRequestURL());

		synchronized (this) {
			if (sessionLock) {
				if ((System.currentTimeMillis() - lastSessionAccess) > 300000) {
					sessionLock = false;
					throw new HTTPUnauthorizedException();
				}
				if (!evalCookie())
					throw new HTTPForbiddenException();
			}

			credentials = request.getCredentials();

			if (!credentials.getUserid().equals(PTConfiguration.getAdminUser())
					|| !credentials.getPassword().equals(PTConfiguration.getAdminPass()))
				throw new HTTPUnauthorizedException();

			if (reqURL.equals("/")) {
				returnXMLFile("admin/index.xml");
			} else if (reqURL.startsWith("/template/")) {
				returnXMLFile("templates/admin/" + reqURL.substring("/template/".length()));
			} else if (reqURL.startsWith("/album/")) {
				handleAlbumAction(reqURL.substring("/album/".length()));
			} else if (reqURL.startsWith("/tags/")) {
				handleTagAction(reqURL.substring("/tags/".length()));
			} else if (reqURL.startsWith("/photo/")) {
				handlePhotoAction(reqURL.substring("/photo/".length()));
			} else if (reqURL.equals("/stylesheet")) {
				returnCSSStylesheet("stylesheet.css");
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}

			lastSessionAccess = System.currentTimeMillis();
			setHeaderField("Set-Cookie", "access=" + lastSessionAccess + "; path=/admin/");
			sessionLock = true;
		}
	}

	private boolean evalCookie() {
		String token, cValue;
		Long lVal;
		int delim;

		if (!request.containsHeaderField("Cookie"))
			return false;

		token = request.getHeaderField("Cookie").getToken();
		delim = token.indexOf('=');
		if (delim == -1)
			return false;
		if (!token.substring(0, delim).equals("access"))
			return false;
		cValue = token.substring(delim + 1);

		try {
			lVal = new Long(cValue);
			if (lVal != lastSessionAccess)
				return false;
		} catch (NumberFormatException e) {
			return false;
		}

		return true;
	}

	private void handleTagAction(String action) throws HTTPException {
		try {
			if (action.equals("")) {
				returnXMLFile("admin/tags.xml");
			} else if (action.equals("add")) {
				handleTagAddition();
			} else if (action.startsWith("delete/")) {
				Integer tagId = new Integer(action.substring("delete/".length()));

				handleTagDelete(tagId);
			} else if (action.startsWith("edit/")) {
				Integer tagId = new Integer(action.substring("edit/".length()));

				handleTagEdit(tagId);
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleTagEdit(Integer tagId) throws HTTPException {
		HashMap<String, String> postVars;
		AOTag tag;
		String tagName;

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();

		if (!postVars.containsKey("tagname"))
			throw new HTTPBadRequestException();

		try {
			tagName = postVars.get("tagname");
			tag = dbHandler.getTag(tagId);
			tag.setName(tagName);
			dbHandler.updateTag(tag);
			dbHandler.commit();
			filesHandler.generateTagsAdminFile();
			returnRedirect("/admin/tags/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleTagDelete(Integer tagId) throws HTTPException {
		try {
			AOTag tag = dbHandler.getTag(tagId);
			dbHandler.deleteTag(tag);
			dbHandler.commit();
			filesHandler.generateTagsAdminFile();
			returnRedirect("/admin/tags/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoAction(String action) throws HTTPException {
		try {
			if (action.startsWith("add/")) {
				Integer album = new Integer(action.substring("add/".length()));

				if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handlePhotoAddition(album);
				else
					returnAlbumEditForm(album);
			} else if (action.startsWith("edit/")) {
				Integer photoId = new Integer(action.substring("edit/".length()));

				if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handlePhotoEdit(photoId);
				else
					returnPhotoEditForm(photoId);
			} else if (action.startsWith("delete/")) {
				Integer photoId = new Integer(action.substring("delete/".length()));

				handlePhotoDelete(photoId);
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void returnPhotoEditForm(Integer photoId) throws HTTPException {
		returnXMLFile("admin/photo_" + photoId + ".xml");
	}

	private void handleAlbumAction(String action) throws HTTPException {
		try {
			if (action.startsWith("edit/")) {
				Integer album = new Integer(action.substring("edit/".length()));

				if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handleAlbumEdit(album);
				else
					returnAlbumEditForm(album);
			} else if (action.startsWith("delete/")) {
				Integer album = new Integer(action.substring("delete/".length()));

				handleAlbumDelete(album);
			} else if (action.equals("add/")) {
				if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handleAlbumAddition();
				else
					throw new HTTPNotFoundException(request.getRequestURL());
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handlePhotoDelete(Integer photoId) throws HTTPException {
		try {
			AOPhoto photo = dbHandler.getPhoto(photoId);
			File photoAdminFile = new File(PTConfiguration.getServerRoot(), "admin/photo_" + photoId + ".xml");
			File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + photoId + ".jpg");
			if (photoAdminFile.isFile())
				photoAdminFile.delete();
			if(photoFile.isFile())
				photoFile.delete();
			dbHandler.deletePhoto(photo);
			dbHandler.commit();
			filesHandler.generateAlbumFiles(photo.getAlbumId());
			returnRedirect("/admin/album/edit/" + photo.getAlbumId());
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleAlbumDelete(Integer albumId) throws HTTPException {
		try {
			AOAlbum album = new AOAlbum();
			File albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + albumId + ".xml");
			album.setId(albumId);
			dbHandler.deleteAlbum(album);
			dbHandler.commit();
			if (albumFile.isFile())
				albumFile.delete();
			albumFile = new File(PTConfiguration.getServerRoot(), "album/album_" + albumId + ".xml");
			if (albumFile.isFile())
				albumFile.delete();
			filesHandler.generateIndexFiles();
			returnRedirect("/admin/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoEdit(Integer photoId) throws HTTPException {
		HashMap<String, String> postVars;
		AOPhoto photo;

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();
		if (!postVars.containsKey("photodescription"))
			throw new HTTPBadRequestException();

		try {
			photo = dbHandler.getPhoto(photoId);
			photo.setDescription(postVars.get("photodescription"));
			dbHandler.updatePhoto(photo);
			dbHandler.commit();
			filesHandler.generatePhotoAdminFile(photo.getId());
			returnPhotoEditForm(photoId);
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleAlbumEdit(Integer albumId) throws HTTPException {
		HashMap<String, String> postVars;
		AOAlbum album = new AOAlbum();

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();
		if (!postVars.containsKey("albumname") || !postVars.containsKey("albumdescription"))
			throw new HTTPBadRequestException();

		try {
			album.setId(albumId);
			album.setName(postVars.get("albumname"));
			album.setDescription(postVars.get("albumdescription"));
			dbHandler.updateAlbum(album);
			dbHandler.commit();
			filesHandler.generateAlbumFiles(albumId);
			filesHandler.generateIndexFiles();
			returnAlbumEditForm(albumId);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleTagAddition() throws HTTPException {
		HashMap<String, String> postVars;
		AOTag tag;
		String tagName;

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();

		if (!postVars.containsKey("tagname"))
			throw new HTTPBadRequestException();

		try {
			tagName = postVars.get("tagname");
			tag = new AOTag();
			tag.setName(tagName);
			dbHandler.addTag(tag);
			dbHandler.commit();
			filesHandler.generateTagsAdminFile();
			returnRedirect("/admin/tags/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleAlbumAddition() throws HTTPException {
		HashMap<String, String> postVars;
		AOAlbum album;
		String albumName, albumDescription, url;
		XMLFilesHandler fHandler;
		int albumId;

		if (!request.hasPostVars())
			throw new HTTPBadRequestException();

		postVars = request.getPostVars();

		if (!postVars.containsKey("albumname"))
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
			returnRedirect(url);
		} catch (DatabaseException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoAddition(Integer albumId) throws HTTPException {
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
		photo.setAlbumId(albumId);
		photo.setDescription(photoDescription);
		photo.setOrigFileName(photoFileName);

		try {
			photoId = dbHandler.addPhoto(photo);
			dbHandler.commit();
			savePhoto("photo_" + photoId + ".jpg", part.getPayloadBuffer(), part.getPayloadStart(), part.getPayloadLength());
			filesHandler = new XMLFilesHandler();
			filesHandler.generateAlbumFiles(albumId);
			filesHandler.generatePhotoAdminFiles(albumId);
			getMetadata(photoId);
			returnRedirect("/admin/album/edit/" + albumId);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void getMetadata(int photoId) {
		try {
			AOPhoto photo;
			Directory exif;
			Iterator exifIt;
			Tag exifTag;
			StringBuffer exifData = new StringBuffer();
			File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + photoId + ".jpg");
			Metadata metadata = JpegMetadataReader.readMetadata(photoFile);
			if (!metadata.containsDirectory(ExifDirectory.class))
				return;
			exif = metadata.getDirectory(ExifDirectory.class);
			exifIt = exif.getTagIterator();
			while (exifIt.hasNext()) {
				exifTag = (Tag) exifIt.next();
				exifData.append(exifTag.toString() + "\n");
			}
			photo = dbHandler.getPhoto(photoId);
			photo.setMetadata(exifData.toString());
			dbHandler.updatePhoto(photo);
		} catch (DatabaseException e) {
			e.printStackTrace();
		} catch (JpegProcessingException e) {
			e.printStackTrace();
		}
	}

	private void savePhoto(String photoFileName, byte[] payloadBuffer, int payloadStart, int payloadLength) throws HTTPException,
			IOException {
		FileOutputStream oStream;
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/" + photoFileName);

		if (!photoFile.exists() && !photoFile.createNewFile())
			throw new HTTPServerErrorException();

		oStream = new FileOutputStream(photoFile);
		oStream.write(payloadBuffer, payloadStart, payloadLength);
		oStream.close();
	}

	private void returnAlbumEditForm(Integer album) throws HTTPException {
		returnXMLFile("admin/album_" + album + ".xml");
	}

	public void setSecureConnection(boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}

	protected HTTPRequest getRequest() {
		return request;
	}
}
