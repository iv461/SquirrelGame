package com.ivj.squirrelgame.board;

/**
 * Exception thrown when trying to access Locations out of bounds.
 * 
 * @author
 *
 */
public class LocationOutOfBoundsException extends RuntimeException {

	private static final long serialVersionUID = -7697678267337318102L;

	public LocationOutOfBoundsException() {
		super();
	}

	public LocationOutOfBoundsException(String message) {
		super(message);
	}
}
