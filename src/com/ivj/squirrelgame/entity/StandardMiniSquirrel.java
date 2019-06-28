package com.ivj.squirrelgame.entity;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.core.EntityContext;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * This entity is like MiniSquirrel, but movement is implemented by choosing
 * random direction
 * 
 * @author
 * @see Entity
 * @see MiniSquirrel
 */
public class StandardMiniSquirrel extends MiniSquirrel {
	public StandardMiniSquirrel(int energy, XY pos, MasterSquirrel master) {
		super(energy, pos, master);

	}

	@Override
	public void nextStep(EntityContext ec) {

		ec.tryMove(this, XYSupport.getRandomDirection());
	}
}
