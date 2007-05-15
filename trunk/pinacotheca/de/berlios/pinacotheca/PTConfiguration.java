
package de.berlios.pinacotheca;

public class PTConfiguration {

	/**
	 * @uml.property name="serverRoot"
	 */
	private String serverRoot = "null";

	/**
	 * Getter of the property <tt>serverRoot</tt>
	 * 
	 * @return Returns the serverRoot.
	 * @uml.property name="serverRoot"
	 */
	public String getServerRoot() {
		return serverRoot;
	}

	/**
	 * Setter of the property <tt>serverRoot</tt>
	 * 
	 * @param serverRoot
	 *            The serverRoot to set.
	 * @uml.property name="serverRoot"
	 */
	public void setServerRoot(String serverRoot) {
		this.serverRoot = serverRoot;
	}

	/**
	 * @uml.property name="serverPort"
	 */
	private java.lang.Short serverPort;

	/**
	 * Getter of the property <tt>port</tt>
	 * 
	 * @return Returns the port.
	 * @uml.property name="serverPort"
	 */
	public java.lang.Short getServerPort() {
		return serverPort;
	}

	/**
	 * Setter of the property <tt>port</tt>
	 * 
	 * @param port
	 *            The port to set.
	 * @uml.property name="serverPort"
	 */
	public void setServerPort(java.lang.Short serverPort) {
		this.serverPort = serverPort;
	}

	/**
	 * @uml.property name="serverSSLPort"
	 */
	private Short serverSSLPort;

	/**
	 * Getter of the property <tt>serverSSLPort</tt>
	 * 
	 * @return Returns the serverSSLPort.
	 * @uml.property name="serverSSLPort"
	 */
	public Short getServerSSLPort() {
		return serverSSLPort;
	}

	/**
	 * Setter of the property <tt>serverSSLPort</tt>
	 * 
	 * @param serverSSLPort
	 *            The serverSSLPort to set.
	 * @uml.property name="serverSSLPort"
	 */
	public void setServerSSLPort(Short serverSSLPort) {
		this.serverSSLPort = serverSSLPort;
	}

}
