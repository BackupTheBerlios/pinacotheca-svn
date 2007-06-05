package de.berlios.pinacotheca.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

import de.berlios.pinacotheca.PTConfiguration;

public class DatabaseHandler {

	private static Connection dbConnection;

	public DatabaseHandler() {

	}

	/**
	 * method to open the Database connection. if the Database not created
	 * before, create Database pinacothecaDB and create the tables in this db
	 * 
	 * @throws DatabaseException
	 */
	public static void init() throws DatabaseException {

		File dbDir = new File(PTConfiguration.getServerRoot(), "db");
		File testFile = new File(dbDir, "pinacothecaDB");

		boolean createTables = !testFile.isDirectory();

		System.out.println(createTables);

		System.setProperty("derby.system.home", dbDir.getPath());

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");

			dbConnection = DriverManager
					.getConnection("jdbc:derby:pinacothecaDB;create=true");

			dbConnection.setAutoCommit(false);

			/**
			 * if database never create before
			 */
			if (createTables) {

				System.out.println("create Table");

				/**
				 * create table album
				 */
				dbConnection.createStatement().executeUpdate(
						"CREATE TABLE " + "album" + " ("
								+ "aid INTEGER PRIMARY KEY,"
								+ "name VARCHAR(50),"
								+ "description VARCHAR(100) )");

				/**
				 * create table photo
				 */
				dbConnection.createStatement().executeUpdate(
						"CREATE TABLE " + "photo" + " ("
								+ "pid INTEGER PRIMARY KEY,"
								+ "aid INTEGER REFERENCES album(aid),"
								+ "origFileName VARCHAR(50),"
								+ "description VARCHAR(100),"
								+ "metadata VARCHAR(1024) )");

				/**
				 * create table tag
				 */
				dbConnection.createStatement().executeUpdate(
						"CREATE TABLE " + "tag" + " ("
								+ "tid INTEGER PRIMARY KEY,"
								+ "name VARCHAR(50) )");

				/**
				 * create table photo_tag
				 */
				dbConnection.createStatement().executeUpdate(
						"CREATE TABLE " + "photo_tag" + " ("
								+ "pid INTEGER REFERENCES photo,"
								+ "tid INTEGER REFERENCES tag,"
								+ "PRIMARY KEY (pid, tid) )");
			}

		} catch (ClassNotFoundException e) {
			throw new DatabaseException(e.getMessage());
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	/**
	 * method to build a Album object
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AOAlbum buildAlbumAO(ResultSet rs) throws SQLException {

		AOAlbum album;

		int aid = rs.getInt("aid");
		String name = rs.getString("name");
		String description = rs.getString("description");

		album = new AOAlbum(aid, name);
		album.setDescription(description);

		return album;
	}

	/**
	 * method to read the album with the aid id from the database
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseException
	 */
	public AOAlbum getAlbum(int id) throws DatabaseException {

		AOAlbum album = null;

		ResultSet rs;
		try {
			rs = dbConnection.createStatement()
					.executeQuery(
							"SELECT aid, name, discription FROM album WHERE id = "
									+ id);
			while (rs.next()) {
				album = buildAlbumAO(rs);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return album;
	}

	/**
	 * method to make a ArrayList of all albums from the database
	 * 
	 * @return
	 * @throws DatabaseException
	 */
	public ArrayList<AOAlbum> getAlbums() throws DatabaseException {

		ResultSet rs;
		ArrayList<AOAlbum> list = new ArrayList<AOAlbum>();

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT * FROM album");

			while (rs.next()) {
				AOAlbum album = buildAlbumAO(rs);
				list.add(album);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return list;
	}

	/**
	 * method to build a photo object
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AOPhoto buildPhotoAO(ResultSet rs) throws SQLException {

		AOPhoto photo;

		int pid = rs.getInt("pid");
		int aid = rs.getInt("aid");
		String description = rs.getString("description");
		String origFileName = rs.getString("origFileName");
		String metadata = rs.getString("metadata");

		photo = new AOPhoto(pid, aid, origFileName);
		photo.setDescription(description);
		photo.setMetadata(metadata);

		return photo;
	}

	/**
	 * method to read the photo with the pid id from the database
	 * 
	 * @param id
	 * @return
	 * @throws DatabaseException
	 */
	public AOPhoto getPhoto(int id) throws DatabaseException {

		AOPhoto photo = null;
		ResultSet rs;

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT pid, aid, origFilename, description, metadata"
							+ " FROM photo WHERE pid = " + id);

			if (rs.next()) {
				photo = buildPhotoAO(rs);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return photo;
	}

	/**
	 * method to make a ArrayList of all photos from a album
	 * 
	 * @param album
	 * @throws DatabaseException
	 */
	public void getPhotos(AOAlbum album) throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();

		int aid = album.getId();

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT pid, aid, origFilename, description, metadata "
							+ "FROM photo WHERE aid = " + aid);

			while (rs.next()) {
				AOPhoto photo = buildPhotoAO(rs);
				list.add(photo);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		album.setPhotos(list);
	}

	/**
	 * method to make a ArrayList of photos with the number of num who are
	 * towards of the given photo in the database
	 * 
	 * @param photo
	 * @param num
	 * @return
	 * @throws DatabaseException
	 */
	public ArrayList<AOPhoto> getNextPhotos(AOPhoto photo, short num)
			throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();

		int aid = photo.getAlbumId();
		int pid = photo.getId();

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT pid, aid, origFilename, description, metadata"
							+ " FROM photo WHERE aid = " + aid + " AND pid > "
							+ pid + " ORDER BY pid ASC LIMIT 0," + num);

			while (rs.next()) {

				int id = rs.getInt(1);
				AOPhoto lPhoto = getPhoto(id);
				list.add(lPhoto);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return list;
	}

	/**
	 * method to make a ArrayList of photos with the number of num who are ahead
	 * of the given photo in the database
	 * 
	 * @param photo
	 * @param num
	 * @return
	 * @throws DatabaseException
	 */
	public ArrayList<AOPhoto> getPreviousPhotos(AOPhoto photo, short num)
			throws DatabaseException {

		ResultSet rs;
		ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();

		int aid = photo.getAlbumId();
		int pid = photo.getId();

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT pid, aid, origFilename, description, metadata"
							+ " FROM photo WHERE aid = " + aid + " AND pid < "
							+ pid + " ORDER BY pid DESC LIMIT 0," + num);

			while (rs.next()) {

				int id = rs.getInt(1);
				AOPhoto lPhoto = getPhoto(id);
				list.add(lPhoto);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return list;
	}

	/**
	 * method to build a Tag object
	 * 
	 * @param rs
	 * @return
	 * @throws SQLException
	 */
	private AOTag buildTagAO(ResultSet rs) throws SQLException {

		AOTag tag;

		int tid = rs.getInt("tid");
		String name = rs.getString("name");

		tag = new AOTag(tid, name);

		return tag;
	}

	/**
	 * method to to read all tags from a photo out of the database and put they
	 * into a ArrayList and set this ArrayList to the photo
	 * 
	 * @param photo
	 * @throws DatabaseException
	 */
	public void getTags(AOPhoto photo) throws DatabaseException {

		ResultSet rs;
		ArrayList<AOTag> list = new ArrayList<AOTag>();

		int pid = photo.getId();

		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT tag.tid, tag.name FROM tag "
							+ "INNER JOIN photo_tag "
							+ "ON tag.tid = photo_tag.tid "
							+ "WHERE photo_tag.pid = " + pid);

			while (rs.next()) {

				AOTag tag = buildTagAO(rs);
				list.add(tag);
			}
			rs.close();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		photo.setTags(list);
	}

	/**
	 * method to write a album into the database table album
	 * 
	 * @param album
	 * @throws DatabaseException
	 */
	public int addAlbum(AOAlbum album) throws DatabaseException {

		ResultSet rs;
		int newAid;
		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT MAX (aid) AS aid_max FROM Album");

			rs.next();
			newAid = 1 + rs.getInt("aid_max");

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		
		String values = " VALUES (" + newAid + ", '" + album.getName()
				+ "', '" + album.getDescription() + "')";

		System.out.println("INSERT INTO album (aid, name, description)"
				+ values);

		try {
			dbConnection.createStatement().executeUpdate(
					"INSERT INTO album (aid, name, description)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return newAid;
	}

	/**
	 * method to write a photo into the database table photo
	 * 
	 * @param photo
	 * @throws DatabaseException
	 */
	public int addPhoto(AOPhoto photo) throws DatabaseException {

		ResultSet rs;
		int newPid;
		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT MAX (pid) AS pid_max FROM Photo");

			rs.next();
			newPid = 1 + rs.getInt("pid_max");

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

		String values = "VALUES (" + newPid + "," + photo.getAlbumId() + ", '"
				+ photo.getOrigFileName() + "', '" + photo.getDescription()
				+ "', '" + photo.getMetadata() + "')";

		System.out.println("INSERT INTO photo (pid, aid, origFileName, "
				+ "description, metadata)" + values);

		try {
			dbConnection.createStatement().executeUpdate(
					"INSERT INTO photo (pid, aid, origFileName, "
							+ "description, metadata)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return newPid;
	}

	/**
	 * is it necessary???
	 * 
	 * @param album
	 * @throws DatabaseException
	 */
	public void addPhotos(ArrayList<AOPhoto> list) throws DatabaseException {
		// TODO: Noch verändern mit eigenen Query
		for (AOPhoto photo : list) {
			this.addPhoto(photo);
		}
	}

	/**
	 * method to write a tag into the database table tag
	 * 
	 * @param tag
	 * @throws DatabaseException
	 */
	public int addTag(AOTag tag) throws DatabaseException {

		ResultSet rs;
		int newTid;
		try {
			rs = dbConnection.createStatement().executeQuery(
					"SELECT MAX (tid) AS tid_max FROM tag");

			rs.next();
			newTid = 1 + rs.getInt("tid_max");

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		
		String values = "VALUES (" + newTid + ", '" + tag.getName() + "')";

		System.out.println("INSERT INTO tag (tid, name)" + values);

		try {
			dbConnection.createStatement().executeUpdate(
					"INSERT INTO tag (tid, name)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
		return newTid;
	}

	public void asignPhotoTag(AOPhoto photo, AOTag tag)
			throws DatabaseException {

		String values = "VALUES (" + photo.getId() + "," + tag.getId() + ")";

		System.out.println("INSERT INTO photo_tag (pid, tid)" + values);

		try {
			dbConnection.createStatement().executeUpdate(
					"INSERT INTO photo_tag (pid, tid)" + values);
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void deleteAlbum(AOAlbum album) throws DatabaseException {

		try {
			dbConnection.createStatement().executeUpdate(
					"DELETE FROM photo_tag WHERE pid IN (SELECT pid FROM photo where aid = "
							+ album.getId() + ")");

			dbConnection.createStatement().executeUpdate(
					"DELETE FROM photo WHERE aid = " + album.getId());

			dbConnection.createStatement().executeUpdate(
					"DELETE FROM album WHERE aid = " + album.getId());

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}

	}

	public void deletePhoto(AOPhoto photo) throws DatabaseException {

		try {
			dbConnection.createStatement().executeUpdate(
					"DELETE FROM photo_tag WHERE pid = " + photo.getId());

			dbConnection.createStatement().executeUpdate(
					"DELETE FROM photo WHERE aid = " + photo.getId());

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void deleteTag(AOTag tag) throws DatabaseException {
		try {

			dbConnection.createStatement().executeUpdate(
					"DELETE FROM tag WHERE aid = " + tag.getId());

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void updateAlbum(AOAlbum album) throws DatabaseException {
		try {

			dbConnection.createStatement().executeUpdate(
					"UPDATE album SET name = " + album.getName()
							+ ", description = " + album.getDescription()
							+ " WHERE aid = " + album.getId());

		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}

	public void commit() throws DatabaseException {
		try {
			dbConnection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e.getMessage());
		}
	}
}
