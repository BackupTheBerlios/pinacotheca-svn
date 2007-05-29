package de.berlios.pinacotheca;

public class PTLogger {
	private static PTLogger instance = new PTLogger();
	
	private PTLogger() {
	}
	
	public static void logError(String errMsg) {
		instance.iLogErr(errMsg);
	}

	private void iLogErr(String errMsg) {
		System.err.println(errMsg);
	}
}
