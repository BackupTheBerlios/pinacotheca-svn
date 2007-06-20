package de.berlios.pinacotheca.db;

public class AOTag {

	private int tId;

	private String name = "";

	public AOTag(int tId) {

		this.tId = tId;
	}

	public int getTId() {
		return tId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public boolean equals(Object obj) {
		AOTag othertag = (AOTag) obj;
		return tId == othertag.tId && name.equals(othertag.name);
	}
}
