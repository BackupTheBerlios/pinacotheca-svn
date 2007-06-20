package de.berlios.pinacotheca.http;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;
import java.net.Socket;
import java.util.Set;

import de.berlios.pinacotheca.PTDispatcher;
import de.berlios.pinacotheca.PTLogger;
import de.berlios.pinacotheca.PTModule;
import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;
import de.berlios.pinacotheca.http.exceptions.HTTPServerErrorException;

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

					if (clientRequest == null)
						continue; // ignore newlines at start

					closeConnection = clientRequest.containsHeaderField("Connection")
							&& clientRequest.getHeaderField("Connection").equals("close");
					module = PTDispatcher.dispatch(clientRequest, isSecure);
					module.handleRequest();
					serverResponse = module.getResponse();
				} catch (HTTPException e) {
					serverResponse = e.getResponse();
				}

				if (serverResponse == null)
					serverResponse = new HTTPServerErrorException().getResponse();

				closeConnection = (serverResponse.containsHeaderField("Connection") && serverResponse.getHeaderField("Connection").equals(
						"close"))
						|| closeConnection;
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

		strBuffer = serverResponse.getHttpVersion() + " " + serverResponse.getResponseCode() + " " + serverResponse.getResponseMessage()
				+ "\r\n";
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

		buffer = new byte[512000];
		while ((bytesRead = dataInput.read(buffer)) != -1) {
			clientOutput.write(buffer, 0, bytesRead);
		}
		clientOutput.flush();
	}

	private HTTPRequest readClientRequest() throws IOException, HTTPException {
		String msgHeaderLine = "";
		HTTPRequestBuilder requestBuilder;
		HTTPMessage request = null;
		HTTPLineReader reader = new HTTPLineReader(clientInput);

		try {
			msgHeaderLine = reader.readLine();
			if (msgHeaderLine.equals("\r\n"))
				return null; // ignore newlines at start
			requestBuilder = new HTTPRequestBuilder(msgHeaderLine);

			for (;;) {
				msgHeaderLine = reader.readLine();
				if (msgHeaderLine.equals("\r\n"))
					break;
				requestBuilder.parseHeaderLine(msgHeaderLine);
			}

			request = requestBuilder.getMessage();
			if (!(request instanceof HTTPRequest))
				throw new HTTPBadRequestException();
			request.setPayloadStream(clientInput);
		} catch (HTTPException e) {
			// wait for request end, then throw exception again
			try {
				byte[] buf = new byte[4096];
				while (clientInput.read(buf) != -1)
					;
				throw e;
			} catch (IOException iE) {
				throw new HTTPServerErrorException();
			}
		}
		return (HTTPRequest) request;
	}
}
