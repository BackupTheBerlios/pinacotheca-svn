package de.berlios.pinacotheca.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.berlios.pinacotheca.PTConfiguration;

public class DatabaseHandler {

	/**
	 * @uml.property name="configuration"
	 */
	private PTConfiguration configuration;

	private Connection dbConnection;


	/**
	 */
	private DatabaseHandler() {

	}

	public void init(PTConfiguration configuration) throws DatabaseException {

		System.setProperty("derby.system.home", "C:/Basti/db");

		try {

			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			this.dbConnection = DriverManager
					.getConnection("jdbc:derby:TestDB;create=true");


		} catch (ClassNotFoundException e) {
			throw new DatabaseException(e.getMessage());
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}
	

	// method to make a ArrayList of all albums from the database
	// create by ebse. right?
	public ArrayList<AOAlbum> getAlbums() throws DatabaseException {

		ResultSet rs;
		ArrayList<AOAlbum> list = null;

		try {
			
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM album");

			while (rs.next()) {

				int id = rs.getInt(1);
				String name = rs.getString(2);

				AOAlbum album = new AOAlbum(id, name);

				album.setDescription(rs.getString(3));

				list.add(album);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return list;
	}

	// method to read the albim with the aid id from the database
	// create by ebse. right?
	public AOAlbum getAlbum(int id) throws DatabaseException {

		AOAlbum album = null;

		String aid = Integer.toString(id);

		ResultSet rs;
		try {
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM album WHERE id = "
					+ aid);

			while (rs.next()) {

				String name = rs.getString(2);

				album = new AOAlbum(id, name);

				album.setDescription(rs.getString(3));

			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return album;
	}

	// rigth return value ArrayList? was void before!
	// method to make a ArrayList of all photos from a album
	// create by ebse. right?
	public ArrayList<AOPhoto> getPhotos(AOAlbum album) throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = null;

		String aid = Integer.toString(album.getId());

		try {
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM photo WHERE aid = "
					+ aid);

			while (rs.next()) {

				AOPhoto photo = buildPhotoAO(rs);
				list.add(photo);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return list;
	}

	private AOPhoto buildPhotoAO(ResultSet rs) throws SQLException {

		AOPhoto photo;

		long pid = rs.getLong("pid");
		int aid = rs.getInt("aid");
		String description = rs.getString("description");
		String origFileName = rs.getString("origFileName");
		String metadata = rs.getString("metadata");

		photo = new AOPhoto(pid, aid, origFileName);
		photo.setDescription(description);
		photo.setMetadata(metadata);

		return photo;
	}

	// method to read the photo with the pid id from the database
	// create by ebse. right?
	public AOPhoto getPhoto(long id) throws DatabaseException {

		AOPhoto photo = null;
		ResultSet rs;

		String pid = Long.toString(id);

		try {
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM photo WHERE pid = "
					+ pid);

			if (rs.next()) {

				photo = buildPhotoAO(rs);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return photo;
	}

	// method to make a ArrayList of photos with the number of num who are
	// towards
	// of the given photo in the database
	// create by ebse. right?
	public ArrayList<AOPhoto> getNextPhotos(AOPhoto photo, short num)
			throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = null;

		String aid = Integer.toString(photo.getAlbumId());
		long pid = photo.getId();

		try {
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM photo WHERE aid = "
					+ aid + " AND pid > " + pid + " ORDER BY pid ASC LIMIT 0,"
					+ num);

			while (rs.next()) {

				long id = rs.getLong(1);
				AOPhoto lPhoto = getPhoto(id);
				list.add(lPhoto);
			}

			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return list;
	}

	// method to make a ArrayList of photos with the number of num who are ahead
	// of the given photo in the database
	// create by ebse. right?
	public ArrayList<AOPhoto> getPreviousPhotos(AOPhoto photo, short num)
			throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = null;

		String aid = Integer.toString(photo.getAlbumId());
		long pid = photo.getId();

		try {
			rs = dbConnection.createStatement().executeQuery("SELECT * FROM photo WHERE aid = "
					+ aid + " AND pid < " + pid + " ORDER BY pid DESC LIMIT 0,"
					+ num);

			while (rs.next()) {

				long id = rs.getLong(1);
				AOPhoto lPhoto = getPhoto(id);
				list.add(lPhoto);
			}

			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return list;
	}

	// rigth return value ArrayList? was void before!
	// create by ebse. right?
	// method to to read all tags from a photo out of the database and put they
	// into a ArrayList
	public ArrayList<AOTag> getTags(AOPhoto photo) throws DatabaseException {

		ResultSet rs;
		ArrayList<AOTag> list = null;

		String pid = Long.toString(photo.getId());

		try {
			rs = dbConnection.createStatement()
					.executeQuery("SELECT tag.tid, tag.name FROM tag "
							+ "INNER JOIN photo_tag ON tag.tid = photo_tag.tid "
							+ "WHERE photo_tag.pid = " + pid);

			while (rs.next()) {

				int id = rs.getInt(1);
				String name = rs.getString(2);

				AOTag tag = new AOTag(id, name);

				list.add(tag);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		return list;
	}

	// method to write a photo into the database table photo
	// create by ebse. rigth and rigth place or other class?
	public void writePhoto(AOPhoto photo) throws DatabaseException {

		int pid = (int) photo.getId();

		String values = "VALUES (" + pid + "," + photo.getAlbumId() + ", '"
				+ photo.getOrigFileName() + "', '" + photo.getDescription()
				+ "', '" + photo.getMetadata() + "')";

		try {
			dbConnection.createStatement()
					.executeUpdate("INSERT INTO photo (pid, aid, origFileName, "
							+ "description, metadata)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	// method to write a album into the database table album
	// create by ebse. rigth and rigth place or other class?
	public void writeAlbum(AOAlbum album) throws DatabaseException {

		String values = "VALUES (" + album.getId() + "," + album.getName()
				+ "', '" + album.getDescription() + "')";

		try {
			dbConnection.createStatement().executeUpdate("INSERT INTO album (aid, name,"
					+ " description)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	// method to write a tag into the database table tag
	// create by ebse. rigth and rigth place or other class?
	public void writeTag(AOTag tag) throws DatabaseException {

		String values = "VALUES (" + tag.getId() + "," + tag.getName() + "')";

		try {
			dbConnection.createStatement()
					.executeUpdate("INSERT INTO tag (tid, name)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	/**
	 * @uml.property name="instance" readOnly="true"
	 */
	private static DatabaseHandler instance;

	public static DatabaseHandler getInstance()
			throws DatabaseNotInitializedException {

		return null;
	}

}
