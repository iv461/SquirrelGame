package com.ivj.squirrelgame.board;

import java.io.BufferedReader;
import java.io.File;

import java.io.FileReader;
import java.io.IOException;

import java.io.PrintWriter;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.BadPlant;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.EntitySet;
import com.ivj.squirrelgame.entity.EntitySetException;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.GoodPlant;
import com.ivj.squirrelgame.entity.MasterSquirrel;
import com.ivj.squirrelgame.entity.Wall;

/**
 * This class represents a 2d Board for a turn based Game, has a container for
 * the entities which is linear, but a 2d representation can be created by
 * flatten method. Also it contains methods to operate on the container and
 * methods for random populating of the Board.
 * 
 * @author
 * @see EntitySet
 *
 */
public class Board {

	private EntitySet entities = new EntitySet();
	private final SecureRandom rnd = new SecureRandom();
	private BoardConfig config;

	boolean positionCheck = true;

	/**
	 * Constructs a Board and populates it by calling populate()
	 */
	public Board(BoardConfig config) {
		this.config = config;

	}

	/**
	 * Adds an entity to internal container entities
	 * 
	 * @param e
	 *            Entity to add
	 * 
	 */
	public void addEntity(Entity e) {

		if (positionCheck) {
			// make a copy to iterate to avoid ConcurrentModificationException
			List<Entity> entitiesCopy = new ArrayList<>();
			entitiesCopy.addAll(entities.getEntites());
			for (Entity entity : entitiesCopy) {
				if (entity != null) {
					if (e.getPos().equals(entity.getPos())) {
						throw new BoardException("Trying to add: "
								+ EntityType.getEntityType(e)
								+ "; Error: field is not empty; on position "
								+ e.getPos().toString() + " there is already a "
								+ EntityType.getEntityType(entity).toString());
					}
				}
			}
		}
		entities.addEntity(e);
	}

	/**
	 * Removes the entity with the given ID from the internal container
	 * entities, does nothing if no such entity with this given ID exists
	 * 
	 * @param e
	 *            Entity to remove
	 * 
	 */
	public void removeEntity(UUID id) {
		entities.removeEntity(id);
	}

	public BoardConfig getConfig() {
		return config;
	}

	/**
	 * Adds new created entity, doesn't add MiniSquirrel, doesn't add if type is
	 * null
	 * 
	 * @param type
	 *            of Entity
	 * 
	 * @param pos
	 *            of Entity
	 * @return entity created
	 * 
	 */
	// TODO refactor to entityFactory
	public Entity addNewEntity(EntityType type, XY pos) {
		Entity e = null;
		if (type == EntityType.WALL)
			e = new Wall(pos);
		else if (type == EntityType.MASTER_SQUIRREL)
			e = new MasterSquirrel(pos);
		else if (type == EntityType.GOOD_PLANT)
			e = new GoodPlant(pos);
		else if (type == EntityType.BAD_PLANT)
			e = new BadPlant(pos);
		else if (type == EntityType.GOOD_BEAST)
			e = new GoodBeast(pos);
		else if (type == EntityType.BAD_BEAST)
			e = new BadBeast(pos);
		else
			;

		if (e != null) {
			addEntity(e);
		}

		return e;
	}

	/**
	 * Adds new created entity on random position , doesn't add MiniSquirrel,
	 * doesn't add if type is null
	 * 
	 * @param type
	 *            of Entity
	 * 
	 * 
	 * @return entity created
	 * 
	 */
	public Entity addNewEntityOnRandomPosition(EntityType type) {
		return addNewEntity(type, getRandomPosition());
	}

	/**
	 * Creates and return a FlattenedBoard populated with all entities from
	 * entities container
	 * 
	 * @return created and populated FlattenedBoard
	 */
	public FlattenedBoard flatten() {
		FlattenedBoard fb = new FlattenedBoard(this);
		// make a copy to iterate to avoid ConcurrentModificationException
		List<Entity> entitiesCopy = new ArrayList<>();
		entitiesCopy.addAll(entities.getEntites());

		for (Entity e : entitiesCopy) {
			if (e != null) {
				fb.addEntityInField(e);
			}

		}
		return fb;
	}

	public BoardView getBoardView() {
		return flatten();
	}

	public EntityContext getEntityContext() {
		return flatten();
	}

	/**
	 * Gets a random empty position in the field
	 * 
	 * @return random Position
	 */
	// TODO fix the loop
	// TODO fix the field
	public XY getRandomPosition() {

		XY pos = new XY(rnd.nextInt(config.width - 1),
				rnd.nextInt(config.height - 1));

		FlattenedBoard fb = flatten();
		while (!fb.isEmptyField(pos)) {
			pos = new XY(rnd.nextInt(config.width - 1),
					rnd.nextInt(config.height - 1));
		}

		// TODO fix this test exception
		if (!fb.isEmptyField(pos)) {
			throw new NullPointerException();
		}
		return pos;
	}

	public EntitySet getEntities() {
		return entities;
	}

	public String createEntityConfig() {
		StringBuffer buf = new StringBuffer();
		List<Entity> entitiesCopy = new ArrayList<>();
		entitiesCopy.addAll(entities.getEntites());

		for (Entity e : entitiesCopy) {
			if (e != null) {
				buf.append(EntityType.getEntityType(e).toString() + " "
						+ e.getPos().toString() + "\n");
			}
		}

		return buf.toString();

	}

