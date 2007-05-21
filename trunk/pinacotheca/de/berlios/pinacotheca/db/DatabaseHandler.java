package de.berlios.pinacotheca.db;

import java.util.ArrayList;

import de.berlios.pinacotheca.PTConfiguration;

public class DatabaseHandler {

	/**
	 * @uml.property name="configuration"
	 */
	private PTConfiguration configuration;

	/**
	 */
	private DatabaseHandler() {
	}
	
	/**
	 */
	public void init(PTConfiguration configuration) {
	}

	/**
	 */
	public ArrayList<AOAlbum> getAlbums() {
		return null;
	}

	public AOAlbum getAlbum(int id) {

		return null;
	}

	/**
	 */
	public void getPhotos(AOAlbum album) {
	}

	public AOPhoto getPhoto(long id) {

		return null;
	}

	public ArrayList<AOPhoto> getNextPhotos(AOPhoto photo, short num) {

		return null;
	}

	public ArrayList<AOPhoto> getPreviousPhotos(AOPhoto photo, short num) {

		return null;
	}

	/**
	 */
	public void getTags(AOPhoto photo) {
	}

	/** 
	 * @uml.property name="instance" readOnly="true"
	 */
	private static DatabaseHandler instance;

		
			
			
			public static DatabaseHandler getInstance()	throws DatabaseNotInitializedException {
			
						return null;
					 }

}
