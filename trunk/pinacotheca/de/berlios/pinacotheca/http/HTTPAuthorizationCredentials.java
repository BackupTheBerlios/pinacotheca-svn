package de.berlios.pinacotheca.http;

public class HTTPAuthorizationCredentials {

	private String userid;

	private String password;

	public HTTPAuthorizationCredentials(String userid, String password) {
		this.userid = userid;
		this.password = password;
	}

	public String getPassword() {
		return password;
	}

	public String getUserid() {
		return userid;
	}
}
