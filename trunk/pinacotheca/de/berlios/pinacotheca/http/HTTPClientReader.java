package de.berlios.pinacotheca.http;

import java.net.Socket;
import java.io.DataInputStream;
import java.io.DataOutputStream;

public class HTTPClientReader implements Runnable {
	/** 
	 * @uml.property name="clientSocket"
	 */
	private Socket clientSocket;
	
	/**
	 */
	public HTTPClientReader(Socket clientSocket) {
	}

	public void run() {
	}

		
		/**
		 */
		private String readLine(){
			return "";	
		}


		/**
		 * @uml.property  name="inputStream"
		 */
		private DataInputStream inputStream;
		/**
		 * @uml.property  name="outputStream"
		 */
		private DataOutputStream outputStream;

}
