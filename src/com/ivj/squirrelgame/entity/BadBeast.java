package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;
import com.ivj.squirrelgame.core.EntityContext;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * This entity attacks the player and gives him damage, moves only every
 * {@value #SKIPPING_MOVES}th move
 * 
 * @author
 * @see Entity
 *
 */
public class BadBeast extends DynamicEntity {

	public final static int START_ENERGY = -150;

	public final static int SKIPPING_MOVES = 4;
	private int remainingBites;
	// distance this entity can see other entities
	public static final int VIEWDISTANCE = 6;
	// default number of bites
	public static final int BITES = 7;

	public BadBeast(XY pos) {

		super(UUID.randomUUID(), START_ENERGY, pos);

		remainingBites = BITES;

	}

	public void bite() {
		remainingBites--;
	}

	@Override
	public boolean shouldRespawn() {
		return remainingBites <= 0;
	}

	/**
	 * override move to move only every 4th call
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
			if (shouldRespawn()) {
				ec.killAndReplace(this);
				return;
			}

			Entity target = ec.nearestSquirrelEntity(pos);
			if (target == null) {
				return;
			}

			if (XYSupport.vec(pos, target.getPos()).length() <= VIEWDISTANCE) {
				XY direction;
				direction = XYSupport.vec(pos, target.getPos());
				direction = XYSupport
						.neg(XYSupport.truncateDirection(direction));
				updateMovingDirection(direction);
				ec.tryMove(this, direction);
			}

		}

	}

}
