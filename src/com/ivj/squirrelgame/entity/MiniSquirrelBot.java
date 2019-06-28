package com.ivj.squirrelgame.entity;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.botapi.BotController;
import com.ivj.squirrelgame.botapi.ControllerContext;
import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.botapi.OutOfViewException;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.logging.*;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * This entity is like MiniSquirrel, but movement is implemented using AI
 * 
 * @author
 * @see Entity
 * @see MiniSquirrel
 */
public class MiniSquirrelBot extends MiniSquirrel {
	private BotController controller;
	private ControllerContext controllerContext;

	public MiniSquirrelBot(int energy, XY pos, MasterSquirrel master,
			BotController controller) {
		super(energy, pos, master);
		this.controller = controller;
	}

	private ControllerContext getControllerContext(
			EntityContext entityContext) {
		if (this.controllerContext == null) {
			ControllerContextImpl ctx = new ControllerContextImpl(
					entityContext);
			this.controllerContext = LoggingProxyFactory.createLogger(ctx,
					this.getId().toString());
		}

		return controllerContext;
	}

	@Override
	public void nextStep(EntityContext ec) {
		controller.nextStep(getControllerContext(ec));
	}

	private class ControllerContextImpl implements ControllerContext {

		private final EntityContext entityContext;

		private static final int MaxSightRange = 10;

		ControllerContextImpl(EntityContext context) {
			entityContext = context;
		}

		@Override
		public XY getViewLowerLeft() {
			return new XY(0, entityContext.getSize().y);
		}

		@Override
		public XY getViewUpperRight() {
			return new XY(entityContext.getSize().x, 0);
		}

		@Override
		public EntityType getEntityAt(XY xy) {
			XY myPosition = MiniSquirrelBot.this.getPos();
			XY delta = xy.minus(myPosition);

			if (!isInBounds(delta)) {
				throw new OutOfViewException();
			}

			return entityContext.getEntityType(xy);
		}

		@Override
		public boolean isMine(XY location) {
			if (!isInBounds(location)) {
				throw new OutOfViewException();
			}

			Entity destinationEntity = entityContext.getEntity(location);
			return isSibling(destinationEntity)
					|| getMaster().equals(destinationEntity);
		}

		private boolean isInBounds(XY delta) {
			int dx = Math.abs(delta.x);
			int dy = delta.y;
			return dx <= MaxSightRange && dy <= MaxSightRange;
		}

		@Override
		public void implode(int impactRadius) {
			entityContext.implode(MiniSquirrelBot.this, impactRadius);
		}

		@Override
		public XY directionOfMaster() {
			XY direction = XYSupport
					.truncateDirection(getMaster().getPos().minus(getPos()));

			return new XY(direction.x, direction.y);
		}

		@Override
		public XY locate() {
			return new XY(getPos().x, getPos().y);
		}

		@Override
		public void move(XY direction) {
			entityContext.tryMove(MiniSquirrelBot.this, direction);
		}

		@Override
		public void spawnMiniBot(XY direction, int energy) {
			// not for MiniSquirrel
		}

		@Override
		public int getEnergy() {
			return MiniSquirrelBot.this.getEnergy();
		}

		@Override
		public long getRemainingSteps() {
			// TODO ?
			return 0;
		}

	}
}
