package de.berlios.pinacotheca.db;

import java.util.ArrayList;

public class AOAlbum {

	private int aId;

	private String name = "";

	private String description = "";

	private ArrayList<AOPhoto> photos = null;
	
	private String authkey = "";
	
	public AOAlbum(int aId) {
		this.aId = aId;
	}

	public int getAId() {
		return aId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	
	public ArrayList<AOPhoto> getPhotos() {
		return photos;
	}

	public void setPhotos(ArrayList<AOPhoto> photos) {
		this.photos = photos;
	}

	public String getAuthkey() {
		return authkey;
	}

	public void setAuthkey(String key) {
		this.authkey = key;
	}
}
