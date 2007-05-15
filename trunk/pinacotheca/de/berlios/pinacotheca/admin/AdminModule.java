package de.berlios.pinacotheca.admin;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.HTTPException;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;

public class AdminModule implements PTModule {

	public HTTPResponse getResponse() {
		return null;
	}

		
		/**
		 */
		public AdminModule(PTConfiguration configuration, HTTPRequest request){
		}

	/**
	 * @uml.property name="request"
	 */
	private HTTPRequest request;

	public void handleRequest() throws HTTPException {
		// TODO Auto-generated method stub

	}

}
