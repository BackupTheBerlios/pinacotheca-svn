package de.berlios.pinacotheca;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import de.berlios.pinacotheca.http.HTTPSSLServer;
import de.berlios.pinacotheca.http.HTTPServer;


public class PTMain {
	public static void main(String[] args) {
		HTTPServer server;
		HTTPSSLServer sslServer;
		Thread serverThread;
		Thread sslServerThread;
		
		try {
    		initConfiguration(args);
    		server = new HTTPServer();
    		sslServer = new HTTPSSLServer();
    		serverThread = new Thread(server);
    		sslServerThread = new Thread(sslServer);
    		serverThread.start();
    		sslServerThread.start();
			serverThread.join();
			sslServerThread.join();
		} catch (InterruptedException e) {
			exitError(e.getLocalizedMessage());
		}
	}

	private static void initConfiguration(String[] args) {
		String configFileName;
		File configFile, serverRoot;
		FileInputStream configInputStream = null;
		Properties props;
		Short serverPort, serverSSLPort;
		
		if(args.length != 1) {
			printUsage();
			System.exit(1);
		}
		
		configFileName = args[0];
		configFile = new File(configFileName);
		props = new Properties();
		
		try {
			configInputStream = new FileInputStream(configFile);
			props.load(configInputStream);
		} catch (IOException e) {
			exitError(e.getLocalizedMessage());
		}
		
		if(!props.containsKey("pt.port"))
			exitError("Configuration file does not contain pt.port entry.");
		
		if(!props.containsKey("pt.sslPort"))
			exitError("Configuration file does not contain pt.sslPort entry.");
		
		if(!props.containsKey("pt.serverRoot"))
			exitError("Configuration file does not contain pt.serverRoot entry.");
		
		if((serverPort = validatePort(props.getProperty("pt.port"))) == null)
			exitError("Invalid pt.port specified.");
		
		if((serverSSLPort = validatePort(props.getProperty("pt.sslPort"))) == null)
			exitError("Invalid pt.sslPort specified.");
		
		serverRoot = new File(props.getProperty("pt.serverRoot"));
		
		if(!serverRoot.isDirectory())
			exitError("Invalid pt.serverRoot specified.");
		
		PTConfiguration.init(serverRoot, serverPort, serverSSLPort);
		
		try {
			configInputStream.close();
		} catch (IOException e) {
			exitError(e.getLocalizedMessage());
		}
	}
	
	public static Short validatePort(String portString) {
		Short port = null;
		
		try {
			port = new Short(portString);
		} catch(NumberFormatException e) {
			// Do nothing, we simply return null
		}
		
		return port;
	}

	public static void exitError(String error) {
		System.err.println(error);
		System.exit(1);
	}

	private static void printUsage() {
		System.out.println("Usage:\tpinacothecasrv -h");
		System.out.println("\tpinacothecasrv <propertyFile>");
	}
}
