package de.berlios.pinacotheca.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;

public class HTTPClientHandler implements Runnable {
	/** 
	 * @uml.property name="clientSocket"
	 */
	private Socket clientSocket;
	
		
		
		public HTTPClientHandler(boolean isSecure, Socket clientSocket){
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
		/** 
		 * @uml.property name="isSecure"
		 */
		private boolean isSecure;

}
