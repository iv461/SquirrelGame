package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;
import com.ivj.squirrelgame.core.EntityContext;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * This entity runs away from the player and gives him energy, moves only every
 * {@value #SKIPPING_MOVES}th move
 * 
 * @author
 * @see Entity
 *
 */
public class GoodBeast extends DynamicEntity {

	private final static int startenergy = 200;
	public final static int SKIPPING_MOVES = 4;

	// distance this entity can see other entities
	public static final int VIEW_DISTANCE = 6;

	public GoodBeast(XY pos) {
		super(UUID.randomUUID(), startenergy, pos);
	}

	/**
	 * Override move to move only every 4th call.
	 */
	@Override
	public boolean move(XY direction) {
		if (super.move(direction)) {
			setRemainingSkippingMoves(SKIPPING_MOVES);
			return true;
		}
		return false;
	}

	@Override
	public void nextStep(EntityContext ec) {
		if (ec != null) {

			Entity target = ec.nearestSquirrelEntity(pos);
			if (target == null) {
				return;
			}

			if (XYSupport.vec(pos, target.getPos()).length() <= VIEW_DISTANCE) {
				XY direction;
				direction = XYSupport.vec(pos, target.getPos());
				direction = XYSupport.truncateDirection(direction);
				updateMovingDirection(direction);
				ec.tryMove(this, direction);
			}

		}

	}

}
