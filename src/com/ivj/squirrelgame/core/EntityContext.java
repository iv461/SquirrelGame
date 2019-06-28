package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.MasterSquirrel;
import com.ivj.squirrelgame.entity.MiniSquirrel;
import com.ivj.squirrelgame.entity.NotEnoughEnergyException;

public interface EntityContext {

	/**
	 * 
	 * @return size of the board
	 */
	XY getSize();

	void tryMove(MasterSquirrel masterSquirrel, XY direction);

	void tryMove(MiniSquirrel miniSquirrel, XY direction);

	void tryMove(GoodBeast goodBeast, XY direction);

	void tryMove(BadBeast badBeast, XY direction);

	void killAndReplace(Entity entity);

	void kill(Entity entity);

	Entity nearestSquirrelEntity(XY location);

	/**
	 * Spawns, updates board and field, MiniSquirrel with energy and in
	 * direction from the master, direction is truncated to one move. Spawning
	 * fails on too low energy or invalid position
	 * 
	 * @param master
	 *            from which it is spawned, needed e.g for its energy
	 * @param direction,
	 *            truncated to one move
	 * @param energy
	 * @return true if spawning successful, false if not
	 * @throws NotEnoughEnergyException
	 */
	boolean spawnMiniSquirrel(MasterSquirrel master, XY direction, int energy)
			throws NotEnoughEnergyException;

	void implode(MiniSquirrel miniSquirrel, int impactRadius);

	EntityType getEntityType(XY xy);

	Entity getEntity(XY xy);
	
	

}
