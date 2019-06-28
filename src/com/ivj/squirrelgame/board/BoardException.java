package com.ivj.squirrelgame.board;

/**
 * General purpose exception related to Board class
 * 
 * @author
 * @see FlattenedBoard
 *
 */
public class BoardException extends RuntimeException {

	private static final long serialVersionUID = 481719365687467964L;

	public BoardException() {
		super();
	}

	public BoardException(String message) {
		super(message);
	}

}
