package de.berlios.pinacotheca.db;

import java.util.ArrayList;

public class AOAlbum {

	private int id;

	private String name = "null";

	private String description = "null";

	private ArrayList<AOPhoto> photos = null;
	
	public AOAlbum() {
	}

	public AOAlbum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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
}
