package de.berlios.pinacotheca;

public class PTLogger {
	private static PTLogger instance = new PTLogger();
	
	private PTLogger() {
	}
	
	/**
	 * This method logs an error message to the default error logging stream.
	 * 
	 * @param errMsg
	 */
	public static void logError(String errMsg) {
		instance.iLogErr(errMsg);
	}

	private void iLogErr(String errMsg) {
		System.err.println(errMsg);
	}
}
