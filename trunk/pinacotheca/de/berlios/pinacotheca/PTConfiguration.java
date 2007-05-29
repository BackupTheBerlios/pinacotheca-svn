package de.berlios.pinacotheca;

import java.io.File;

public class PTConfiguration {
	private final static PTConfiguration instance = new PTConfiguration();

	private File serverRoot = null;

	private Short serverPort;

	private Short serverSSLPort;

	private PTConfiguration() {
	}

	public static void init(File serverRoot, Short serverPort, Short serverSSLPort) {
		instance.serverRoot = serverRoot;
		instance.serverPort = serverPort;
		instance.serverSSLPort = serverSSLPort;
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
}
