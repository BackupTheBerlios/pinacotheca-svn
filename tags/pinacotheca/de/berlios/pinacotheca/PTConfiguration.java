package de.berlios.pinacotheca;

import java.io.File;

public class PTConfiguration {
	private final static PTConfiguration instance = new PTConfiguration();

	private File serverRoot = null;

	private Short serverPort;

	private Short serverSSLPort;
	
	private String adminUser;
	
	private String adminPass;

	private PTConfiguration() {
	}

	public static void init(File serverRoot, Short serverPort, Short serverSSLPort, String adminUser, String adminPass) {
		instance.serverRoot = serverRoot;
		instance.serverPort = serverPort;
		instance.serverSSLPort = serverSSLPort;
		instance.adminPass = adminPass;
		instance.adminUser = adminUser;
	}

	public static File getServerRoot() {
		return instance.serverRoot;
	}

	public static Short getServerPort() {
		return instance.serverPort;
	}

	public static Short getServerSSLPort() {
		return instance.serverSSLPort;
	}

	public static String getAdminPass() {
		return instance.adminPass;
	}

	public static String getAdminUser() {
		return instance.adminUser;
	}
}
