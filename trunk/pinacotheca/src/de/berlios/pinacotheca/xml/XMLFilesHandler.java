package de.berlios.pinacotheca.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.AOPhoto;
import de.berlios.pinacotheca.db.AOTag;
import de.berlios.pinacotheca.db.DatabaseException;
import de.berlios.pinacotheca.db.DatabaseHandler;

public class XMLFilesHandler {
	private DatabaseHandler dbHandler;

	public XMLFilesHandler() throws DatabaseException {
		dbHandler = DatabaseHandler.getInstance();
	}

	public void generateIndexFiles() throws IOException, DatabaseException {
		File indexFile = new File(PTConfiguration.getServerRoot(), "album/index.xml");
		FileOutputStream stream = new FileOutputStream(indexFile);
		ArrayList<AOAlbum> albums = dbHandler.getAlbums();

		writeIndexFile(stream, "/album/template/index.xsl", albums);
		indexFile = new File(PTConfiguration.getServerRoot(), "admin/index.xml");
		stream = new FileOutputStream(indexFile);
		writeIndexFile(stream, "/admin/template/index.xsl", albums);
	}

	public void writeIndexFile(FileOutputStream stream, String xslURL, ArrayList<AOAlbum> albums) throws IOException {
		XMLWriter writer = new XMLWriter(stream);

		writer.setXSLTemplate(xslURL);
		writer.addNode("albumlist");

		for (AOAlbum album : albums) {
			HashMap<String, String> attributes = new HashMap<String, String>();

			attributes.put("id", String.valueOf(album.getAId()));
			writer.addNode("album", false, attributes);
			writer.addNode("name");
			writer.setNodeValue(album.getName());
			writer.closeNode();
			writer.addNode("description");
			writer.setNodeValue(album.getDescription());
			writer.closeNode();
			writer.closeNode(); // album
		}

		writer.closeNode(); // albumlist
		stream.close();
	}

	public void generateAlbumFiles(int albumId) throws IOException, DatabaseException {
		File albumFile = new File(PTConfiguration.getServerRoot(), "album/album_" + albumId + ".xml");
		FileOutputStream stream = new FileOutputStream(albumFile);
		AOAlbum album = dbHandler.getAlbumById(albumId);

		dbHandler.getPhotos(album);
		writeAlbumFile(stream, "/album/template/album.xsl", album, false);
		albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + albumId + ".xml");
		stream = new FileOutputStream(albumFile);
		writeAlbumFile(stream, "/admin/template/album.xsl", album, true);
	}

	private void writeAlbumFile(FileOutputStream stream, String xslURL, AOAlbum album, boolean writeAuthkey) throws IOException {
		XMLWriter writer = new XMLWriter(stream);
		ArrayList<AOPhoto> photos;
		String key;
		
		photos = album.getPhotos();
		writer.setXSLTemplate(xslURL);
		writer.addNode("album");
		writer.addNode("id");
		writer.setNodeValue(String.valueOf(album.getAId()));
		writer.closeNode();
		writer.addNode("name");
		writer.setNodeValue(album.getName());
		writer.closeNode();
		writer.addNode("description");
		writer.setNodeValue(album.getDescription());
		writer.closeNode();
		
		if(writeAuthkey) {
			key = album.getAuthkey();
			if(key.trim().length() == 0)
				key = "no key";
			
    		writer.addNode("authkey");
    		writer.setNodeValue(key);
    		writer.closeNode();
		}
		
		writer.addNode("photolist");

		for (AOPhoto photo : photos) {
			HashMap<String, String> attributes = new HashMap<String, String>();

			attributes.put("id", String.valueOf(photo.getPId()));
			attributes.put("name", photo.getFilename());
			writer.addNode("photo", true, attributes);
		}

		writer.closeNode(); // photolist
		writer.closeNode(); // album
		stream.close();
	}

	public void generatePhotoAdminFiles(int albumId) throws IOException, DatabaseException {
		AOAlbum album = dbHandler.getAlbumById(albumId);
		ArrayList<AOPhoto> photos;
		File photoFile;
		FileOutputStream stream;

		dbHandler.getPhotos(album);
		photos = album.getPhotos();

		for (AOPhoto photo : photos) {
			photoFile = new File(PTConfiguration.getServerRoot(), "admin/photo_" + photo.getPId() + ".xml");
			stream = new FileOutputStream(photoFile);
			writePhotoAdminFile(stream, photo);
		}
	}

	public void generatePhotoAdminFile(int photoId) throws DatabaseException, IOException {
		AOPhoto photo = dbHandler.getPhotoById(photoId);
		File photoFile = new File(PTConfiguration.getServerRoot(), "admin/photo_" + photo.getPId() + ".xml");
		FileOutputStream stream = new FileOutputStream(photoFile);
		writePhotoAdminFile(stream, photo);
	}

	private void writePhotoAdminFile(FileOutputStream stream, AOPhoto photo) throws IOException {
		XMLWriter writer = new XMLWriter(stream);

		writer.setXSLTemplate("/admin/template/photo.xsl");
		writer.addNode("photo");
		writer.addNode("id");
		writer.setNodeValue(String.valueOf(photo.getPId()));
		writer.closeNode();
		writer.addNode("name");
		writer.setNodeValue(photo.getFilename());
		writer.closeNode();
		writer.addNode("aid");
		writer.setNodeValue(String.valueOf(photo.getAId()));
		writer.closeNode();
		writer.addNode("description");
		writer.setNodeValue(photo.getDescription());
		writer.closeNode();
		writer.closeNode(); // photo
		stream.close();
	}
	
	public void generateTagFiles() throws IOException, DatabaseException {
		File tagsFile = new File(PTConfiguration.getServerRoot(), "admin/tags.xml");
		FileOutputStream stream = new FileOutputStream(tagsFile);
		ArrayList<AOTag> tags = dbHandler.getTags();
		
		writeTagFile(stream, "/admin/template/tags.xsl", tags);
		tagsFile = new File(PTConfiguration.getServerRoot(), "album/tags.xml");
		stream = new FileOutputStream(tagsFile);
		writeTagFile(stream, "/album/template/tags.xsl", tags);
	}
	
	private void writeTagFile(FileOutputStream stream, String xslURL, ArrayList<AOTag> tags) throws IOException {
		XMLWriter writer = new XMLWriter(stream);
		HashMap<String, String> attributes;
		
		writer.setXSLTemplate(xslURL);
		
		writer.addNode("taglist");
		for(AOTag tag : tags) {
			attributes = new HashMap<String, String>();
			attributes.put("id", String.valueOf(tag.getTId()));
			attributes.put("name", tag.getName());
			writer.addNode("tag", true, attributes);
		}
		writer.closeNode(); //taglist
	}
}
