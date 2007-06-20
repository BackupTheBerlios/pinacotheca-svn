package de.berlios.pinacotheca.db;

public class SQLQueries {
	private final static String DB_SCHEMA =
		"pinacotheca";

	protected final static String SET_SCHEMA =
		"SET SCHEMA " + DB_SCHEMA;

	////////////////////
	// CREATE QUERIES //
	////////////////////
	protected final static String CREATE_SCHEMA =
		"CREATE SCHEMA " + DB_SCHEMA;

	protected final static String CREATE_TABLE_ALBUMS =
		"CREATE TABLE " + DB_SCHEMA + ".albums (aid INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(100), "
			+ "description LONG VARCHAR, authkey CHAR(20))";

	protected final static String CREATE_TABLE_PHOTOS =
		"CREATE TABLE " + DB_SCHEMA + ".photos (pid INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, aid INTEGER REFERENCES " + DB_SCHEMA
			+ ".albums, filename VARCHAR(100), description LONG VARCHAR, metadata LONG VARCHAR )";

	protected final static String CREATE_TABLE_TAGS =
		"CREATE TABLE " + DB_SCHEMA + ".tags (tid INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, name VARCHAR(100) )";

	protected final static String CREATE_TABLE_PHOTO_TAGS =
		"CREATE TABLE " + DB_SCHEMA + ".photo_tags (pid INTEGER REFERENCES " + DB_SCHEMA + ".photos, "
			+ "tid INTEGER REFERENCES " + DB_SCHEMA + ".tags, PRIMARY KEY (pid, tid) )";

	protected final static String CREATE_TABLE_PHOTO_COMMENTS =
		"CREATE TABLE " + DB_SCHEMA + ".photo_comments (cid INTEGER GENERATED ALWAYS AS IDENTITY PRIMARY KEY, pid INTEGER REFERENCES "
			+ DB_SCHEMA + ".photos, " + " name VARCHAR(100), " + "text LONG VARCHAR)";

	////////////////////
	// INSERT QUERIES //
	////////////////////
	protected final static String INSERT_ALBUM =
		"INSERT INTO albums (name, description, authkey) VALUES (?, ?, ?)";
	
	protected final static String INSERT_COMMENT =
		"INSERT INTO photo_comments (pid, name, text) VALUES (?, ?, ?)";
	
	protected final static String INSERT_PHOTO =
		"INSERT INTO photos (aid, filename, description, metadata) VALUES (?, ?, ?, ?)";
	
	protected final static String INSERT_TAG =
		"INSERT INTO tags (name) VALUES (?)";
	
	protected static final String INSERT_PHOTO_TAG = 
		"INSERT INTO photo_tags (pid, tid) VALUES (?, ?)";

	////////////////////
	// SELECT QUERIES //
	////////////////////
	protected static final String SELECT_MAX_ALBUM = 
		"SELECT MAX(aid) as max_val FROM albums";

	protected static final String SELECT_MAX_PHOTO =
		"SELECT MAX(pid) as max_val FROM photos";
	
	protected static final String SELECT_ALL_ALBUMS =
		"SELECT aid, name, description, authkey FROM albums";

	protected static final String SELECT_ALBUM_BY_ID =
		"SELECT aid, name, description, authkey FROM albums WHERE aid = ?";

	protected static final String SELECT_ALBUM_BY_AUTHKEY = 
		"SELECT aid, name, description, authkey FROM albums WHERE authkey = ?";

	protected static final String SELECT_COMMENTS =
		"SELECT cid, pid, name, text FROM photo_comments WHERE pid = ?";
	
	protected static final String SELECT_COMMENT =
		"SELECT cid, pid, name, text FROM photo_comments WHERE cid = ?";

	protected static final String SELECT_PHOTOS_BY_ID = 
		"SELECT pid, aid, filename, description, metadata FROM photos WHERE aid = ?";
	
	protected static final String SELECT_PHOTOS_BY_TAG =
		"SELECT photos.pid, photos.aid, photos.filename, photos.description, photos.metadata FROM photos, photo_tags, albums WHERE "
			+ "photos.aid = albums.aid AND LTRIM(albums.authkey) = '' AND photos.pid = photo_tags.pid AND photo_tags.tid = ?";

	protected static final String SELECT_NEXT_PHOTOS = 
		"SELECT pid, aid, filename, description, metadata FROM photos WHERE aid = ? AND pid > ? ORDER BY pid ASC";

	protected static final String SELECT_PREVIOUS_PHOTOS =
		"SELECT pid, aid, filename, description, metadata FROM photos WHERE aid = ? AND pid < ? ORDER BY pid DESC";

	protected static final String SELECT_PHOTO =
		"SELECT pid, aid, filename, description, metadata FROM photos WHERE pid = ?";
	
	protected static final String SELECT_TAGS =
		"SELECT tid, name FROM tags";
	
	protected static final String SELECT_TAG =
		"SELECT tid, name FROM tags WHERE tid = ?";
	
	protected static final String SELECT_TAG_BY_PHOTO =
		"SELECT tags.tid, tags.name FROM tags, photo_tags WHERE tags.tid = photo_tags.tid AND photo_tags.pid = ?";
	
	protected static final String SELECT_PHOTO_TAG =
		"SELECT pid, tid FROM photo_tags WHERE pid = ? AND tid = ?";

	////////////////////
	// DELETE QUERIES //
	////////////////////
	protected static final String DELETE_PHOTO_TAG =
		"DELETE FROM photo_tags WHERE pid = ? AND tid = ?";
	
	protected static final String DELETE_PHOTO_TAGS_BY_ALBUM =
		"DELETE FROM photo_tags WHERE pid IN (SELECT pid FROM photos WHERE aid = ?)";
	
	protected static final String DELETE_PHOTO_TAGS_BY_TAG =
		"DELETE FROM photo_tags WHERE tid = ?";
	
	protected static final String DELETE_PHOTOS_BY_ALBUM =
		"DELETE FROM photos WHERE aid = ?";
	
	protected static final String DELETE_ALBUM =
		"DELETE FROM albums WHERE aid = ?";
	
	protected static final String DELETE_PHOTO_TAGS_BY_PHOTO =
		"DELETE FROM photo_tags WHERE pid = ?";
	
	protected static final String DELETE_PHOTO =
		"DELETE FROM photos WHERE pid = ?";
	
	protected static final String DELETE_TAG =
		"DELETE FROM tags WHERE tid = ?";
	
	protected static final String DELETE_COMMENT =
		"DELETE FROM photo_comments WHERE cid = ?";
	
	protected static final String DELETE_COMMENTS_BY_PHOTO =
		"DELETE FROM photo_comments WHERE pid = ?";
	
	protected static final String DELETE_COMMENTS_BY_ALBUM =
		"DELETE FROM photo_comments WHERE pid IN (SELECT pid FROM photos WHERE aid = ?)";

	////////////////////
	// UPDATE QUERIES //
	////////////////////
	protected static final String UPDATE_ALBUM =
		"UPDATE albums SET name = ?, description = ?, authkey = ? WHERE aid = ?";
	
	protected static final String UPDATE_PHOTO =
		"UPDATE photos SET description = ?, metadata = ? WHERE pid = ?";
	
	protected static final String UPDATE_TAG =
		"UPDATE tags SET name = ? WHERE tid = ?";
}
