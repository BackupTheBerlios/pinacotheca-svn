package de.berlios.pinacotheca.http;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import de.berlios.pinacotheca.PTConfiguration;
import de.berlios.pinacotheca.PTMain;

public class HTTPServer implements Runnable {

	public void run() {
		try {
			ServerSocket serverSocket = new ServerSocket(PTConfiguration.getServerPort());
			while (true) {
				Socket cSocket = serverSocket.accept();
				HTTPClientHandler cHandler = new HTTPClientHandler(cSocket, false);
				new Thread(cHandler).start();
			}
		} catch (IOException e) {
			PTMain.exitError(e.getLocalizedMessage());
		}
	}
}
