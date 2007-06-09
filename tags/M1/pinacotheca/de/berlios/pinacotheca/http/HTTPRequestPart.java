package de.berlios.pinacotheca.http;

import java.util.HashMap;

public class HTTPRequestPart {
	private HashMap<String, HTTPMessageHeaderValue> headerFields = new HashMap<String, HTTPMessageHeaderValue>();

	private byte[] payloadBuffer;

	private int payloadStart;

	private int payloadLength;

	public void addHeaderField(String fieldName, HTTPMessageHeaderValue value) {
		headerFields.put(fieldName, value);
	}

	public boolean containsHeaderField(String fieldName) {
		return headerFields.containsKey(fieldName);
	}

	public HTTPMessageHeaderValue getHeaderField(String fieldName) {
		return headerFields.get(fieldName);
	}

	public void setPayload(byte[] buffer, int payloadStart, int length) {
		this.payloadBuffer = buffer;
		this.payloadStart = payloadStart;
		this.payloadLength = length;
	}

	public byte[] getPayloadBuffer() {
		return payloadBuffer;
	}

	public int getPayloadLength() {
		return payloadLength;
	}

	public int getPayloadStart() {
		return payloadStart;
	}

}
