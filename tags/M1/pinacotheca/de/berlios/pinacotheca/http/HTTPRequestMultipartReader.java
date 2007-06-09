package de.berlios.pinacotheca.http;

import java.util.HashMap;

import de.berlios.pinacotheca.http.exceptions.HTTPBadRequestException;
import de.berlios.pinacotheca.http.exceptions.HTTPException;

public class HTTPRequestMultipartReader {
	private int readPosition = 0;

	private byte[] buffer;

	private String boundary;

	private byte[] boundaryBytes;

	public HTTPRequestMultipartReader(byte[] buffer, String boundary) {
		this.buffer = buffer;
		this.boundary = "--" + boundary;
		this.boundaryBytes = this.boundary.getBytes();
	}

	public HashMap<String, HTTPRequestPart> getParts() throws HTTPException {
		String line, fieldName, formPartName;
		int delim, payloadStart;
		HTTPRequestPart part;
		HashMap<String, HTTPRequestPart> partList = new HashMap<String, HTTPRequestPart>();
		HTTPMessageHeaderValue headerValue;
		boolean isEndBoundary;

		if (!findNextBoundary())
			throw new HTTPBadRequestException();
		while (readPosition < buffer.length) {
			part = new HTTPRequestPart();

			for (;;) {
				line = readLine();

				if (line.equals("\r\n"))
					break;

				delim = line.indexOf(':');

				if (delim == -1)
					throw new HTTPBadRequestException();

				fieldName = line.substring(0, delim).trim();
				headerValue = HTTPMessageHeaderValue.parseHeaderValue(line.substring(delim + 1));
				part.addHeaderField(fieldName, headerValue);
			}

			payloadStart = readPosition;

			if (!part.containsHeaderField("Content-Disposition") || !findNextBoundary())
				throw new HTTPBadRequestException();

			headerValue = part.getHeaderField("Content-Disposition");

			if (!headerValue.containsAttribute("name"))
				throw new HTTPBadRequestException();

			formPartName = headerValue.getAttribute("name");
			
			isEndBoundary = isEndBoundary();
			
			part.setPayload(buffer, payloadStart, readPosition - payloadStart - boundaryBytes.length - ((isEndBoundary) ? 4 : 2));
			partList.put(formPartName, part);

			if (isEndBoundary)
				break;
		}

		if (!isEndBoundary())
			throw new HTTPBadRequestException();

		return partList;
	}

	private boolean isEndBoundary() {
		return buffer[readPosition - 2] == '-' && buffer[readPosition - 1] == '-';
	}

	private boolean findNextBoundary() {
		int checkPos = 0;

		while (readPosition < buffer.length) {
			if (buffer[readPosition++] == boundaryBytes[checkPos])
				checkPos++;
			else
				checkPos = 0;
			if (checkPos == boundaryBytes.length) {
				readPosition += 2;
				return true;
			}
		}
		return false;
	}

	private String readLine() throws HTTPException {
		byte currentByte, previousByte = '\0';
		int lineStart = readPosition, lineEnd = readPosition;

		while (readPosition < buffer.length) {
			currentByte = buffer[readPosition++];
			if (previousByte == '\r' && currentByte == '\n') {
				lineEnd = readPosition;
				break;
			}
			previousByte = currentByte;
		}

		if (lineStart == lineEnd)
			throw new HTTPBadRequestException();

		return new String(buffer, lineStart, lineEnd - lineStart);
	}
}
