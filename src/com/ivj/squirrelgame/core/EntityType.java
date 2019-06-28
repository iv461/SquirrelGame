package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.BadPlant;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.GoodPlant;
import com.ivj.squirrelgame.entity.MasterSquirrel;
import com.ivj.squirrelgame.entity.MiniSquirrel;
import com.ivj.squirrelgame.entity.Wall;

public enum EntityType {
	GOOD_BEAST, BAD_BEAST, GOOD_PLANT, BAD_PLANT, MINI_SQUIRREL, MASTER_SQUIRREL, WALL, NONE;

	public static EntityType getEntityType(Entity entity) {
		if (entity instanceof Wall)
			return EntityType.WALL;
		else if (entity instanceof MasterSquirrel)
			return EntityType.MASTER_SQUIRREL;
		else if (entity instanceof MiniSquirrel)
			return EntityType.MINI_SQUIRREL;
		else if (entity instanceof GoodPlant)
			return EntityType.GOOD_PLANT;
		else if (entity instanceof BadPlant)
			return EntityType.BAD_PLANT;
		else if (entity instanceof GoodBeast)
			return EntityType.GOOD_BEAST;
		else if (entity instanceof BadBeast)
			return EntityType.BAD_BEAST;
		else
			return EntityType.NONE;

	}
	// TODO possible to do lookup with hashmap
}