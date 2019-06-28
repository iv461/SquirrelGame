package de.hsa.games.fatsquirrel.botimpls.azardBot;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import com.ivj.squirrelgame.board.FlattenedBoard;
import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.botapi.BotController;
import com.ivj.squirrelgame.botapi.ControllerContext;
import com.ivj.squirrelgame.core.EntityType;

public class AzardBot implements BotController {
	private boolean isMiniSquirrel;
	// save remaining steps, -1 is uninitialized, because we have to wait to a
	// call to nextStep to get the steps
	private long remainingSteps = -1;
	// last calculated path
	private GridPath lastPath;
	// if we are going to hunt a moving entity we should recalculate the path on
	// every step
	private boolean shouldRecalcPath;

	private boolean logToConsole = true;
	private static final Logger logger = Logger
			.getLogger(FlattenedBoard.class.getName());

	private boolean loggingEnabled = false;

	private EntityType hunting;

	public AzardBot(boolean isMiniSquirrel) {
		this.isMiniSquirrel = isMiniSquirrel;

		if (!logToConsole) {
			try {
				FileHandler fh;
				// This block configure the logger with handler and
				// formatter
				Path logPath = FileSystems.getDefault().getPath(
						System.getProperty("user.dir"), "FxUI_Log.txt");
				fh = new FileHandler(logPath.toString());
				logger.addHandler(fh);
				SimpleFormatter formatter = new SimpleFormatter();
				fh.setFormatter(formatter);

			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} else {
			Handler handler = new StreamHandler(System.out,
					new SimpleFormatter());
			// set up handler
			handler.setLevel(Level.ALL);
			logger.addHandler(handler);

		}
		if (!loggingEnabled) {
			LogManager.getLogManager().reset();
		}

	}

	@Override
	public void nextStep(ControllerContext ctx) {
		// get remaining steps if not yet initialized
		if (remainingSteps == -1) {
			remainingSteps = ctx.getRemainingSteps();
		}

		if (isMiniSquirrel) {
			nextStepMini(ctx);
		} else {
			nextStepMaster(ctx);
		}

	}

	private void nextStepMaster(ControllerContext ctx) {

		EntityType[][] rectField = getRectField(ctx);

		int width = rectField.length, height = rectField[0].length;
		XY gridSize = new XY(width, height);

		XY currentLocation = ctx.locate();

		int xL = ctx.getViewLowerLeft().x;
		int yR = ctx.getViewUpperRight().y;
		XY currentLocationRel = new XY(currentLocation.x - xL,
				currentLocation.y - yR);

		boolean invalidPos = true;
		if (lastPath != null) {
			XY posOfEntityToHunt = currentLocationRel
					.plus(lastPath.getVecToEnd());
			EntityType currentlyHunting = rectField[posOfEntityToHunt.x][posOfEntityToHunt.y];
			invalidPos = currentlyHunting != hunting;
		}

		if (lastPath == null || invalidPos || shouldRecalcPath) {

			logger.log(Level.INFO,
					"calculating path because "
							+ ((lastPath == null ? "lastPath is null"
									: (lastPath.isEndReached()
											? "end of path is reached"
											: "shouldRecalc is true"))));

			List<XY> blockedCells = new ArrayList<XY>();
			double minDistance = Double.MAX_VALUE;
			XY nearestTargetPos = XY.ZERO_ZERO;

			//
			for (int x = 0; x < width; x++) {
				for (int y = 0; y < height; y++) {
					EntityType type = rectField[x][y];

					XY currentPos = new XY(x, y);
					double distance = XYSupport
							.vec(currentLocationRel, currentPos).length();

					boolean isBlockingType = false;
					boolean isToHuntType = false;
					boolean isMovingToHuntType = false;
					switch (type) {
					case BAD_BEAST:
						isBlockingType = true;
						break;
					case BAD_PLANT:
						isBlockingType = true;
						break;
					case GOOD_BEAST:
						isToHuntType = true;
						isMovingToHuntType = true;
						break;
					case GOOD_PLANT:
						isToHuntType = true;
						break;
					case MASTER_SQUIRREL:
						isBlockingType = true;
						break;
					case MINI_SQUIRREL:
						isBlockingType = true;
						break;
					case NONE:
						break;
					case WALL:
						isBlockingType = true;
						break;
					default:
						break;

					}

					if (isToHuntType && (minDistance > distance)) {

						minDistance = distance;

						nearestTargetPos = currentPos;

						hunting = type;

						// These are moving entities, so we must recalculate
						// the path every time
						if (isMovingToHuntType) {
							// shouldRecalcPath = true;
							logger.log(Level.INFO,
									"going to hunt a moving type");

						} else {
							// reset recalculation
							shouldRecalcPath = false;
							logger.log(Level.INFO,
									"going to hunt a non moving type");
						}

					}

					if (isBlockingType) {
						if (!currentPos.equals(currentLocationRel)) {
							// check if curretn location is not the starting
							// position; this is nessessary as we add
							// masterSquirrels as blocking types
							blockedCells
									.add(new XY(currentPos.x, currentPos.y));
						}

					}

				}
			}

			XY endPos;
			// if a near entity was found
			if (minDistance != Double.MAX_VALUE) {
				endPos = nearestTargetPos;
				List<XY> path = AStar.calculatePath(gridSize,
						currentLocationRel, endPos, (XY[]) blockedCells
								.toArray(new XY[blockedCells.size()]));
				if (path == null) {
					// no possible path
					shouldRecalcPath = true;
					logger.log(Level.INFO, "no possible path found");
				} else {
					lastPath = new GridPath(path);
				}

			} else {
				// lost path. nothing to do.
				lastPath = null;
				logger.log(Level.INFO, "lost path, nothing to do");
			}

		}
		XY direction = XY.ZERO_ZERO;
		if (lastPath != null) {
			direction = lastPath.getNextDirection();
		}

		if (direction != null) {
			ctx.move(direction);
		}

	}

	private void nextStepMini(ControllerContext ctx) {
		// TODO Auto-generated method stub

	}

	/**
	 * Constructs the Rectangle field with EntityTypes from given
	 * ControllerContext
	 * 
	 * @param ctx
	 *            ControllerContext
	 * @return field
	 */
	private EntityType[][] getRectField(ControllerContext ctx) {
		int width = ctx.getViewUpperRight().x - ctx.getViewLowerLeft().x;
		int height = ctx.getViewLowerLeft().y - ctx.getViewUpperRight().y;
		EntityType[][] field = new EntityType[width][height];

		for (int x = 0; x < width; x++) {
			for (int y = 0; y < height; y++) {
				field[x][y] = ctx
						.getEntityAt(new XY(x + ctx.getViewLowerLeft().x,
								y + ctx.getViewUpperRight().y));

			}
		}

		return field;
	}

}
