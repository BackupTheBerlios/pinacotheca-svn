package de.berlios.pinacotheca.xml;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.db.AOAlbum;
import de.berlios.pinacotheca.db.AOPhoto;
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
		
		for(AOAlbum album : albums) {
			HashMap<String, String> attributes = new HashMap<String, String>();
			
			attributes.put("id", String.valueOf(album.getId()));
			writer.addNode("album", false, attributes);
			writer.addNode("title");
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
		AOAlbum album = dbHandler.getAlbum(albumId);
		
		dbHandler.getPhotos(album);
		writeAlbumFile(stream, "/album/template/album.xsl", album);
		albumFile = new File(PTConfiguration.getServerRoot(), "admin/album_" + albumId + ".xml");
		stream = new FileOutputStream(albumFile);
		writeAlbumFile(stream, "/admin/template/album.xsl", album);
	}
	
	public void writeAlbumFile(FileOutputStream stream, String xslURL, AOAlbum album) throws IOException {
		XMLWriter writer = new XMLWriter(stream);
		ArrayList<AOPhoto> photos;
		
		photos = album.getPhotos();
		writer.setXSLTemplate(xslURL);
		writer.addNode("album");
		writer.addNode("id");
		writer.setNodeValue(String.valueOf(album.getId()));
		writer.closeNode();
		writer.addNode("title");
		writer.setNodeValue(album.getName());
		writer.closeNode();
		writer.addNode("description");
		writer.setNodeValue(album.getDescription());
		writer.closeNode();
		writer.addNode("photolist");
		
		for(AOPhoto photo : photos) {
			HashMap<String, String> attributes = new HashMap<String, String>();
			
			attributes.put("id", String.valueOf(photo.getId()));
			attributes.put("title", photo.getOrigFileName());
			attributes.put("description", photo.getDescription());
			writer.addNode("photo", true, attributes);
		}
		
		writer.closeNode(); // photolist
		writer.closeNode(); // album
		stream.close();
	}
}
