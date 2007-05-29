package de.berlios.pinacotheca;

import de.berlios.pinacotheca.admin.AdminModule;
import de.berlios.pinacotheca.ajax.AJAXModule;
import de.berlios.pinacotheca.album.AlbumModule;
import de.berlios.pinacotheca.http.HTTPRequest;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPNotFoundException;
import de.berlios.pinacotheca.http.exceptions.HTTPSeeOtherException;

public class PTDispatcher {
	public static PTModule dispatch(HTTPRequest request, boolean isSecure) throws HTTPException {
		String url = request.getRequestURL();
		PTModule module;
		
		if(url.equals("/")) throw new HTTPSeeOtherException("/album/");

		if (url.startsWith("/admin/")) {
			module = new AdminModule();
		} else if (url.startsWith("/album/")) {
			module = new AlbumModule();
		} else if (url.startsWith("/ajax/")) {
			module = new AJAXModule();
		} else {
			throw new HTTPNotFoundException(url);
		}

		module.setRequest(request);
		module.setSecureConnection(isSecure);

		return module;
	}

	private PTDispatcher() {
	}
}
