package de.berlios.pinacotheca.xml;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Stack;

public class XMLWriter {
	private OutputStream outStream;
	
	private Stack<String> nodeStack;
	
	private boolean valueWritten = false;
 
	public XMLWriter(OutputStream outStream) throws IOException {
		this.outStream = outStream;
		nodeStack = new Stack<String>();
		initDocument();
	}

	private void initDocument() throws IOException {
		String xmlDecl = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n";
		outStream.write(xmlDecl.getBytes());
		valueWritten = false;
	}

	private String prepareData(String data) {
		data = data.replaceAll(">", "&gt;");
		data = data.replaceAll("<", "&lt;");
		return data;
	}

	private void indentNode(StringBuffer sBuf) {
		for(int i = 0; i < nodeStack.size(); i++) {
			sBuf.append('\t');
		}
	}
	
	public void setXSLTemplate(String url) throws IOException {
		String xslDecl = "<?xml-stylesheet type=\"text/xsl\" href=\"" + prepareData(url) + "\"?>\n";
		outStream.write(xslDecl.getBytes());
		valueWritten = false;
	}

	public void addNode(String node) throws IOException {
		addNode(node, false);
	}
	
	public void addNode(String node, boolean closeNode) throws IOException {
		StringBuffer sBuf = new StringBuffer();
		
		sBuf.append('\n');
		indentNode(sBuf);
		sBuf.append('<');
		sBuf.append(node);
		if(closeNode)
			sBuf.append('/');
		sBuf.append('>');
		outStream.write(sBuf.toString().getBytes());
		if(!closeNode)
			nodeStack.push(node);
		valueWritten = false;
	}
	
	public void addNode(String node, boolean closeNode, HashMap<String, String> attributes) throws IOException{
		StringBuffer sBuf = new StringBuffer();
		
		sBuf.append('\n');
		indentNode(sBuf);
		sBuf.append('<');
		sBuf.append(node);
		
		for(String key : attributes.keySet()) {
			sBuf.append(" " + key + "=\"");
			sBuf.append(prepareData(attributes.get(key)));
			sBuf.append("\"");
		}
		
		if(closeNode)
			sBuf.append('/');
		sBuf.append('>');
		outStream.write(sBuf.toString().getBytes());
		if(!closeNode)
			nodeStack.push(node);
		valueWritten = false;
	}
	
	public void closeNode() throws IOException {
		String node = nodeStack.pop();
		StringBuffer sBuf = new StringBuffer();
		
		if(!valueWritten) {
			sBuf.append('\n');
			indentNode(sBuf);
		}
		
		sBuf.append("</");
		sBuf.append(node);
		sBuf.append(">");
		outStream.write(sBuf.toString().getBytes());
		valueWritten = false;
	}
	
	public void setNodeValue(String value) throws IOException {
		value = prepareData(value);
		outStream.write(value.getBytes());
		valueWritten = true;
	}
}