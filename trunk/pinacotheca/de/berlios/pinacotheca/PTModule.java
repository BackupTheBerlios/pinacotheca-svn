package de.berlios.pinacotheca;

import de.berlios.pinacotheca.http.HTTPException;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;


public interface PTModule {

		
		/**
			 */
			public abstract HTTPResponse getResponse();

			
				
				
				public abstract void handleRequest()	throws HTTPException ;
				
			
			
		

}
