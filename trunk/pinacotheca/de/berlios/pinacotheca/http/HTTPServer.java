package de.berlios.pinacotheca.http;

import java.io.IOException;
import java.net.Socket;

import de.berlios.pinacotheca.PTConfiguration;

public class HTTPServer implements Runnable {

	/**
	 */
	public HTTPServer(PTConfiguration configuration) throws IOException {
	}

	/**
	 * @uml.property name="configuration"
	 */
	private PTConfiguration configuration;

	public void run() {
	}

	/**
	 * @uml.property name="serverSocket"
	 */
	private Socket serverSocket;

	/**
	 * Getter of the property <tt>serverSocket</tt>
	 * 
	 * @return Returns the serverSocket.
	 * @uml.property name="serverSocket"
	 */
	protected Socket getServerSocket() {
		return serverSocket;
	}

	/**
	 * Setter of the property <tt>serverSocket</tt>
	 * 
	 * @param serverSocket
	 *            The serverSocket to set.
	 * @uml.property name="serverSocket"
	 */
	protected void setServerSocket(Socket serverSocket) {
		this.serverSocket = serverSocket;
	}

	/**
	 * Getter of the property <tt>configuration</tt>
	 * 
	 * @return Returns the configuration.
	 * @uml.property name="configuration"
	 */
	protected PTConfiguration getConfiguration() {
		return configuration;
	}

	/**
	 * Setter of the property <tt>configuration</tt>
	 * 
	 * @param configuration
	 *            The configuration to set.
	 * @uml.property name="configuration"
	 */
	protected void setConfiguration(PTConfiguration configuration) {
		this.configuration = configuration;
	}

	/**
	 */
	protected HTTPServer() {
	}

}
