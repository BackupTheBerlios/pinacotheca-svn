package de.berlios.pinacotheca;

import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.HTTPResponse;


public interface PTModule {

		
		/**
		 */
		public abstract void setRequest(HTTPRequest request);

			
			/**
			 */
			public abstract HTTPResponse getResponse();
			
		

}
