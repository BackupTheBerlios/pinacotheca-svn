package de.berlios.pinacotheca.db;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;

import de.berlios.pinacotheca.PTConfiguration;

public class DatabaseHandler {

	private Connection dbConnection;

	private PreparedStatement insertAlbum;

	private PreparedStatement insertComment;

	private PreparedStatement insertPhoto;

	private PreparedStatement insertTag;

	private PreparedStatement selectAlbumById;

	private PreparedStatement selectAlbumByAuthkey;

	private PreparedStatement selectComments;

	private PreparedStatement selectPhotosById;

	private PreparedStatement selectNextPhotos;

	private PreparedStatement selectPreviousPhotos;

	private PreparedStatement selectPhoto;

	private PreparedStatement selectPhotosByTag;

	private PreparedStatement selectTag;

	private PreparedStatement insertPhotoTag;

	private PreparedStatement selectPhotoTag;

	private PreparedStatement deletePhotoTag;

	private PreparedStatement deletePhotoTagsByAlbum;

	private PreparedStatement deletePhotosByAlbum;

	private PreparedStatement deleteAlbum;

	private PreparedStatement deletePhotoTagsByPhoto;

	private PreparedStatement deletePhoto;

	private PreparedStatement deletePhotoTagsByTag;

	private PreparedStatement deleteTag;

	private PreparedStatement updateAlbum;

	private PreparedStatement updatePhoto;

	private PreparedStatement updateTag;

	private PreparedStatement selectTagByPhoto;

	private PreparedStatement selectComment;

	private PreparedStatement deleteComment;

	private PreparedStatement deleteCommentsByPhoto;

	private PreparedStatement deleteCommentsByAlbum;

	private static DatabaseHandler instance;

	private DatabaseHandler() throws DatabaseException {
		initDatabase();
	}

