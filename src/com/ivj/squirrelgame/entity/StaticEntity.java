package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.core.EntityContext;

/**
 * Abstract class for entities which are moving
 * 
 * @author
 * @see Entity
 *
 */
public abstract class StaticEntity extends Entity {

	StaticEntity(UUID id, int energy, XY pos) {
		super(id, energy, pos);
	}

	@Override
	public void nextStep(EntityContext ec) {
		// doesn't move
	}

}
