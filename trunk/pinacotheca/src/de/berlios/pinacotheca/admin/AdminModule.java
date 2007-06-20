package de.berlios.pinacotheca.admin;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Random;

import net.sourceforge.jiu.data.IntegerImage;
import net.sourceforge.jiu.geometry.ScaleReplication;
import net.sourceforge.jiu.gui.awt.ImageCreator;
import net.sourceforge.jiu.gui.awt.ToolkitLoader;
import net.sourceforge.jiu.ops.MissingParameterException;
import net.sourceforge.jiu.ops.WrongParameterException;

import com.drew.imaging.jpeg.JpegMetadataReader;
import com.drew.imaging.jpeg.JpegProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.MetadataException;
import com.drew.metadata.Tag;
import com.drew.metadata.exif.ExifDirectory;
import com.sun.image.codec.jpeg.JPEGCodec;
import com.sun.image.codec.jpeg.JPEGImageEncoder;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.AOComment;
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

	private static long sessionInit = 0L;

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
			if (lVal != sessionInit)
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
			} else if (action.startsWith("edit/")) {
				Integer tagId = new Integer(action.substring("edit/".length()));

				handleTagEdit(tagId);
			} else if (action.startsWith("assign/")) {
				Integer albumId = new Integer(action.substring("assign/".length()));

				handleTagAssignment(albumId);
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleTagAddition() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		String name;

		postVars = request.getPostVars();

		if (!postVars.containsKey("tagname"))
			throw new HTTPBadRequestException();

		try {
			name = postVars.get("tagname").get(0);
			dbHandler.addTag(name);
			dbHandler.commit();
			filesHandler.generateTagFiles();
			returnRedirect("/admin/tags/");
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleTagAssignment(int aId) throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		Integer tId, pId;

		postVars = request.getPostVars();

		if (!postVars.containsKey("tagid"))
			throw new HTTPBadRequestException();

		try {
			tId = new Integer(postVars.get("tagid").get(0));

			for (String key : postVars.keySet()) {
				if (key.startsWith("assigntag")) {
					pId = new Integer(key.substring("assigntag".length()));
					dbHandler.addPhotoTag(pId, tId);
				}

				dbHandler.commit();
				returnRedirect("/admin/album/edit/" + aId);
			}
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}

	}

	private void handleTagEdit(int tId) throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		AOTag tag;
		String tagName;

		postVars = request.getPostVars();

		if (!postVars.containsKey("tagname"))
			throw new HTTPBadRequestException();

		try {
			tagName = postVars.get("tagname").get(0);

			if (tagName.trim().equals("")) {
				dbHandler.deleteTag(tId);
			} else {
				tag = dbHandler.getTagById(tId);
				tag.setName(tagName);
				dbHandler.updateTag(tag);
			}

			dbHandler.commit();
			filesHandler.generateTagFiles();
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
			} else if (action.startsWith("deletecomment/")) {
				Integer cId = new Integer(action.substring("deletecomment/".length()));

				handleCommentDelete(cId);
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleCommentDelete(Integer cId) throws HTTPException {
		try {
			AOComment comment = dbHandler.getComment(cId);

			dbHandler.deleteComment(comment.getCId());
			dbHandler.commit();
			returnRedirect("/admin/photo/edit/" + comment.getPId());
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoAddition(Integer aid) throws HTTPException {
		HashMap<String, HTTPRequestPart> parts;
		HTTPRequestPart part;
		HTTPMessageHeaderValue headerValue;
		String description, filename;
		byte[] payloadBuffer, strBuffer;
		int payloadStart, payloadLength, pId, delim;
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

		description = new String(strBuffer).trim();

		if (!parts.containsKey("photofile"))
			throw new HTTPBadRequestException();

		part = parts.get("photofile");
		headerValue = part.getHeaderField("Content-Disposition");

		if (!headerValue.containsAttribute("filename"))
			throw new HTTPBadRequestException();

		filename = headerValue.getAttribute("filename");
		delim = filename.lastIndexOf('\\');
		
		if (delim == -1)
			delim = filename.lastIndexOf('/');
		
		if(delim != -1)
			filename = filename.substring(delim + 1);

		try {
			pId = dbHandler.addPhoto(aid, filename, description, "");
			dbHandler.commit();
			savePhoto("photo_" + pId + ".jpg", part.getPayloadBuffer(), part.getPayloadStart(), part.getPayloadLength());
			saveThumbnail(pId);
			saveSmallDisplay(pId);
			filesHandler = new XMLFilesHandler();
			filesHandler.generateAlbumFiles(aid);
			filesHandler.generatePhotoAdminFiles(aid);
			getMetadata(pId);
			returnRedirect("/admin/album/edit/" + aid);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoEdit(int pId) throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		ArrayList<String> assignedtags = new ArrayList<String>();
		ArrayList<String> unassignedtags = new ArrayList<String>();
		AOPhoto photo;
		Integer tId;

		postVars = request.getPostVars();
		if (!postVars.containsKey("photodescription"))
			throw new HTTPBadRequestException();

		if (postVars.containsKey("assignedtags")) {
			assignedtags = postVars.get("assignedtags");
		}

		if (postVars.containsKey("unassignedtags")) {
			unassignedtags = postVars.get("unassignedtags");
		}

		try {
			photo = dbHandler.getPhotoById(pId);
			photo.setDescription(postVars.get("photodescription").get(0));
			dbHandler.updatePhoto(photo);

			for (String tagStr : assignedtags) {
				tId = new Integer(tagStr);

				dbHandler.addPhotoTag(pId, tId);
			}

			for (String tagStr : unassignedtags) {
				tId = new Integer(tagStr);

				dbHandler.deletePhotoTag(pId, tId);
			}

			dbHandler.commit();
			filesHandler.generatePhotoAdminFile(photo.getPId());
			returnPhotoEditForm(pId);
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handlePhotoDelete(Integer pId) throws HTTPException {
		try {
			AOPhoto photo = dbHandler.getPhotoById(pId);

			deletePhotoFiles(pId);
			dbHandler.deletePhoto(pId);
			dbHandler.commit();
			filesHandler.generateAlbumFiles(photo.getAId());
			returnRedirect("/admin/album/edit/" + photo.getAId());
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleKeyAction(Integer albumId) throws HTTPException {
		HashMap<String, ArrayList<String>> postVars = request.getPostVars();

		try {
			AOAlbum album = dbHandler.getAlbumById(albumId);
			if (postVars.containsKey("genkey")) {
				String key = genKey();

				album.setAuthkey(key);
			} else if (postVars.containsKey("delkey")) {
				album.setAuthkey("");
			} else {
				throw new HTTPBadRequestException();
			}

			dbHandler.updateAlbum(album);
			dbHandler.commit();
			filesHandler.generateAlbumFiles(albumId);
			returnRedirect("/admin/album/edit/" + albumId);
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private String genKey() {
		Random randomizer = new Random();
		StringBuffer key = new StringBuffer();
		int type;
		char val;

		for (int i = 0; i < 20; i++) {
			type = randomizer.nextInt(2);
			val = (char) ((type == 0) ? '0' + randomizer.nextInt(10) : 'a' + randomizer.nextInt(26));
			key.append(val);
		}

		return key.toString();
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
			} else if (action.equals("add")) {
				if (request.getRequestType().equals(HTTPRequest.TYPE_POST))
					handleAlbumAddition();
				else
					throw new HTTPNotFoundException(request.getRequestURL());
			} else if (action.startsWith("key/")) {
				Integer albumId = new Integer(action.substring("key/".length()));

				handleKeyAction(albumId);
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}
		} catch (NumberFormatException e) {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void handleAlbumAddition() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		String name, description, url;
		XMLFilesHandler fHandler;
		int albumId;

		postVars = request.getPostVars();

		if (!postVars.containsKey("albumname"))
			throw new HTTPBadRequestException();

		name = postVars.get("albumname").get(0);
		description = (postVars.containsKey("albumdescription")) ? postVars.get("albumdescription").get(0) : "";

		try {
			albumId = dbHandler.addAlbum(name, description, "");
			dbHandler.commit();
			url = "/admin/";
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

	private void handleAlbumEdit(Integer aId) throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		AOAlbum album;

		postVars = request.getPostVars();
		if (!postVars.containsKey("albumname") || !postVars.containsKey("albumdescription"))
			throw new HTTPBadRequestException();

		try {
			album = dbHandler.getAlbumById(aId);
			album.setName(postVars.get("albumname").get(0));
			album.setDescription(postVars.get("albumdescription").get(0));
			dbHandler.updateAlbum(album);
			dbHandler.commit();
			filesHandler.generateAlbumFiles(aId);
			filesHandler.generateIndexFiles();
			returnAlbumEditForm(aId);
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleAlbumDelete(Integer aId) throws HTTPException {
		try {
			File albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + aId + ".xml");
			AOAlbum album = dbHandler.getAlbumById(aId);

			dbHandler.getPhotos(album);

			for (AOPhoto photo : album.getPhotos()) {
				deletePhotoFiles(photo.getPId());
			}

			dbHandler.deleteAlbum(aId);
			dbHandler.commit();

			if (albumFile.isFile())
				albumFile.delete();

			albumFile = new File(PTConfiguration.getServerRoot(), "album/album_" + aId + ".xml");

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

	private void deletePhotoFiles(int pId) {
		File photoAdminFile = new File(PTConfiguration.getServerRoot(), "admin/photo_" + pId + ".xml");
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + pId + ".jpg");
		File thumbFile = new File(PTConfiguration.getServerRoot(), "photos/photo_th_" + pId + ".jpg");
		File smallFile = new File(PTConfiguration.getServerRoot(), "photos/photo_small_" + pId + ".jpg");

		if (photoAdminFile.isFile())
			photoAdminFile.delete();
		if (photoFile.isFile())
			photoFile.delete();
		if (thumbFile.isFile())
			thumbFile.delete();
		if (smallFile.isFile())
			smallFile.delete();
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

	private void saveThumbnail(int pId) throws HTTPException {
		ScaleReplication scale = new ScaleReplication();
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + pId + ".jpg");
		File thumbFile = new File(PTConfiguration.getServerRoot(), "photos/photo_th_" + pId + ".jpg");
		IntegerImage image = ToolkitLoader.loadAsRgb24Image(photoFile.getAbsolutePath());
		OutputStream outStream;
		JPEGImageEncoder encoder;
		BufferedImage bufImage;
		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth, newHeight;
		float factor = 0f;

		if (width / 4 >= height / 3) {
			factor = 12000 / width;
			newWidth = 120;
			newHeight = (int) (factor * height / 100);
		} else {
			factor = 9000 / height;
			newHeight = 90;
			newWidth = (int) (factor * width / 100);
		}

		try {
			outStream = new FileOutputStream(thumbFile);
			scale.setInputImage(image);
			scale.setSize(newWidth, newHeight);
			scale.process();
			encoder = JPEGCodec.createJPEGEncoder(outStream);
			bufImage = ImageCreator.convertToAwtBufferedImage(scale.getOutputImage());
			encoder.encode(bufImage);
			outStream.close();
		} catch (MissingParameterException e) {
			throw new HTTPServerErrorException();
		} catch (WrongParameterException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void saveSmallDisplay(int pId) throws HTTPException {
		ScaleReplication scale = new ScaleReplication();
		File photoFile = new File(PTConfiguration.getServerRoot(), "photos/photo_" + pId + ".jpg");
		File thumbFile = new File(PTConfiguration.getServerRoot(), "photos/photo_small_" + pId + ".jpg");
		IntegerImage image = ToolkitLoader.loadAsRgb24Image(photoFile.getAbsolutePath());
		OutputStream outStream;
		JPEGImageEncoder encoder;
		BufferedImage bufImage;
		int width = image.getWidth();
		int height = image.getHeight();
		int newWidth, newHeight;
		float factor = 0f;

		if (width / 4 >= height / 3) {
			factor = 60000 / width;
			newWidth = 600;
			newHeight = (int) (factor * height / 100);
		} else {
			factor = 45000 / height;
			newHeight = 450;
			newWidth = (int) (factor * width / 100);
		}

		try {
			outStream = new FileOutputStream(thumbFile);
			scale.setInputImage(image);
			scale.setSize(newWidth, newHeight);
			scale.process();
			encoder = JPEGCodec.createJPEGEncoder(outStream);
			bufImage = ImageCreator.convertToAwtBufferedImage(scale.getOutputImage());
			encoder.encode(bufImage);
			outStream.close();
		} catch (MissingParameterException e) {
			throw new HTTPServerErrorException();
		} catch (WrongParameterException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
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
			String tagName, tagValue;

			if (!metadata.containsDirectory(ExifDirectory.class))
				return;

			exif = metadata.getDirectory(ExifDirectory.class);
			exifIt = exif.getTagIterator();

			while (exifIt.hasNext()) {
				exifTag = (Tag) exifIt.next();
				tagName = exifTag.getTagName();
				tagValue = exifTag.getDescription();
				exifData.append(tagName + ":" + tagValue + '\n');
			}

			photo = dbHandler.getPhotoById(photoId);
			photo.setMetadata(exifData.toString());
			dbHandler.updatePhoto(photo);
			dbHandler.commit();
		} catch (DatabaseException e) {
			// TODO: Error Handling
			e.printStackTrace();
		} catch (JpegProcessingException e) {
			e.printStackTrace();
		} catch (MetadataException e) {
			e.printStackTrace();
		}
	}

	private void returnAlbumEditForm(Integer album) throws HTTPException {
		returnXMLFile("admin/album_" + album + ".xml");
	}

	private void returnPhotoEditForm(Integer photoId) throws HTTPException {
		returnXMLFile("admin/photo_" + photoId + ".xml");
	}

	protected HTTPRequest getRequest() {
		return request;
	}

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
					sessionInit = 0L;
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
			} else if (reqURL.equals("/logout")) {
				lastSessionAccess = 0L;
				returnRedirect("/admin/");
				return;
			} else {
				throw new HTTPNotFoundException(request.getRequestURL());
			}

			lastSessionAccess = System.currentTimeMillis();

			if (sessionInit == 0L) {
				sessionInit = lastSessionAccess;
				setHeaderField("Set-Cookie", "access=" + sessionInit + "; path=/admin/");
			}

			sessionLock = true;
		}
	}

	public void setSecureConnection(boolean secureConnection) {
		this.secureConnection = secureConnection;
	}

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}
}
