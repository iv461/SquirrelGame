package com.ivj.squirrelgame.entity;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.commandParser.Command;
import com.ivj.squirrelgame.commandParser.CommandTypeInfo;
import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.core.GameCommandType;

/**
 * This entity is like a MasterSquirrel but can be operated from outside e.g by
 * the player
 * 
 * @author
 * @see Entity
 * @see MasterSquirrel
 *
 */
public class HandOperatedMasterSquirrel extends MasterSquirrel {

	private Command lastCommand;

	public HandOperatedMasterSquirrel(XY pos, Command lastCommand) {
		super(pos);
		this.lastCommand = lastCommand;
	}

	@Override
	public void nextStep(EntityContext ec) {
		if (ec != null) {
			XY direction = XY.ZERO_ZERO;
			CommandTypeInfo type = lastCommand.getCommandType();
			if (type == GameCommandType.UP) {
				direction = XY.UP;
			} else if (type == GameCommandType.DOWN) {
				direction = XY.DOWN;
			} else if (type == GameCommandType.LEFT) {
				direction = XY.LEFT;
			} else if (type == GameCommandType.RIGHT) {
				direction = XY.RIGHT;
			}

			if (direction != XY.ZERO_ZERO) {
				updateMovingDirection(direction);
				ec.tryMove(this, direction);
			}
		}
	}

}
