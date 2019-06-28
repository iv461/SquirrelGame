package com.ivj.squirrelgame.entity;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.botapi.BotController;
import com.ivj.squirrelgame.botapi.BotControllerFactory;
import com.ivj.squirrelgame.botapi.ControllerContext;
import com.ivj.squirrelgame.botapi.OutOfViewException;
import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.logging.*;

/**
 * This entity is a playable entity which collects energy and is controlled by
 * AI
 * 
 * @author
 * @see Entity
 */
public class MasterSquirrelBot extends MasterSquirrel {
	private final BotController masterController;
	private ControllerContext controllerContext;
	private final BotController miniController;
	private final BotControllerFactory factory;

	public MasterSquirrelBot(XY pos, BotControllerFactory factory,
			String name) {
		super(pos);
		this.factory = factory;
		this.miniController = this.factory.createMiniBotController();
		this.masterController = this.factory.createMasterBotController();

	}

	@Override
	public void nextStep(EntityContext ec) {
		masterController.nextStep(getControllerContext(ec));
	}

	public BotController getMiniController() {
		return miniController;
	}

	private ControllerContext getControllerContext(
			EntityContext entityContext) {
		if (this.controllerContext == null) {
			ControllerContextImpl ctx = new ControllerContextImpl(
					entityContext);

			// TODO check logger
			this.controllerContext = ctx;

			/*
			 * LoggingProxyFactory.createLogger(ctx, this.getId().toString());
			 */
		}

		return this.controllerContext;
	}

	private class ControllerContextImpl implements ControllerContext {

		private final EntityContext entityContext;

		private static final int MaxSightRange = 15;

		ControllerContextImpl(EntityContext context) {
			this.entityContext = context;
		}

		@Override
		public XY getViewLowerLeft() {
			XY curr = locate();
			XY viewLowerLeft = new XY(curr.x - MaxSightRange,
					curr.y + MaxSightRange);
			XY inBounds = new XY((viewLowerLeft.x < 0 ? 0 : viewLowerLeft.x),
					(viewLowerLeft.y > (entityContext.getSize().y - 1)
							? (entityContext.getSize().y - 1)
							: viewLowerLeft.y));
			return inBounds;
		}

		@Override
		public XY getViewUpperRight() {
			XY curr = locate();
			XY viewLowerLeft = new XY(curr.x + MaxSightRange,
					curr.y - MaxSightRange);
			XY inBounds = new XY(
					(viewLowerLeft.x > (entityContext.getSize().x - 1)
							? (entityContext.getSize().x - 1)
							: viewLowerLeft.x),
					(viewLowerLeft.y < 0 ? 0 : viewLowerLeft.y));
			return inBounds;
		}

		@Override
		public EntityType getEntityAt(XY pos) {

			if (!isInViewRect(pos)) {
				System.out.println("upperR: " + getViewUpperRight().toString()
						+ " lowerL: " + getViewLowerLeft().toString()
						+ "player pos: " + locate().toString());
				throw new OutOfViewException();
			}

			return entityContext.getEntityType(pos);
		}

		private boolean isInViewRect(XY pos) {
			XY uR = getViewUpperRight();
			XY lL = getViewLowerLeft();
			if (pos.x >= lL.x && pos.y <= lL.y && pos.x <= uR.x
					&& pos.y >= uR.y) {
				return true;
			} else {
				return false;
			}
		}

		@Override
		/**
		 * not implemented for {@code MasterSquirrelBot}
		 */
		public void implode(int impactRadius) {

		}

		@Override
		public XY directionOfMaster() {
			// not implemented for master
			return null;

		}

		@Override
		public XY locate() {
			return new XY(getPos().x, getPos().y);
		}

		@Override
		public boolean isMine(XY location) {
			if (!isInViewRect(location)) {
				throw new OutOfViewException();
			}

			Entity destinationEntity = entityContext.getEntity(location);
			return isOwnMiniSquirrel(destinationEntity);
		}

		@Override
		public void move(XY direction) {
			entityContext.tryMove(MasterSquirrelBot.this, direction);
		}

		// TODO check
		@Override
		public void spawnMiniBot(XY direction, int energy) {
			try {
				entityContext.spawnMiniSquirrel(MasterSquirrelBot.this,
						direction, energy);
			} catch (NotEnoughEnergyException e) {
				e.printStackTrace();
			}
		}

		@Override
		public int getEnergy() {
			return MasterSquirrelBot.this.getEnergy();
		}

		@Override
		public long getRemainingSteps() {
			// TODO ?
			return 0;
		}
	}

}
