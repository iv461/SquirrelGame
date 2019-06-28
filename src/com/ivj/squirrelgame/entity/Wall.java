package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;

/**
 * This entity stands still and gives the player damage on collision
 * 
 * @author
 * @see Entity
 */
public class Wall extends StaticEntity {

	private final static int startenergy = -10;

	public Wall(XY pos) {
		super(UUID.randomUUID(), startenergy, pos);
	}

}