	public static Path getDefaultPath() {
		FileSystem defaultF = FileSystems.getDefault();
		return defaultF.getPath(System.getProperty("user.dir"),
				"EntitiesConfig.txt");
	}

	public boolean writeEntityConfig(Path p, String config) {
		boolean success = false;
		try (PrintWriter out = new PrintWriter(p.toString())) {
			out.print(config);
			success = true;
		} catch (IOException e) {
			e.printStackTrace();

		}

		return success;
	}

	public boolean loadEntityConfig(Path p) {
		boolean success = false;
		boolean parseError = false;
		try (BufferedReader br = new BufferedReader(
				new FileReader(new File(p.toString())))) {
			for (String line; (line = br.readLine()) != null;) {
				String[] lines = line.split(" ");

				EntityType type;
				try {
					type = EntityType.valueOf(lines[0]);
				} catch (IllegalArgumentException e) {
					parseError = true;
					break;
				}

				String x = lines[2], y = lines[4]; // "WALL x: 42 y: 42"

				try {
					int x_ = Integer.parseInt(x);
					int y_ = Integer.parseInt(y);

					addNewEntity(type, new XY(x_, y_));
				} catch (NumberFormatException e) {
					parseError = true;
					break;
				}

			}

		} catch (IOException e) {
			parseError = true;
		}

		success = parseError;
		return success;
	}

	/**
	 * Calls nextStep method on all entities
	 */
	public void nextStepOnEntities() {
		EntityContext ec = getEntityContext();

		// make a copy to iterate to avoid ConcurrentModificationException
		List<Entity> entitiesCopy = new ArrayList<>();
		entitiesCopy.addAll(entities.getEntites());

		for (Entity e : entitiesCopy) {
			if (e != null) {
				e.nextStep(ec);
			}
		}
	}

	/**
	 * Populates the Board
	 * 
	 */
	public void populate() {

		try {

			edge();
			poulateWithRandomEntities();

		} catch (EntitySetException e) {
			e.printStackTrace();
		}
	}

	private void poulateWithRandomEntities() {
		// must be called first, as it doesn't check for not empty fields
		createWalls();
		for (int i = 0; i < config.goodPlantNum; i++) {
			addNewEntityOnRandomPosition(EntityType.GOOD_PLANT);
		}

		for (int i = 0; i < config.badPlantNum; i++) {
			addNewEntityOnRandomPosition(EntityType.BAD_PLANT);
		}

		for (int i = 0; i < config.goodBeastNum; i++) {
			addNewEntityOnRandomPosition(EntityType.GOOD_BEAST);
		}

		for (int i = 0; i < config.badBeastNum; i++) {
			addNewEntityOnRandomPosition(EntityType.BAD_BEAST);
		}

	}

	/**
	 * Creates an edging wall, only with width == 1 implemented
	 */
	private void edge() {
		// first the horizontal walls
		for (int i = 0; i < config.width; i++) {
			// up
			addNewEntity(EntityType.WALL, new XY(i, 0));
			// down
			addNewEntity(EntityType.WALL, new XY(i, config.height - 1));

		}

		// now vertical
		for (int j = 1; j < config.height - 1; j++) {

			// up
			addNewEntity(EntityType.WALL, new XY(0, j));

			// down
			addNewEntity(EntityType.WALL, new XY(config.width - 1, j));

		}
	}

	private void createWalls() {

		XY outerBouds = new XY(config.width - 2, config.height - 2);
		boolean grid[][] = new boolean[outerBouds.x][outerBouds.y];
		boolean locked = true;
		XY pos = null;
		boolean v = false;
		int j = 0;
		int maxLocking = 20;
		int i = 0;
		while (true) {
			v = rnd.nextInt(2) == 1;

			pos = getEmptyRandomPosition(grid,
					new XY(outerBouds.x - 1, outerBouds.y - 1));

			locked = true;
			while (locked) {
				if (v) {
					pos = new XY(pos.x + 1, pos.y);
				} else {
					pos = new XY(pos.x, pos.y + 1);
				}
				if (pos.x > outerBouds.x - 1 || pos.y > outerBouds.y - 1) {
					// l = !l;
					j = 0;
					locked = false;
					continue;
				} else {
					j++;
					if (j > maxLocking || j % (3 + rnd.nextInt(5)) == 0) {
						j = 0;
						locked = false;
					}

					if (grid[pos.x][pos.y] != true) {
						grid[pos.x][pos.y] = true;

						if (i >= config.wallNum) {
							break;
						}
						i++;
					}

				}

			}
			if (i >= config.wallNum) {
				break;
			}

		}

		for (int x = 0; x < outerBouds.x; x++) {
			for (int y = 0; y < outerBouds.y; y++) {
				if (grid[x][y]) {
					addNewEntity(EntityType.WALL, new XY(x + 1, y + 1));

				}
			}
		}

	}

	public XY getEmptyRandomPosition(boolean[][] array, XY dimensions) {
		XY pos = new XY(rnd.nextInt(dimensions.x), rnd.nextInt(dimensions.y));

		while (array[pos.x][pos.y]) {
			pos = new XY(rnd.nextInt(dimensions.x - 1),
					rnd.nextInt(dimensions.y - 1));
		}
		return pos;
	}

	@Override
	public String toString() {
		String out = "";
		out += "width: " + config.width + "heght: " + config.height + "\n";
		out += "entities: \n";
		out += entities.toString();
		return out;

	}

	public void reset() {
		entities = new EntitySet();

	}

}
