package de.berlios.pinacotheca.http;

import java.io.DataInputStream;
import java.io.IOException;
import java.util.ArrayList;

import de.berlios.pinacotheca.http.exceptions.HTTPRequestEntityTooLargeException;

public class HTTPLineReader {
	private DataInputStream stream;

	private int bytesRead = 0;

	public HTTPLineReader(DataInputStream stream) {
		this.stream = stream;
	}

	public String readLine() throws IOException, HTTPRequestEntityTooLargeException {
		ArrayList<Byte> strBytes = new ArrayList<Byte>(1024);
		byte cChar, pChar = '\0';

		for (;;) {
			if (strBytes.size() == 1024)
				throw new HTTPRequestEntityTooLargeException();
			cChar = stream.readByte();
			strBytes.add(cChar);
			if (pChar == '\r' && cChar == '\n')
				break;
			pChar = cChar;
		}

		bytesRead = strBytes.size();

		return getString(strBytes);
	}

	private static String getString(ArrayList<Byte> strBytes) {
		byte[] buf = new byte[strBytes.size()];

		for (int i = 0; i < buf.length; i++)
			buf[i] = strBytes.get(i);

		return new String(buf);
	}

	public int getLastBytesRead() {
		return bytesRead;
	}
}
