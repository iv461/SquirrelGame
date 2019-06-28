package com.ivj.squirrelgame.board;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.DynamicEntity;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.MasterSquirrel;
import com.ivj.squirrelgame.entity.MasterSquirrelBot;
import com.ivj.squirrelgame.entity.MiniSquirrel;
import com.ivj.squirrelgame.entity.MiniSquirrelBot;
import com.ivj.squirrelgame.entity.NotEnoughEnergyException;
import com.ivj.squirrelgame.entity.StandardMiniSquirrel;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * Class representing a Board with a 2d container. Implements most of the game's
 * logic related to collision
 * 
 * @author
 * @see Board
 * 
 *
 */
public class FlattenedBoard implements EntityContext, BoardView {
	// reference to Board to access some methods
	private Board board;
	private Entity field[][];

	private int width;
	private int height;

	private static final Logger logger = Logger
			.getLogger(FlattenedBoard.class.getName());
	private static final Handler handler = new StreamHandler(System.out,
			new SimpleFormatter());

	boolean logMovement = false;

	public FlattenedBoard(Board board) {
		this.height = board.getConfig().height;
		this.width = board.getConfig().width;
		field = new Entity[width][height];
		this.board = board;

		// set up handler
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);

	}

	/**
	 * Adds entity in field, uses its position. throws exception if this field
	 * is not empty.
	 * 
	 * @param e
	 *            Entity to add
	 */
	public void addEntityInField(Entity e) {

		if (!isEmptyField(e.getPos())) {
			throw new BoardException(
					"Trying to add: " + EntityType.getEntityType(e)
							+ "; Error: field is not empty; on position "
							+ e.getPos().toString() + " there is already a "
							+ EntityType.getEntityType(getEntity(e.getPos()))
									.toString());
		}
		field[e.getPos().x][e.getPos().y] = e;

	}

	/**
	 * Clears a field cell
	 * 
	 * @param pos
	 *            Position of cell to clear
	 * 
	 */
	private void clearField(XY pos) {
		field[pos.x][pos.y] = null;
	}

	@Override
	public void kill(Entity entity) {
		board.removeEntity(entity.getId());
		clearField(entity.getPos());

		if (logMovement) {
			logger.log(Level.INFO, entity.toString() + " was killed");
		}
	}

	@Override
	public void tryMove(MasterSquirrel masterSquirrel, XY direction) {

		if (masterSquirrel == null || direction == null) {
			return;
		}

		boolean moved = false;
		// ensure the coordinates are truncated
		direction = XYSupport.truncateDirection(direction);

		XY newLocation = masterSquirrel.getPos().plus(direction);

		if (logMovement) {
			logger.log(Level.INFO, masterSquirrel.toString() + " tried to move "
					+ direction.toString());
		}

		Entity targetEntity;
		try {
			targetEntity = getEntity(newLocation);
		} catch (LocationOutOfBoundsException e) {
			if (logMovement) {
				logger.log(Level.INFO, "invalid Location");
			}

			return;
		}

		EntityType type = EntityType.getEntityType(targetEntity);

		switch (type) {

		case MINI_SQUIRREL:
			// absorbs the mini squirrel if own, else it kills it
			boolean ownMini = masterSquirrel.isOwnMiniSquirrel(targetEntity);
			if (ownMini) {
				masterSquirrel.updateEnergyOnCollide(targetEntity);
				kill(targetEntity);
				moved = moveEntityAndUpdateField(masterSquirrel, direction);

			} else {
				masterSquirrel.updateEnergy(+150);
				kill(targetEntity);
				moved = moveEntityAndUpdateField(masterSquirrel, direction);
			}

			break;
		default:
			if (symSquirrelCollide(masterSquirrel, targetEntity)) {
				moved = moveEntityAndUpdateField(masterSquirrel, direction);
			}

			break;
		}

		if (logMovement) {
			logger.log(Level.INFO,
					"Movement " + (moved ? "" : "not ") + "successfull");
		}

	}

	@Override
	public void tryMove(MiniSquirrel miniSquirrel, XY direction) {

		if (miniSquirrel == null || direction == null) {
			return;
		}

		boolean moved = false;

		if (logMovement) {
			logger.log(Level.INFO, miniSquirrel.toString() + " tried to move "
					+ direction.toString());
		}

		// ensure the coordinates are truncated
		direction = XYSupport.truncateDirection(direction);

		XY newLocation = miniSquirrel.getPos().plus(direction);

		Entity targetEntity;
		try {
			targetEntity = getEntity(newLocation);
		} catch (LocationOutOfBoundsException e) {
			if (logMovement) {
				logger.log(Level.INFO, "invalid Location");
			}

			return;
		}
		EntityType type = EntityType.getEntityType(targetEntity);

		switch (type) {

		case MASTER_SQUIRREL:
			// absorbs the mini squirrel if own, else it kills it
			MasterSquirrel targetMaster = (MasterSquirrel) targetEntity;
			boolean ownMaster = targetMaster.isOwnMiniSquirrel(miniSquirrel);
			if (ownMaster) {
				targetMaster.updateEnergyOnCollide(miniSquirrel);
				kill(miniSquirrel);

			} else {
				kill(miniSquirrel);
			}

			break;
		case MINI_SQUIRREL:
			MiniSquirrel targetMini = (MiniSquirrel) targetEntity;
			// kill both, but only if they aren't siblings
			if (miniSquirrel.getMaster() != targetMini.getMaster()) {
				kill(targetMini);
				kill(miniSquirrel);
			}

			break;
		default:
			if (symSquirrelCollide(miniSquirrel, targetEntity)) {
				moved = moveEntityAndUpdateField(miniSquirrel, direction);
			}

			break;
		}

		if (logMovement) {
			logger.log(Level.INFO,
					"Movement " + (moved ? "" : "not ") + "successfull");
		}

	}

	@Override
	public void tryMove(GoodBeast goodBeast, XY direction) {

		if (goodBeast == null || direction == null) {
			return;
		}

		boolean moved = false;
		if (logMovement) {
			logger.log(Level.INFO, goodBeast.toString() + " tried to move "
					+ direction.toString());
		}

		// ensure the coordinates are truncated
		direction = XYSupport.truncateDirection(direction);

		XY newLocation = goodBeast.getPos().plus(direction);

		Entity targetEntity;
		try {
			targetEntity = getEntity(newLocation);
		} catch (LocationOutOfBoundsException e) {
			if (logMovement) {
				logger.log(Level.INFO, "invalid Location");
			}

			return;
		}

		EntityType type = EntityType.getEntityType(targetEntity);

		switch (type) {
		case MASTER_SQUIRREL:
		case MINI_SQUIRREL:
			targetEntity.updateEnergyOnCollide(goodBeast);
			kill(goodBeast);
			break;
		case NONE:
			moved = moveEntityAndUpdateField(goodBeast, direction);
			break;
		default:
			break;

		}
		if (logMovement) {
			logger.log(Level.INFO,
					"Movement " + (moved ? "" : "not ") + "successfull");
		}

	}

	@Override
	public void tryMove(BadBeast badBeast, XY direction) {

		if (badBeast == null || direction == null) {
			return;
		}

		boolean moved = false;

		if (logMovement) {
			logger.log(Level.INFO, badBeast.toString() + " tried to move "
					+ direction.toString());
		}

		// ensure the coordinates are truncated
		direction = XYSupport.truncateDirection(direction);

		XY newLocation = badBeast.getPos().plus(direction);

		Entity targetEntity;
		try {
			targetEntity = getEntity(newLocation);
		} catch (LocationOutOfBoundsException e) {
			if (logMovement) {
				logger.log(Level.INFO, "invalid Location");
			}

			return;
		}
		EntityType type = EntityType.getEntityType(targetEntity);

		switch (type) {
		case MASTER_SQUIRREL:
		case MINI_SQUIRREL:
			targetEntity.updateEnergyOnCollide(badBeast);
			break;
		case NONE:
			moved = moveEntityAndUpdateField(badBeast, direction);
			break;
		default:
			break;

		}

		if (logMovement) {
			logger.log(Level.INFO,
					"Movement " + (moved ? "" : "not ") + "successfull");
		}

	}

	@Override
	public Entity nearestSquirrelEntity(XY location) {
		double distance = Double.MAX_VALUE;
		Entity nearest = null;
		// make a copy to iterate to avoid ConcurrentModificationException
		List<Entity> entitiesCopy = new ArrayList<>();
		entitiesCopy.addAll(board.getEntities().getEntites());

		for (Entity e : entitiesCopy) {
			EntityType type = EntityType.getEntityType(e);
			if (type == EntityType.MASTER_SQUIRREL
					|| type == EntityType.MINI_SQUIRREL) {
				double tDist = XYSupport.vec(location, e.getPos()).length();
				if (tDist < distance) {
					distance = tDist;
					nearest = e;
				}

			}
		}

		return nearest;

	}

	@Override
	public void killAndReplace(Entity entity) {
		if (entity == null) {
			throw new NullPointerException();
		}
		EntityType type = EntityType.getEntityType(entity);
		kill(entity);
		Entity replacingEntity = board.addNewEntityOnRandomPosition(type);
		addEntityInField(replacingEntity);

		if (logMovement) {
			logger.log(Level.INFO,
					entity.toString() + " was killed and replaced with "
							+ replacingEntity.toString());
		}

	}

	@Override
	public boolean spawnMiniSquirrel(MasterSquirrel master, XY direction,
			int energy) throws NotEnoughEnergyException {
		// energy must be at least MINIMALENERGY
		if (energy < MiniSquirrel.MINIMAL_ENERGY) {
			throw new NotEnoughEnergyException(
					"minimal required energy for spawning is "
							+ MiniSquirrel.MINIMAL_ENERGY);
		}

		// if more energy than the master has
		if (master.getEnergy() <= energy) {
			throw new NotEnoughEnergyException(
					"master has  enough energy to spawn mini with given energy");
		}

		direction = XYSupport.truncateDirection(direction);
		XY targetPosition = master.getPos().plus(direction);

		// TODO maybe fix this botcontroller stuff
		if (master instanceof MasterSquirrelBot) {
			spawnEntity(new MiniSquirrelBot(energy, targetPosition, master,
					((MasterSquirrelBot) master).getMiniController()));
		} else {
			spawnEntity(
					new StandardMiniSquirrel(energy, targetPosition, master));
		}

		master.updateEnergy(-energy);
		return true;

	}

	@Override
	// TODO test
	public void implode(MiniSquirrel miniSquirrel, int impactRadius) {

		if (miniSquirrel.getEnergy() <= 0) {
			return;
		}

		// truncate the impactRadius
		impactRadius = (impactRadius < 2 ? 2
				: (impactRadius > 10 ? 10 : impactRadius));

		double impactArea = impactRadius * impactRadius * Math.PI;
		int energyLossSum = 0;

		List<XY> position = calculateCellsInCircle(miniSquirrel.getPos(),
				impactRadius);

		for (XY pos : position) {
			if (pos == null
					|| /*
						 * this check is necessary as it is not performed before
						 */!isLocationInBounds(pos)) {
				continue;
			}
			Entity entity = getEntity(pos);
			EntityType type = EntityType.getEntityType(entity);

			if (type == EntityType.NONE || type == EntityType.WALL) {
				// no impact on walls and empty fields
				continue;
			}

			if (type == EntityType.MASTER_SQUIRREL) {

				if (((MasterSquirrel) entity).isOwnMiniSquirrel(miniSquirrel)) {
					// no impact on own patron
					continue;
				}
			}

			if (type == EntityType.MINI_SQUIRREL) {
				if (miniSquirrel.isSibling(entity)) {
					// no impact on siblings
					continue;
				}
			}

			double distance = miniSquirrel.getPos().minus(entity.getPos())
					.length();

			int energyLoss = (int) (200
					* (miniSquirrel.getEnergy() / impactArea)
					* (1 - distance / impactRadius));

			if (Math.abs(entity.getEnergy()) >= Math.abs(energyLoss)) {
				if (entity.getEnergy() > 0) {
					entity.updateEnergy(-Math.abs(energyLoss));
				} else {
					entity.updateEnergy(Math.abs(energyLoss));
				}

				energyLossSum += Math.abs(energyLoss);
			} else {
				// entity does not have enough energy, take all and kill it
				energyLossSum += entity.getEnergy();
				// TODO killANdReplace
				kill(entity);
			}

		}

		// add energyLossSum to master
		MasterSquirrel master = miniSquirrel.getMaster();
		master.updateEnergy(energyLossSum);
		kill(miniSquirrel);

	}

	/**
	 * Spawns a entity by adding it in boards EntitySet and in field
	 * 
	 * @param e
	 *            Entity to add
	 */
	private void spawnEntity(Entity e) {
		if (e != null) {
			addEntityInField(e);
			board.addEntity(e);
		}

	}

	/**
	 * Calculates positions of cells in a Grid which are in the circle with a
	 * center and a radius. So it basically rasterizes the circle line, but
	 * returns also the grid cell positions which are in the circle. It doesn't
	 * truncate the coordinates, so e.g with a center of 5,5 and a radius of 10,
	 * there will be negative coordinates. If the radius is <=0, only the
	 * coordinate of the center is returned.
	 * 
	 * @param center
	 *            the center of the circle
	 * @param radius
	 *            the radius of the circle
	 * @return List of Positions in Circle
	 */
	public static List<XY> calculateCellsInCircle(XY center, int radius) {
		List<XY> positions = new ArrayList<XY>();
		int x = radius, y = 0;

		// first add the center
		positions.add(new XY(center.x, center.y));

		// Initializing the value of P
		int d = 1 - radius;
		while (x > y) {

			final int H_OCTANTS = 0;
			final int V_OCTANTS = 1;
			for (int o = 0; o < 2; o++) {
				// for the x pos we are using a counter so we change only
				// the sign
				int xsign = +1;
				for (int k = 0, ytmp = y; k < 4; k++) {
					// set fill lines
					for (int i =
							// omit middle point but not diagonal lines in
							// horizontal octants,
							// in vertical octants omit always middle point and
							// diagonal line as it
							// is already printed
							(y == 0 || o == V_OCTANTS ? 1 : 0)
									+ y; i <= x; i++) {
						if (o == H_OCTANTS) {
							positions.add(new XY(center.x + i * xsign,
									ytmp + center.y));
						} else if (o == V_OCTANTS) {
							// in vertical direction the x and y are swapped
							positions.add(new XY(ytmp + center.x,
									center.y + i * xsign));
						}

					}

					// swap the sign of the coordinates for the 4 quadrants, so
					// its x,y;
					// -x,y;
					// x,-y;
					// -x,-y;
					xsign = -xsign;
					if (k == 1) {
						ytmp = -ytmp;
					}

					if (y == 0 && k == 1) {
						// on y == 0 we are painting the middle line,
						// which is as well the symmetry axis of the circle,
						// so it doesn't make sense to mirror it;
						break;
					}
				}
			}

			y++;

			if (d <= 0) {
				// Mid-point is inside
				d += 2 * y + 1;
			} else {
				// Mid-point is outside
				x--;
				d += 2 * y - 2 * x + 1;
			}

		}
		return positions;
	}

	/**
	 * Performs necessary actions on symmetric collisions between squirrels and
	 * other entities
	 * 
	 * @param squirrel,
	 *            masterSquirrel or miniSquirrel
	 * @param target,
	 *            can be null
	 * @return true if the squirrel should move on this collision, false if not
	 */
	private boolean symSquirrelCollide(Entity squirrel, Entity target) {
		boolean move = false;

		EntityType type = EntityType.getEntityType(target);

		switch (type) {
		case BAD_BEAST:
			// no movement
			((BadBeast) target).bite();
			squirrel.updateEnergyOnCollide(target);
			break;
		case GOOD_BEAST:
			// no movement
			killAndReplace(target);
			squirrel.updateEnergyOnCollide(target);
			break;
		case BAD_PLANT:
		case GOOD_PLANT:
			killAndReplace(target);
			squirrel.updateEnergyOnCollide(target);
			move = true;
			break;
		case MASTER_SQUIRREL:
			break;
		case MINI_SQUIRREL:
			break;
		case NONE:
			move = true;
			break;
		case WALL:
			// no movement
			squirrel.updateEnergyOnCollide(target);
			squirrel.setRemainingSkippingMoves(
					MasterSquirrel.SKIPPING_MOVES_ON_WALL_COLLIDE);
			break;
		default:
			break;

		}

		return move;
	}

	/**
	 * Moves a entity and updates the entity position in the field
	 * 
	 * @param entity
	 * @param direction
	 * @return true if it moved, false if not
	 */
	private boolean moveEntityAndUpdateField(Entity entity, XY direction) {

		XY oldPos = new XY(entity.getPos().x, entity.getPos().y);

		// check if entity is moved
		if (!entity.move(direction)) {
			return false;
		}

		addEntityInField(entity);

		clearField(oldPos);

		return true;

	}

	/**
	 * Check if this position is empty
	 * 
	 * @param pos
	 *            Position to check
	 * @return true if empty, false if not
	 */
	public boolean isEmptyField(XY pos) {
		if (!isLocationInBounds(pos)) {
			throw new LocationOutOfBoundsException();
		}

		return field[pos.x][pos.y] == null;
	}

	/**
	 * Check if this position is in bounds of the field
	 * 
	 * @param pos
	 *            Position to check
	 * @return true if in bounds, false if not
	 */
	private boolean isLocationInBounds(XY location) {
		int x = location.x, y = location.y;
		if (x >= 0 && x < width && y >= 0 && y < height) {
			return true;
		}

		return false;
	}

	@Override
	public String toString() {
		String out = "";
		out += "width: " + width + "heght: " + height + "\n";
		out += "board reference: " + board + "\n";
		return out;

	}

	@Override
	public XY getSize() {
		return new XY(width, height);
	}

	@Override
	public EntityType getEntityType(XY pos) {
		Entity e = getEntity(pos);
		return EntityType.getEntityType(e);
	}

	@Override
	public Entity getEntity(XY pos) {
		if (!isLocationInBounds(pos)) {
			throw new LocationOutOfBoundsException();
		}
		return field[pos.x][pos.y];
	}

	@Override
	public XY getLastMovingDirection(XY pos) {
		Entity e = getEntity(pos);
		if (!(e instanceof DynamicEntity)) {
			return null;
		}
		DynamicEntity de = ((DynamicEntity) e);
		return de.getLastMovingDirection();
	}

	@Override
	public UUID getID(XY pos) {
		Entity e = getEntity(pos);
		return e.getId();
	}

	@Override
	public boolean areRelated(UUID id1, UUID id2) {
		Entity e1 = board.getEntities().getEntity(id1);
		Entity e2 = board.getEntities().getEntity(id2);

		EntityType et1 = EntityType.getEntityType(e1);
		EntityType et2 = EntityType.getEntityType(e2);

		if (et1 == EntityType.MASTER_SQUIRREL) {
			if (et2 == EntityType.MINI_SQUIRREL) {
				if (e1 == ((MiniSquirrel) e2).getMaster()) {
					return true;
				}

			}
		} else if (et1 == EntityType.MINI_SQUIRREL) {
			if (et2 == EntityType.MASTER_SQUIRREL) {
				if (e2 == ((MiniSquirrel) e1).getMaster()) {
					return true;
				}
			}
		}

		return false;
	}

}
