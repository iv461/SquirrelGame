package com.ivj.squirrelgame.commandParser;

public class ScanException extends Exception {

	private static final long serialVersionUID = -1310054450756652248L;

	public ScanException() {
		super();
	}

	public ScanException(String message) {
		super(message);
	}

	public ScanException(Exception exception) {
		super(exception);
	}

	public ScanException(String message, Exception exception) {
		super(message, exception);
	}

}
