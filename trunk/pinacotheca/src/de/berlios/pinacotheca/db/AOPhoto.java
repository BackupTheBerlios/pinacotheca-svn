package de.berlios.pinacotheca.db;

import java.util.ArrayList;

public class AOPhoto {

	private int pId;

	private int aId = -1;

	private String filename = "";

	private String description = "";

	private ArrayList<AOTag> tags = null;

	private String metadata = "";

	public AOPhoto(int pId) {
		this.pId = pId;
	}

	public int getPId() {
		return pId;
	}

	public int getAId() {
		return aId;
	}

	public void setAId(int aId) {
		this.aId = aId;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
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
