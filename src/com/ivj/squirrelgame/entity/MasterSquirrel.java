package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;
import com.ivj.squirrelgame.core.EntityContext;

/**
 * This entity is a playable entity which collects energy
 * 
 * @author
 * @see Entity
 */

public class MasterSquirrel extends DynamicEntity {

	private static final int startenergy = 1000;
	public final static int SKIPPING_MOVES_ON_WALL_COLLIDE = 3;

	public MasterSquirrel(XY pos) {
		super(UUID.randomUUID(), startenergy, pos);
	}

	/**
	 * Checks if MiniSquirrel is own
	 * 
	 * @param entity
	 * @return true if won MiniSquirrel, false if not
	 */
	public boolean isOwnMiniSquirrel(Entity entity) {
		if (entity instanceof MiniSquirrel) {
			MiniSquirrel sq = (MiniSquirrel) entity;
			if (sq.getMaster() == this) {
				return true;
			}
		}
		return false;
	}

	/*
	 * Updates energy on collision with other entity e, e.g on collision with
	 * bad plant the energy decreases
	 * 
	 * Here it ensures that the energy dont't drop below 0
	 * 
	 * @param e other Entity to collide with
	 * 
	 * @return energy left
	 */
	@Override
	public int updateEnergyOnCollide(Entity e) {
		if (e != null) {
			updateEnergy(e.getEnergy());

		}
		if (getEnergy() < 0) {
			energy = 0;
		}
		return getEnergy();
	}

	@Override
	public void nextStep(EntityContext ec) {

	}

}
