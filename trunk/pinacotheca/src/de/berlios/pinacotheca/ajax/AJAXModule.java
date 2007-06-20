package de.berlios.pinacotheca.ajax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.StringTokenizer;

import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
import de.berlios.pinacotheca.db.AOComment;
import de.berlios.pinacotheca.db.AOPhoto;
import de.berlios.pinacotheca.db.AOTag;
import de.berlios.pinacotheca.db.DatabaseException;
import de.berlios.pinacotheca.db.DatabaseHandler;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;
import de.berlios.pinacotheca.xml.XMLWriter;

public class AJAXModule extends PTResponder implements PTModule {
	private DatabaseHandler dbHandler;

	public AJAXModule() throws HTTPException {
		super();
		try {
			dbHandler = DatabaseHandler.getInstance();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private HTTPRequest request;

	public void setRequest(HTTPRequest request) {
		this.request = request;
	}

	public void handleRequest() throws HTTPException {
		String reqURL = request.getRequestURL().substring("/ajax/".length());

		if (reqURL.equals("photoinfo")) {
			returnPhotoInfo();
		} else if (reqURL.equals("taglist")) {
			returnTagList();
		} else if (reqURL.equals("tagassignments")) {
			returnTagAssignments();
		} else if (reqURL.equals("taggedphotos")) {
			returnTaggedPhotos();
		} else if(reqURL.equals("comments")) {
			returnComments();
		} else if (reqURL.equals("comment")) {
			handleCommentAddition();
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void returnComments() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		ArrayList<AOComment> comments;
		Integer pId;
		
		postVars = request.getPostVars();
		
		if(!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();
		
		try {
			pId = new Integer(postVars.get("photo").get(0));
			comments = dbHandler.getComments(pId);
			returnComments(comments);
		} catch(NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void handleCommentAddition() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		AOComment comment;
		ArrayList<AOComment> comments;
		Integer photoId;
		String name, text;

		postVars = request.getPostVars();

		if (!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();

		if (!postVars.containsKey("name"))
			throw new HTTPBadRequestException();

		if (!postVars.containsKey("text"))
			throw new HTTPBadRequestException();

		try {
			name = postVars.get("name").get(0);
			text = postVars.get("text").get(0);
			photoId = new Integer(postVars.get("photo").get(0));
			dbHandler.addComment(photoId, name, text);
			dbHandler.commit();
			comment = new AOComment(-1); // comment id doesn't matter here
			comment.setPId(photoId);
			comment.setName(name);
			comment.setText(text);
			comments = new ArrayList<AOComment>();
			comments.add(comment);
			returnComments(comments);
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnComments(ArrayList<AOComment> comments) throws HTTPException {
		try {
    		ByteArrayOutputStream oStream = new ByteArrayOutputStream();
    		XMLWriter writer = new XMLWriter(oStream);
    		HashMap<String, String> attributes = new HashMap<String, String>();
    		ByteArrayInputStream stream;
    		
    		writer.addNode("responseData");
    		
    		for(AOComment comment : comments) {
    			attributes.put("id", String.valueOf(comment.getCId()));
    			attributes.put("name", comment.getName());
    			attributes.put("text", comment.getText());
    			writer.addNode("comment", true, attributes);
    		}
    		
    		writer.closeNode();
    		oStream.close();
    		stream = new ByteArrayInputStream(oStream.toByteArray());
    		returnXMLStream(stream);
		} catch(IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnTaggedPhotos() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;

		postVars = request.getPostVars();

		if (!postVars.containsKey("tag"))
			throw new HTTPBadRequestException();

		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			XMLWriter writer = new XMLWriter(oStream);
			Integer tId = new Integer(postVars.get("tag").get(0));
			HashMap<String, String> attributes = new HashMap<String, String>();
			ArrayList<AOPhoto> photos = dbHandler.getPhotosByTag(tId);
			ByteArrayInputStream stream;

			attributes.put("id", tId.toString());
			writer.addNode("responsedata", false, attributes);

			for (AOPhoto photo : photos) {
				attributes.put("id", String.valueOf(photo.getPId()));
				attributes.put("name", photo.getFilename());
				writer.addNode("photo", true, attributes);
			}

			writer.closeNode();
			oStream.close();
			stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}

	}

	private void returnTagAssignments() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;

		postVars = request.getPostVars();

		if (!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();

		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("photo").get(0));
			AOPhoto photo = dbHandler.getPhotoById(photoId);
			ArrayList<AOTag> allTags = dbHandler.getTags();
			HashMap<String, String> attributes = new HashMap<String, String>();
			ByteArrayInputStream stream;
			ArrayList<AOTag> assignedTags;
			XMLWriter writer;

			dbHandler.getTags(photo);
			assignedTags = photo.getTags();

			if (photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());

			writer = new XMLWriter(oStream);
			writer.addNode("responsedata");

			for (AOTag tag : assignedTags) {
				attributes.put("id", String.valueOf(tag.getTId()));
				attributes.put("name", tag.getName());
				writer.addNode("assignedtag", true, attributes);
			}

			for (AOTag tag : allTags) {
				if (assignedTags.contains(tag))
					continue;
				attributes.put("id", String.valueOf(tag.getTId()));
				attributes.put("name", tag.getName());
				writer.addNode("unassignedtag", true, attributes);
			}

			writer.closeNode();
			oStream.close();
			stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnTagList() throws HTTPException {
		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			ArrayList<AOTag> tags = dbHandler.getTags();
			XMLWriter writer = new XMLWriter(oStream);
			HashMap<String, String> attributes = new HashMap<String, String>();
			ByteArrayInputStream stream;

			writer.addNode("responsedata");

			for (AOTag tag : tags) {
				attributes.put("id", String.valueOf(tag.getTId()));
				attributes.put("name", tag.getName());
				writer.addNode("tag", true, attributes);
			}

			writer.closeNode();
			oStream.close();
			stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnPhotoInfo() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;

		postVars = request.getPostVars();

		if (!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();

		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("photo").get(0)), delim;
			AOPhoto photo = dbHandler.getPhotoById(photoId);
			if(photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());
			ArrayList<AOPhoto> nextPhotos = dbHandler.getNextPhotos(photo, (short) 1);
			ArrayList<AOPhoto> prevPhotos = dbHandler.getPreviousPhotos(photo, (short) 1);
			HashMap<String, String> attributes = new HashMap<String, String>();
			XMLWriter writer;
			ByteArrayInputStream stream;
			StringTokenizer metadataTokenizer;
			String token;

			if (photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());

			writer = new XMLWriter(oStream);
			writer.addNode("responsedata", false);
			writer.addNode("name");
			writer.setNodeValue(photo.getFilename());
			writer.closeNode();
			writer.addNode("description");
			writer.setNodeValue(photo.getDescription());
			writer.closeNode();
			writer.addNode("next");

			if (nextPhotos.size() > 0)
				writer.setNodeValue(String.valueOf(nextPhotos.get(0).getPId()));
			else
				writer.setNodeValue("-1");

			writer.closeNode();
			writer.addNode("prev");

			if (prevPhotos.size() > 0)
				writer.setNodeValue(String.valueOf(prevPhotos.get(0).getPId()));
			else
				writer.setNodeValue("-1");

			metadataTokenizer = new StringTokenizer(photo.getMetadata(), "\n");

			while (metadataTokenizer.hasMoreTokens()) {
				token = metadataTokenizer.nextToken();
				delim = token.indexOf(':');

				if (delim == -1)
					continue; // we ignore this for now

				if (token.substring(0, delim).equals("Date/Time")) {
					writer.addNode("date");
					writer.setNodeValue(token.substring(delim + 1));
					writer.closeNode();
				} else {
					attributes.put("type", token.substring(0, delim));
					writer.addNode("metadata", false, attributes);
					writer.setNodeValue(token.substring(delim + 1));
					writer.closeNode();
				}
			}

			writer.closeNode();
			writer.closeNode();
			oStream.close();
			stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch (NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	public void setSecureConnection(boolean isSecure) {
		// not needed here
	}

	protected HTTPRequest getRequest() {
		return request;
	}
}
