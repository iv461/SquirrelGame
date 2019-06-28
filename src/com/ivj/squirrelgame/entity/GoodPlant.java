package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;

/**
 * This entity stands still and gives the player energy
 * 
 * @author
 * @see Entity
 *
 */
public class GoodPlant extends StaticEntity {
	private final static int startenergy = 100;

	public GoodPlant(XY pos) {
		super(UUID.randomUUID(), startenergy, pos);
	}

}
