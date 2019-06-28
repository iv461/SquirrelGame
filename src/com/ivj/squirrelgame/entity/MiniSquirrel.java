package com.ivj.squirrelgame.entity;

import com.ivj.squirrelgame.board.*;
import com.ivj.squirrelgame.core.EntityContext;

/**
 * This entity collects energy but is not directly playable. It can only be
 * created by a MasterSquirrel and derivations from it. It has only so much
 * energy the MasterSquirrel gave him. Movement is only implemented in Bot
 * version MiniSquirrelBot.
 * 
 * @author
 * @see Entity
 */

public class MiniSquirrel extends MasterSquirrel {

	private MasterSquirrel master;
	public final static int MINIMAL_ENERGY = 100;

	public MiniSquirrel(int energy, XY pos, MasterSquirrel master) {
		super(pos);
		this.energy = energy;
		this.master = master;
	}

	public MasterSquirrel getMaster() {
		return master;
	}

	@Override
	public boolean shouldDie() {
		return energy <= 0;
	}

	@Override
	public void nextStep(EntityContext ec) {
		if (ec != null) {

			if (shouldDie()) {
				ec.kill(this);
				return;
			}

			energy--;
		}

	}

	public boolean isSibling(Entity entity) {
		if (entity instanceof MiniSquirrel) {
			if (((MiniSquirrel) entity).getMaster() == this.getMaster()) {
				return true;
			}
		}
		return false;
	}
}
