package com.ivj.squirrelgame.entity;

/**
 * General purpose exception used by EntitySet and related
 * 
 * @author
 * @see EntitySet
 *
 */
public class EntitySetException extends RuntimeException {

	private static final long serialVersionUID = 4220462621963226496L;

	public EntitySetException(String message) {
		super(message);
	}

}
