package de.berlios.pinacotheca.db;

public class AOComment {
	private int cId;
	
	private int pId = -1;
	
	private String name = "";

	private String text = "";
	
	public AOComment(int cId) {
		this.cId = cId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getPId() {
		return pId;
	}

	public void setPId(int pId) {
		this.pId = pId;
	}

	public int getCId() {
		return cId;
	}
}
