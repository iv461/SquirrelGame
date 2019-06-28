package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.XY;

/**
 * Abstract class for entities which don't move
 * 
 * @author
 * @see Entity
 *
 */
public abstract class DynamicEntity extends Entity {

	// moving (dynamic) entities have a direction in which they are moving, here
	// the last direction is saved, this is needed for Rendering
	private XY lastMovingDirection = XY.RIGHT;

	DynamicEntity(UUID id, int energy, XY pos) {
		super(id, energy, pos);
	}

	protected void updateMovingDirection(XY direction) {
		// only horizontal direction is important
		if (direction.equals(XY.RIGHT) || direction.equals(XY.LEFT)) {
			lastMovingDirection = new XY(direction.x, direction.y);
		}

	}

	public XY getLastMovingDirection() {
		return lastMovingDirection;
	}

}
