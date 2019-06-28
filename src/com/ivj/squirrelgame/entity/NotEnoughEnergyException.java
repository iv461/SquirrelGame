package com.ivj.squirrelgame.entity;

/**
 * Exception thrown on trying to spawn a MiniSquirrel or derivations of it with
 * not enough energy
 * 
 * @author
 * @see FlattenedBoard
 * @see MiniSquirrel
 *
 */
public class NotEnoughEnergyException extends Exception {

	public NotEnoughEnergyException(String string) {
		super(string);
	}

	private static final long serialVersionUID = -1927288501359511876L;

}
