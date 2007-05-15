package de.berlios.pinacotheca.http;

import java.net.Socket;

public class HTTPResponder implements Runnable {

	/**
	 */
	public HTTPResponder(Socket clientSocket, HTTPResponse response) {
	}

	/**
	 */
	public HTTPResponder(Socket clientSocket, HTTPRequest request) {
	}

	public void run() {
	}

	/**
	 * @uml.property name="clientSocket"
	 */
	private Socket clientSocket;

}
