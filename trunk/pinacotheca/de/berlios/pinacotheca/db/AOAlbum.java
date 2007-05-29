package de.berlios.pinacotheca.db;

import java.util.ArrayList;

public class AOAlbum {

	/**
	 * @uml.property name="id"
	 */
	private int id;

	/**
	 * Getter of the property <tt>id</tt>
	 * 
	 * @return Returns the id.
	 * @uml.property name="id"
	 */
	public int getId() {
		return id;
	}

	/**
	 * Setter of the property <tt>id</tt>
	 * 
	 * @param id
	 *            The id to set.
	 * @uml.property name="id"
	 */
	public void setId(int id) {
		this.id = id;
	}

	/**
	 * @uml.property name="name"
	 */
	private String name = "null";

	/**
	 * Getter of the property <tt>name</tt>
	 * 
	 * @return Returns the name.
	 * @uml.property name="name"
	 */
	public String getName() {
		return name;
	}

	/**
	 * Setter of the property <tt>name</tt>
	 * 
	 * @param name
	 *            The name to set.
	 * @uml.property name="name"
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @uml.property name="description"
	 */
	private String description = "null";

	/**
	 * Getter of the property <tt>description</tt>
	 * 
	 * @return Returns the description.
	 * @uml.property name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter of the property <tt>description</tt>
	 * 
	 * @param description
	 *            The description to set.
	 * @uml.property name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 */
	public AOAlbum(int id, String name) {
		this.id = id;
		this.name = name;
	}

	/**
	 * @uml.property name="photos"
	 */
	private ArrayList<AOPhoto> photos = null;

	/**
	 * Getter of the property <tt>photos</tt>
	 * 
	 * @return Returns the photos.
	 * @uml.property name="photos"
	 */
	public ArrayList<AOPhoto> getPhotos() {
		return photos;
	}

	/**
	 * Setter of the property <tt>photos</tt>
	 * 
	 * @param photos
	 *            The photos to set.
	 * @uml.property name="photos"
	 */
	public void setPhotos(ArrayList<AOPhoto> photos) {
		this.photos = photos;
	}

}
