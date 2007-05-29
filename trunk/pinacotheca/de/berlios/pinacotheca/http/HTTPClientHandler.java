package de.berlios.pinacotheca.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Set;

import de.berlios.pinacotheca.PTDispatcher;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPRequestEntityTooLargeException;

public class HTTPClientHandler implements Runnable {
	private Socket clientSocket;

	private boolean isSecure;

	private DataInputStream clientInput;

	public HTTPClientHandler(Socket clientSocket, boolean isSecure) throws IOException {
		this.isSecure = isSecure;
		this.clientSocket = clientSocket;
		clientInput = new DataInputStream(clientSocket.getInputStream());
	}

	public void run() {
		HTTPRequest clientRequest;
		HTTPResponse serverResponse;
		PTModule module;
		boolean closeConnection = false;

		try {
			while (!closeConnection) {
				try {
					clientRequest = readClientRequest();
					if(clientRequest == null) continue; //ignore newlines at start
					closeConnection = clientRequest.containsHeaderField("Connection")
							&& clientRequest.getHeaderField("Connection").equals("close");
					module = PTDispatcher.dispatch(clientRequest, isSecure);
					module.handleRequest();
					serverResponse = module.getResponse();
				} catch (HTTPException e) {
					serverResponse = e.getResponse();
				}
				closeConnection = (serverResponse.containsHeaderField("Connection")
						&& serverResponse.getHeaderField("Connection").equals("close")) || closeConnection;
				sendServerResponse(serverResponse);
				if (closeConnection)
					break;
			}
		} catch (EOFException e) {
			// EOF, do nothing
		} catch (IOException e) {
			PTLogger.logError(e.getMessage());
		} finally {
			try {
				clientSocket.close();
			} catch (IOException e) {
				PTLogger.logError(e.getMessage());
			}
		}
	}

	private void sendServerResponse(HTTPResponse serverResponse) throws IOException {
		DataOutputStream clientOutput = new DataOutputStream(clientSocket.getOutputStream());
		InputStream dataInput;
		String strBuffer;
		Set<String> headerFieldNames = serverResponse.getHeaderFieldNames();
		byte[] buffer;
		int bytesRead;

		strBuffer = serverResponse.getHttpVersion() + " " + serverResponse.getResponseCode() + " " + serverResponse.getResponseMessage() + "\r\n";
		clientOutput.write(strBuffer.getBytes());

		for (String headerFieldName : headerFieldNames) {
			strBuffer = headerFieldName + ": " + serverResponse.getHeaderField(headerFieldName) + "\r\n";
			clientOutput.write(strBuffer.getBytes());
		}
		
		clientOutput.write("\r\n".getBytes());
		clientOutput.flush();

		dataInput = serverResponse.getPayloadStream();
		if (dataInput == null)
			return;

		buffer = new byte[1024];
		while ((bytesRead = dataInput.read(buffer)) != -1) {
			clientOutput.write(buffer, 0, bytesRead);
		}
		clientOutput.flush();
	}

	private HTTPRequest readClientRequest() throws IOException, HTTPException {
		String msgHeaderLine = "";
		HTTPRequestBuilder msgBuilder;
		HTTPMessage request = null;

		try {
    		msgHeaderLine = readLine();
    		if(msgHeaderLine.equals("\r\n")) return null; //ignore newlines at start
    		msgBuilder = new HTTPRequestBuilder(msgHeaderLine);
    
    		for (;;) {
    			msgHeaderLine = readLine();
    			if (msgHeaderLine.equals("\r\n"))
    				break;
    			msgBuilder.parseHeaderLine(msgHeaderLine);
    		}
    
    		request = msgBuilder.getMessage();
    		if (!(request instanceof HTTPRequest))
    			throw new HTTPBadRequestException();
    		request.setPayloadStream(clientInput);
		} catch(HTTPException e) {
			// wait for request end, then throw exception again
			while(!msgHeaderLine.equals("\r\n")) msgHeaderLine = readLine();
			throw e;
		}
		return (HTTPRequest) request;
	}

	private String readLine() throws IOException, HTTPRequestEntityTooLargeException {
		ArrayList<Byte> strBytes = new ArrayList<Byte>(1024);
		byte cChar, pChar = '\0';

		for (;;) {
			if (strBytes.size() == 1024)
				throw new HTTPRequestEntityTooLargeException();
			cChar = clientInput.readByte();
			strBytes.add(cChar);
			if (pChar == '\r' && cChar == '\n')
				break;
			pChar = cChar;
		}

		return getString(strBytes);
	}

	private String getString(ArrayList<Byte> strBytes) {
		byte[] buf = new byte[strBytes.size()];

		for (int i = 0; i < buf.length; i++)
			buf[i] = strBytes.get(i);

		return new String(buf);
	}
}
