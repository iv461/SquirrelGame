package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;

/**
 * This entity stands still and gives the player damage
 * 
 * @author
 * @see Entity
 *
 */
public class BadPlant extends StaticEntity {

	private final static int startenergy = -100;

	public BadPlant(XY pos) {

		super(UUID.randomUUID(), startenergy, pos);

	}

}
