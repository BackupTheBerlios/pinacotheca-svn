package de.berlios.pinacotheca.db;

import java.util.ArrayList;

;

public class AOPhoto {

	private int id;

	private int albumId;
	private String origFileName = null;
	private String description = null;
	private ArrayList<AOTag> tags = null;
	private String metadata = null;
	
	public AOPhoto() {
	}
	
	public AOPhoto(int id, int albumId, String origFileName) {
		this.id = id;
		this.albumId = albumId;
		this.origFileName = origFileName;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getAlbumId() {
		return albumId;
	}

	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	public String getOrigFileName() {
		return origFileName;
	}

	public void setOrigFileName(String origFileName) {
		this.origFileName = origFileName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}


	public ArrayList<AOTag> getTags() {
		return tags;
	}

	public void setTags(ArrayList<AOTag> tags) {
		this.tags = tags;
	}

	public String getMetadata() {
		return metadata;
	}

	public void setMetadata(String metadata) {
		this.metadata = metadata;
	}
}
