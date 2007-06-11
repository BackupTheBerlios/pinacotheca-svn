package de.berlios.pinacotheca.ajax;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.PTResponder;
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
		
		if(reqURL.equals("nextphoto")) {
			returnNextPhoto();
		} else if(reqURL.equals("prevphoto")) {
			returnPrevPhoto();
		} else if(reqURL.equals("photoinfo")) {
			returnPhotoInfo();
		} else if(reqURL.equals("taglist")) {
			returnTagList();
		} else if(reqURL.equals("tagassignment")) {
			returnTagAssignment();
		} else {
			throw new HTTPNotFoundException(request.getRequestURL());
		}
	}

	private void returnTagAssignment() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		
		postVars = request.getPostVars();
		
		if(!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();
		
		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("photo").get(0));
			AOPhoto photo = dbHandler.getPhoto(photoId);
			ArrayList<AOTag> allTags = dbHandler.getTags();
			ArrayList<AOTag> assignedTags;
			HashMap<String, String> attributes = new HashMap<String, String>();
			dbHandler.getTags(photo);
			assignedTags = photo.getTags();
			if(photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());
			XMLWriter writer = new XMLWriter(oStream);
			writer.addNode("responsedata");
			
			for(AOTag tag : assignedTags) {
				attributes.put("id", String.valueOf(tag.getId()));
				attributes.put("name", tag.getName());
				writer.addNode("assignedtag", true, attributes);
			}
			
			for(AOTag tag : allTags) {
				if(assignedTags.contains(tag)) continue;
				attributes.put("id", String.valueOf(tag.getId()));
				attributes.put("name", tag.getName());
				writer.addNode("unassignedtag", true, attributes);
			}
			
			writer.closeNode();
			oStream.close();
			ByteArrayInputStream stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch(NumberFormatException e) {
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
			writer.addNode("responsedata");
			for(AOTag tag : tags) {
				attributes.put("id", String.valueOf(tag.getId()));
				attributes.put("name", tag.getName());
				writer.addNode("tag", true, attributes);
			}
			writer.closeNode();
			oStream.close();
			ByteArrayInputStream stream = new ByteArrayInputStream(oStream.toByteArray());
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
		
		if(!postVars.containsKey("photo"))
			throw new HTTPBadRequestException();
		
		try {
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("photo").get(0));
			AOPhoto photo = dbHandler.getPhoto(photoId);
			if(photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());
			XMLWriter writer = new XMLWriter(oStream);
			writer.addNode("responsedata");
			writer.addNode("name");
			writer.setNodeValue(photo.getOrigFileName());
			writer.closeNode();
			writer.addNode("description");
			writer.setNodeValue(photo.getDescription());
			writer.closeNode();
			writer.closeNode();
			oStream.close();
			ByteArrayInputStream stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch(NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}

	private void returnNextPhoto() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		
		postVars = request.getPostVars();
		
		if(!postVars.containsKey("currentphoto"))
			throw new HTTPBadRequestException();
		
		try {
			Integer nPhotoId;
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("currentphoto").get(0));
			AOPhoto photo = dbHandler.getPhoto(photoId);
			if(photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());
			ArrayList<AOPhoto> nextPhotos = dbHandler.getNextPhotos(photo, (short) 1);
			if(nextPhotos.size() > 0) {
				nPhotoId = nextPhotos.get(0).getId();
			} else {
				nPhotoId = photo.getId();
			}
			XMLWriter writer = new XMLWriter(oStream);
			writer.addNode("responsedata");
			writer.setNodeValue(String.valueOf(nPhotoId));
			writer.closeNode();
			oStream.close();
			ByteArrayInputStream stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch(NumberFormatException e) {
			throw new HTTPBadRequestException();
		} catch (DatabaseException e) {
			throw new HTTPServerErrorException();
		} catch (IOException e) {
			throw new HTTPServerErrorException();
		}
	}
	
	private void returnPrevPhoto() throws HTTPException {
		HashMap<String, ArrayList<String>> postVars;
		
		postVars = request.getPostVars();
		
		if(!postVars.containsKey("currentphoto"))
			throw new HTTPBadRequestException();
		
		try {
			Integer pPhotoId;
			ByteArrayOutputStream oStream = new ByteArrayOutputStream();
			Integer photoId = new Integer(postVars.get("currentphoto").get(0));
			AOPhoto photo = dbHandler.getPhoto(photoId);
			if(photo == null)
				throw new HTTPNotFoundException(request.getRequestURL());
			ArrayList<AOPhoto> prevPhotos = dbHandler.getPreviousPhotos(photo, (short) 1);
			if(prevPhotos.size() > 0) {
				pPhotoId = prevPhotos.get(0).getId();
			} else {
				pPhotoId = photo.getId();
			}
			XMLWriter writer = new XMLWriter(oStream);
			writer.addNode("responsedata");
			writer.setNodeValue(String.valueOf(pPhotoId));
			writer.closeNode();
			oStream.close();
			ByteArrayInputStream stream = new ByteArrayInputStream(oStream.toByteArray());
			returnXMLStream(stream);
		} catch(NumberFormatException e) {
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
