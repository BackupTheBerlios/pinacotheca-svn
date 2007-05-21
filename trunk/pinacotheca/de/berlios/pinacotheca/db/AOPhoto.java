package de.berlios.pinacotheca.db;

import java.util.ArrayList;;


public class AOPhoto {

	/**
	 * @uml.property  name="id"
	 */
	private long id;

	/**
	 * Getter of the property <tt>id</tt>
	 * @return  Returns the id.
	 * @uml.property  name="id"
	 */
	public long getId() {
		return id;
	}

	/**
	 * Setter of the property <tt>id</tt>
	 * @param id  The id to set.
	 * @uml.property  name="id"
	 */
	public void setId(long id) {
		this.id = id;
	}

	/**
	 * @uml.property  name="albumId"
	 */
	private int albumId;

	/**
	 * Getter of the property <tt>albumId</tt>
	 * @return  Returns the albumId.
	 * @uml.property  name="albumId"
	 */
	public int getAlbumId() {
		return albumId;
	}

	/**
	 * Setter of the property <tt>albumId</tt>
	 * @param albumId  The albumId to set.
	 * @uml.property  name="albumId"
	 */
	public void setAlbumId(int albumId) {
		this.albumId = albumId;
	}

	/**
	 * @uml.property  name="origFileName"
	 */
	private String origFileName = "";

	/**
	 * Getter of the property <tt>origFileName</tt>
	 * @return  Returns the origFileName.
	 * @uml.property  name="origFileName"
	 */
	public String getOrigFileName() {
		return origFileName;
	}

	/**
	 * Setter of the property <tt>origFileName</tt>
	 * @param origFileName  The origFileName to set.
	 * @uml.property  name="origFileName"
	 */
	public void setOrigFileName(String origFileName) {
		this.origFileName = origFileName;
	}

	/**
	 * @uml.property  name="description"
	 */
	private String description = "null";

	/**
	 * Getter of the property <tt>description</tt>
	 * @return  Returns the description.
	 * @uml.property  name="description"
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Setter of the property <tt>description</tt>
	 * @param description  The description to set.
	 * @uml.property  name="description"
	 */
	public void setDescription(String description) {
		this.description = description;
	}

	/**
	 * @uml.property  name="tags"
	 */
	private ArrayList<AOTag> tags = null;

	/**
	 * Getter of the property <tt>tags</tt>
	 * @return  Returns the tags.
	 * @uml.property  name="tags"
	 */
	public ArrayList<AOTag> getTags() {
		return tags;
	}

	/**
	 * Setter of the property <tt>tags</tt>
	 * @param tags  The tags to set.
	 * @uml.property  name="tags"
	 */
	public void setTags(ArrayList<AOTag> tags) {
		this.tags = tags;
	}

		
		/**
		 */
		public AOPhoto(long id, int albumId, String origFileName){
		}

		/**
		 * @uml.property  name="metadata"
		 */
		private String metadata = "";

		/**
		 * Getter of the property <tt>metadata</tt>
		 * @return  Returns the metadata.
		 * @uml.property  name="metadata"
		 */
		public String getMetadata() {
			return metadata;
		}

		/**
		 * Setter of the property <tt>metadata</tt>
		 * @param metadata  The metadata to set.
		 * @uml.property  name="metadata"
		 */
		public void setMetadata(String metadata) {
			this.metadata = metadata;
		}
}