	/**
	 * Initializes the database and creates the tables if needed.
	 * 
	 * @throws DatabaseException
	 */
	private void initDatabase() throws DatabaseException {
		File dbDir = new File(PTConfiguration.getServerRoot(), "db");
		File testFile = new File(dbDir, "pinacothecaDB");
		boolean createTables = !testFile.isDirectory();
		Statement stmt;

		System.setProperty("derby.system.home", dbDir.getPath());

		try {
			Class.forName("org.apache.derby.jdbc.EmbeddedDriver");
			dbConnection = DriverManager.getConnection("jdbc:derby:pinacothecaDB;create=true");
			dbConnection.setAutoCommit(false);
			stmt = dbConnection.createStatement();

			if (createTables) {
				stmt.executeUpdate(SQLQueries.CREATE_SCHEMA);
				// TODO: make authkey unique
				stmt.executeUpdate(SQLQueries.CREATE_TABLE_ALBUMS);
				stmt.executeUpdate(SQLQueries.CREATE_TABLE_PHOTOS);
				stmt.executeUpdate(SQLQueries.CREATE_TABLE_TAGS);
				stmt.executeUpdate(SQLQueries.CREATE_TABLE_PHOTO_TAGS);
				stmt.executeUpdate(SQLQueries.CREATE_TABLE_PHOTO_COMMENTS);
				dbConnection.commit();
			}

			stmt.executeUpdate(SQLQueries.SET_SCHEMA);
			initInsertStatements();
			initSelectStatements();
			initDeleteStatements();
			initUpdateStatements();
		} catch (ClassNotFoundException e) {
			throw new DatabaseException(e);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Initializes the prepared statements for insert queries.
	 * 
	 * @throws SQLException
	 */
	private void initInsertStatements() throws SQLException {
		insertAlbum = dbConnection.prepareStatement(SQLQueries.INSERT_ALBUM);
		insertComment = dbConnection.prepareStatement(SQLQueries.INSERT_COMMENT);
		insertPhoto = dbConnection.prepareStatement(SQLQueries.INSERT_PHOTO);
		insertTag = dbConnection.prepareStatement(SQLQueries.INSERT_TAG);
		insertPhotoTag = dbConnection.prepareStatement(SQLQueries.INSERT_PHOTO_TAG);
	}

	/**
	 * Initializes the prepared statements for select queries.
	 * 
	 * @throws SQLException
	 */
	private void initSelectStatements() throws SQLException {
		selectAlbumById = dbConnection.prepareStatement(SQLQueries.SELECT_ALBUM_BY_ID);
		selectAlbumByAuthkey = dbConnection.prepareStatement(SQLQueries.SELECT_ALBUM_BY_AUTHKEY);
		selectComments = dbConnection.prepareStatement(SQLQueries.SELECT_COMMENTS);
		selectComment = dbConnection.prepareStatement(SQLQueries.SELECT_COMMENT);
		selectPhotosById = dbConnection.prepareStatement(SQLQueries.SELECT_PHOTOS_BY_ID);
		selectPhotosByTag = dbConnection.prepareStatement(SQLQueries.SELECT_PHOTOS_BY_TAG);
		selectNextPhotos = dbConnection.prepareStatement(SQLQueries.SELECT_NEXT_PHOTOS);
		selectPreviousPhotos = dbConnection.prepareStatement(SQLQueries.SELECT_PREVIOUS_PHOTOS);
		selectPhoto = dbConnection.prepareStatement(SQLQueries.SELECT_PHOTO);
		selectTag = dbConnection.prepareStatement(SQLQueries.SELECT_TAG);
		selectTagByPhoto = dbConnection.prepareStatement(SQLQueries.SELECT_TAG_BY_PHOTO);
		selectPhotoTag = dbConnection.prepareStatement(SQLQueries.SELECT_PHOTO_TAG);
	}

	/**
	 * Initializes the prepared statements for delete queries.
	 * 
	 * @throws SQLException
	 */
	private void initDeleteStatements() throws SQLException {
		deletePhotoTag = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTO_TAG);
		deletePhotoTagsByAlbum = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTO_TAGS_BY_ALBUM);
		deletePhotoTagsByPhoto = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTO_TAGS_BY_PHOTO);
		deletePhotoTagsByTag = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTO_TAGS_BY_TAG);
		deletePhotosByAlbum = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTOS_BY_ALBUM);
		deleteAlbum = dbConnection.prepareStatement(SQLQueries.DELETE_ALBUM);
		deletePhoto = dbConnection.prepareStatement(SQLQueries.DELETE_PHOTO);
		deleteTag = dbConnection.prepareStatement(SQLQueries.DELETE_TAG);
		deleteComment = dbConnection.prepareStatement(SQLQueries.DELETE_COMMENT);
		deleteCommentsByPhoto = dbConnection.prepareStatement(SQLQueries.DELETE_COMMENTS_BY_PHOTO);
		deleteCommentsByAlbum = dbConnection.prepareStatement(SQLQueries.DELETE_COMMENTS_BY_ALBUM);
	}

	private void initUpdateStatements() throws SQLException {
		updateAlbum = dbConnection.prepareStatement(SQLQueries.UPDATE_ALBUM);
		updatePhoto = dbConnection.prepareStatement(SQLQueries.UPDATE_PHOTO);
		updateTag = dbConnection.prepareStatement(SQLQueries.UPDATE_TAG);
	}

	/**
	 * Get the maximum id of a table, the query has to be supplied as parameter
	 * <em>query</em>. The query must define a field max_value which will be
	 * returned. The type of the field is integer.
	 * 
	 * @param query
	 *            The query to get the maximum id.
	 * @return The maximum id;
	 * @throws SQLException
	 */
	private int getMaxVal(String query) throws SQLException {
		ResultSet rs = dbConnection.createStatement().executeQuery(query);

		rs.next();
		return rs.getInt("max_val");
	}

	/**
	 * Creates a new AOAlbum object and fills it with the data the ResultSet
	 * <em>rs</em> provides;
	 * 
	 * @param rs
	 *            The ResultSet to build the AO from.
	 * @return The newly created AO.
	 * @throws SQLException
	 */
	private AOAlbum buildAlbumAO(ResultSet rs) throws SQLException {
		AOAlbum album;
		int aId = rs.getInt("aid");
		String name = rs.getString("name");
		String description = rs.getString("description");
		String authkey = rs.getString("authkey");

		album = new AOAlbum(aId);
		album.setName(name);
		album.setDescription(description);
		album.setAuthkey(authkey);

		return album;
	}

	/**
	 * Creates a new AOComment object and fills it with the data the ResultSet
	 * <em>rs</em> provides;
	 * 
	 * @param rs
	 *            The ResultSet to build the AO from.
	 * @return The newly created AO.
	 * @throws SQLException
	 */
	private AOComment buildCommentAO(ResultSet rs) throws SQLException {
		AOComment comment;
		int cId = rs.getInt("cid");
		int pId = rs.getInt("pid");
		String name = rs.getString("name");
		String text = rs.getString("text");

		comment = new AOComment(cId);
		comment.setName(name);
		comment.setText(text);
		comment.setPId(pId);

		return comment;
	}

	/**
	 * Creates a new AOPhoto object and fills it with the data the ResultSet
	 * <em>rs</em> provides;
	 * 
	 * @param rs
	 *            The ResultSet to build the AO from.
	 * @return The newly created AO.
	 * @throws SQLException
	 */
	private AOPhoto buildPhotoAO(ResultSet rs) throws SQLException {
		AOPhoto photo;
		int pid = rs.getInt("pid");
		int aId = rs.getInt("aid");
		String filename = rs.getString("filename");
		String description = rs.getString("description");
		String metadata = rs.getString("metadata");

		photo = new AOPhoto(pid);
		photo.setAId(aId);
		photo.setFilename(filename);
		photo.setDescription(description);
		photo.setMetadata(metadata);

		return photo;
	}

	/**
	 * Creates a new AOTag object and fills it with the data the ResultSet
	 * <em>rs</em> provides;
	 * 
	 * @param rs
	 *            The ResultSet to build the AO from.
	 * @return The newly created AO.
	 * @throws SQLException
	 */
	private AOTag buildTagAO(ResultSet rs) throws SQLException {
		AOTag tag;
		int tId = rs.getInt("tid");
		String name = rs.getString("name");

		tag = new AOTag(tId);
		tag.setName(name);

		return tag;
	}

	/**
	 * Adds a new Album to the database with the given data. To leave a part of
	 * the Album empty just provide an empty String as the value.
	 * 
	 * @param name
	 *            The name of the new Album.
	 * @param description
	 *            The description for the new Album.
	 * @param authkey
	 *            The authorization key for the new Album.
	 * @throws DatabaseException
	 */
	public synchronized int addAlbum(String name, String description, String authkey) throws DatabaseException {
		try {
			insertAlbum.setString(1, name);
			insertAlbum.setString(2, description);
			insertAlbum.setString(3, authkey);
			insertAlbum.executeUpdate();
			return getMaxVal(SQLQueries.SELECT_MAX_ALBUM);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Adds a new Comment to the database with the given data. To leave a part
	 * of the Comment empty just provide an empty String as the value.
	 * 
	 * @param pId
	 *            The id of the photo this comment is assigned to.
	 * @param name
	 *            The author's name of the comment.
	 * @param text
	 *            The comment text.
	 * @throws DatabaseException
	 */
	public void addComment(int pId, String name, String text) throws DatabaseException {
		try {
			insertComment.setInt(1, pId);
			insertComment.setString(2, name);
			insertComment.setString(3, text);
			insertComment.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Adds a new photo to the database with the given data. To leave a part of
	 * the photo empty, just provide an empty String as the value.
	 * 
	 * @param aId
	 *            The id of the album to which the photo should be added.
	 * @param filename
	 *            The original filename the photo had before it was uploaded.
	 * @param description
	 *            The description of the photo.
	 * @param metadata
	 *            The metadata that was extracted from the photo.
	 * @throws DatabaseException
	 */
	public synchronized int addPhoto(int aId, String filename, String description, String metadata) throws DatabaseException {
		try {
			insertPhoto.setInt(1, aId);
			insertPhoto.setString(2, filename);
			insertPhoto.setString(3, description);
			insertPhoto.setString(4, metadata);
			insertPhoto.executeUpdate();
			return getMaxVal(SQLQueries.SELECT_MAX_PHOTO);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Add a new tag assignment with tag <em>tId</em> with photo <em>pId</em>.
	 * 
	 * @param pId
	 *            The photo id.
	 * @param tId
	 *            The tag id.
	 * @throws DatabaseException
	 */
	public void addPhotoTag(int pId, int tId) throws DatabaseException {
		try {
			ResultSet rs;

			selectPhotoTag.setInt(1, pId);
			selectPhotoTag.setInt(2, tId);
			rs = selectPhotoTag.executeQuery();

			if (rs.next())
				return;

			insertPhotoTag.setInt(1, pId);
			insertPhotoTag.setInt(2, tId);
			insertPhotoTag.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Adds a new tag to the database with the given name. Leaving this name
	 * empty makes no sense, so just don't to it.
	 * 
	 * @param name
	 *            The name of this tag.
	 * @throws DatabaseException
	 */
	public void addTag(String name) throws DatabaseException {
		try {
			insertTag.setString(1, name);
			insertTag.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available albums.
	 * 
	 * @return An ArrayList which contains the albums.
	 * @throws DatabaseException
	 */
	public ArrayList<AOAlbum> getAlbums() throws DatabaseException {
		try {
			ArrayList<AOAlbum> list = new ArrayList<AOAlbum>();
			AOAlbum album;
			ResultSet rs;

			rs = dbConnection.createStatement().executeQuery(SQLQueries.SELECT_ALL_ALBUMS);

			while (rs.next()) {
				album = buildAlbumAO(rs);
				list.add(album);
			}

			rs.close();
			return list;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get an Album identified by <em>aId</em>.
	 * 
	 * @param aId
	 *            The album id.
	 * @return The album or null if the database doesn't contain an album with
	 *         this id.
	 * @throws DatabaseException
	 */
	public AOAlbum getAlbumById(int aId) throws DatabaseException {
		try {
			AOAlbum album = null;
			ResultSet rs;

			selectAlbumById.setInt(1, aId);
			rs = selectAlbumById.executeQuery();

			if (rs.next()) {
				album = buildAlbumAO(rs);
			}

			rs.close();
			return album;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get an Album identified by the albums authkey.
	 * 
	 * @param authkey
	 *            The albums authkey.
	 * @return The album or null if the database doesn't contain an album with
	 *         this authkey.
	 * @throws DatabaseException
	 */
	public AOAlbum getAlbumByAuthkey(String authkey) throws DatabaseException {
		try {
			AOAlbum album = null;
			ResultSet rs;

			selectAlbumByAuthkey.setString(1, authkey);
			rs = selectAlbumByAuthkey.executeQuery();

			if (rs.next()) {
				album = buildAlbumAO(rs);
			}

			rs.close();
			return album;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available comments from the database which are assigned to a
	 * specified photo.
	 * 
	 * @param pId
	 *            The photos' id.
	 * @return An ArrayList which contains the comments.
	 * @throws DatabaseException
	 */
	public ArrayList<AOComment> getComments(int pId) throws DatabaseException {
		try {
			ArrayList<AOComment> comments = new ArrayList<AOComment>();
			AOComment comment;
			ResultSet rs;

			selectComments.setInt(1, pId);
			rs = selectComments.executeQuery();

			while (rs.next()) {
				comment = buildCommentAO(rs);
				comments.add(comment);
			}

			return comments;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get a comment identified by <em>cId</em>.
	 * 
	 * @param cId
	 *            The comment id.
	 * @return The comment or null if the comment doesn't exist in the database.
	 * @throws DatabaseException
	 */
	public AOComment getComment(int cId) throws DatabaseException {
		try {
			AOComment comment = null;
			ResultSet rs;

			selectComment.setInt(1, cId);
			rs = selectComment.executeQuery();

			if (rs.next()) {
				comment = buildCommentAO(rs);
			}

			rs.close();
			return comment;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available photos for <em>album<em> and automatically assign
	 * them.
	 * 
	 * @param album
	 *            The album for which the photos should bet retrieved.
	 * @throws DatabaseException
	 */
	public void getPhotos(AOAlbum album) throws DatabaseException {
		try {
			ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();
			ResultSet rs;

			selectPhotosById.setInt(1, album.getAId());
			rs = selectPhotosById.executeQuery();

			while (rs.next()) {
				AOPhoto photo = buildPhotoAO(rs);
				list.add(photo);
			}

			rs.close();
			album.setPhotos(list);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available photos which are assigned to the tag with id
	 * <em>tId</em>
	 * 
	 * @param tId
	 *            The tag id.
	 * @return An ArrayList which contains the photos.
	 * @throws DatabaseException
	 */
	public ArrayList<AOPhoto> getPhotosByTag(int tId) throws DatabaseException {
		try {
			ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();
			AOPhoto photo;
			ResultSet rs;

			selectPhotosByTag.setInt(1, tId);
			rs = selectPhotosByTag.executeQuery();

			while (rs.next()) {
				photo = buildPhotoAO(rs);
				list.add(photo);
			}

			rs.close();
			return list;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get the <em>num</em> next photos from the database which have a higher
	 * photo id than the given photo.
	 * 
	 * @param photo
	 *            The photo for which the following photos should be retrieved.
	 * @param num
	 *            The number of photos that should be retrieved at most.
	 * @return An ArrayList which contains the photos.
	 * @throws DatabaseException
	 */
	public ArrayList<AOPhoto> getNextPhotos(AOPhoto photo, short num) throws DatabaseException {
		try {
			ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();
			AOPhoto listPhoto;
			ResultSet rs;
			int pId;

			selectNextPhotos.setInt(1, photo.getAId());
			selectNextPhotos.setInt(2, photo.getPId());
			selectNextPhotos.setMaxRows(num);
			rs = selectNextPhotos.executeQuery();

			while (rs.next()) {
				pId = rs.getInt(1);
				listPhoto = getPhotoById(pId);
				list.add(listPhoto);
			}

			rs.close();
			return list;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get the <em>num</em> previous photos from the database which have a
	 * lower photo id than the given photos.
	 * 
	 * @param photo
	 *            The photo for which the previous photos should be retrieved.
	 * @param num
	 *            The number of photos that should be retrieved at most.
	 * @return An ArrayList which contains the photos.
	 * @throws DatabaseException
	 */
	public ArrayList<AOPhoto> getPreviousPhotos(AOPhoto photo, short num) throws DatabaseException {
		try {
			ArrayList<AOPhoto> list = new ArrayList<AOPhoto>();
			ResultSet rs;
			AOPhoto listPhoto;
			int pId;

			selectPreviousPhotos.setInt(1, photo.getAId());
			selectPreviousPhotos.setInt(2, photo.getPId());
			selectPreviousPhotos.setMaxRows(num);
			rs = selectPreviousPhotos.executeQuery();

			while (rs.next()) {
				pId = rs.getInt(1);
				listPhoto = getPhotoById(pId);
				list.add(listPhoto);
			}

			rs.close();
			return list;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get a photo identified by <em>pId</em>.
	 * 
	 * @param pId
	 *            The photo id.
	 * @return The photo or null if the database doesn't contain a photo with
	 *         this id.
	 * @throws DatabaseException
	 */
	public AOPhoto getPhotoById(int pId) throws DatabaseException {
		try {
			AOPhoto photo = null;
			ResultSet rs;

			selectPhoto.setInt(1, pId);
			rs = selectPhoto.executeQuery();

			if (rs.next()) {
				photo = buildPhotoAO(rs);
			}

			rs.close();
			return photo;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available tags.
	 * 
	 * @return An ArrayList which contains the tags.
	 * @throws DatabaseException
	 */
	public ArrayList<AOTag> getTags() throws DatabaseException {
		try {
			ArrayList<AOTag> list = new ArrayList<AOTag>();
			ResultSet rs = dbConnection.createStatement().executeQuery(SQLQueries.SELECT_TAGS);
			AOTag tag;

			while (rs.next()) {
				tag = buildTagAO(rs);
				list.add(tag);
			}

			rs.close();
			return list;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get all available tags for <em>photo<em> and automatically assign
	 * them.
	 * 
	 * @param photo The photo for which the tags should be retrieved.
	 * @throws DatabaseException
	 */
	public void getTags(AOPhoto photo) throws DatabaseException {
		try {
			ArrayList<AOTag> list = new ArrayList<AOTag>();
			ResultSet rs;
			AOTag tag;

			selectTagByPhoto.setInt(1, photo.getPId());
			rs = selectTagByPhoto.executeQuery();

			while (rs.next()) {
				tag = buildTagAO(rs);
				list.add(tag);
			}

			rs.close();
			photo.setTags(list);
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Get a tag identified by <em>tId</em>.
	 * 
	 * @param tId
	 *            The tag id.
	 * @return The tag or null if the database doesn't contain a tag with this
	 *         id.
	 * @throws DatabaseException
	 */
	public AOTag getTagById(int tId) throws DatabaseException {
		try {
			AOTag tag = null;
			ResultSet rs;
			selectTag.setInt(1, tId);
			rs = selectTag.executeQuery();

			if (rs.next()) {
				tag = buildTagAO(rs);
			}

			rs.close();
			return tag;
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete the photo tag assignment of photo with id <em>pId</em> to tag
	 * with id <em>tId</em>.
	 * 
	 * @param pId
	 *            The photo id.
	 * @param tId
	 *            The tag id.
	 * @throws DatabaseException
	 */
	public void deletePhotoTag(int pId, int tId) throws DatabaseException {
		try {
			deletePhotoTag.setInt(1, pId);
			deletePhotoTag.setInt(2, tId);
			deletePhotoTag.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete an album and all relationships. This means that all photo to tag
	 * assignments, photo comments and photos which are related to this album
	 * will be deleted too.
	 * 
	 * @param aId
	 *            The album id.
	 * @throws DatabaseException
	 */
	public void deleteAlbum(int aId) throws DatabaseException {
		try {
			deletePhotoTagsByAlbum.setInt(1, aId);
			deletePhotoTagsByAlbum.executeUpdate();
			deleteCommentsByAlbum.setInt(1, aId);
			deleteCommentsByAlbum.executeUpdate();
			deletePhotosByAlbum.setInt(1, aId);
			deletePhotosByAlbum.executeUpdate();
			deleteAlbum.setInt(1, aId);
			deleteAlbum.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete a comment.
	 * 
	 * @param id
	 *            The comment id.
	 * @throws DatabaseException
	 */
	public void deleteComment(int cId) throws DatabaseException {
		try {
			deleteComment.setInt(1, cId);
			deleteComment.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete a photo and all relationsships. This means that alls photo to tag
	 * assignments for this photo will be deleted too.
	 * 
	 * @param pId
	 *            The photo id.
	 * @throws DatabaseException
	 */
	public void deletePhoto(int pId) throws DatabaseException {
		try {
			deletePhotoTagsByPhoto.setInt(1, pId);
			deletePhotoTagsByPhoto.executeUpdate();
			deleteCommentsByPhoto.setInt(1, pId);
			deleteCommentsByPhoto.executeUpdate();
			deletePhoto.setInt(1, pId);
			deletePhoto.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Delete a tag with id <em>tId</em> and all photo tag assignments with
	 * this tag.
	 * 
	 * @param tId
	 *            The tag id.
	 * @throws DatabaseException
	 */
	public void deleteTag(int tId) throws DatabaseException {
		try {
			deletePhotoTagsByTag.setInt(1, tId);
			deletePhotoTagsByTag.executeUpdate();
			deleteTag.setInt(1, tId);
			deleteTag.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Update details of an album.
	 * 
	 * @param album
	 *            The album.
	 * @throws DatabaseException
	 */
	public void updateAlbum(AOAlbum album) throws DatabaseException {
		try {
			updateAlbum.setString(1, album.getName());
			updateAlbum.setString(2, album.getDescription());
			updateAlbum.setString(3, album.getAuthkey());
			updateAlbum.setInt(4, album.getAId());
			updateAlbum.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Update details of a photo.
	 * 
	 * @param photo
	 *            The photo.
	 * @throws DatabaseException
	 */
	public void updatePhoto(AOPhoto photo) throws DatabaseException {
		try {
			updatePhoto.setString(1, photo.getDescription());
			updatePhoto.setString(2, photo.getMetadata());
			updatePhoto.setInt(3, photo.getPId());
			updatePhoto.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Update details of a tag.
	 * 
	 * @param The
	 *            tag.
	 * @throws DatabaseException
	 */
	public void updateTag(AOTag tag) throws DatabaseException {
		try {
			updateTag.setString(1, tag.getName());
			updateTag.setInt(2, tag.getTId());
			updateTag.executeUpdate();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Commits all changes to the database. Without calling this, every change
	 * will be temporary.
	 * 
	 * @throws DatabaseException
	 */
	public void commit() throws DatabaseException {
		try {
			dbConnection.commit();
		} catch (SQLException e) {
			throw new DatabaseException(e);
		}
	}

	/**
	 * Initialize the singleton instance.
	 * 
	 * @throws DatabaseException
	 */
	public static void init() throws DatabaseException {
		if (instance == null)
			instance = new DatabaseHandler();
	}

	/**
	 * Get the singleton instance of the DatabaseHandler.
	 * 
	 * @return The instance.
	 * @throws DatabaseException
	 */
	public static DatabaseHandler getInstance() throws DatabaseException {
		if (instance == null)
			init();
		return instance;
	}
}
