package de.berlios.pinacotheca.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ServerSocketFactory;
import javax.net.ssl.SSLServerSocketFactory;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTMain;


public class HTTPSSLServer implements Runnable {
	
	public void run() {
		try {
			ServerSocketFactory sslFactory; 
			ServerSocket serverSocket;
			
			sslFactory = SSLServerSocketFactory.getDefault();
			serverSocket = sslFactory.createServerSocket(PTConfiguration.getServerSSLPort());
			
			while (true) {
				Socket cSocket = serverSocket.accept();
				HTTPClientHandler cHandler = new HTTPClientHandler(cSocket, true);
				new Thread(cHandler).start();
			}
		} catch (IOException e) {
			PTMain.exitError(e.getLocalizedMessage());
		}
	}
}
