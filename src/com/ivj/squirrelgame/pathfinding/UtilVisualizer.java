package com.ivj.squirrelgame.pathfinding;

import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import java.util.logging.StreamHandler;

import com.ivj.squirrelgame.board.FlattenedBoard;
import com.ivj.squirrelgame.board.XY;

import de.hsa.games.fatsquirrel.botimpls.azardBot.AStar;
import de.hsa.games.fatsquirrel.botimpls.azardBot.GridPath;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;

import javafx.scene.paint.*;

/**
 * Visualizer for visualizing a grid. Can visualize AStar and Midpoint Circle
 * algorithm
 * 
 * @author
 * @see AStar
 *
 */
@SuppressWarnings("restriction")
public class UtilVisualizer extends Scene {

	private static final int CELL_SIZE = 15;
	private static final int ARCH_SIZE = 10;
	private static final double PATHMARK_FACTOR = 0.5;
	private static final int BLOCKEDCELLNUM = 100;
	private Canvas boardCanvas;
	private static int GRIDSIZE = 30;
	private XY[][] grid = new XY[GRIDSIZE][GRIDSIZE];
	private SecureRandom rnd = new SecureRandom();
	private List<XY> blockedCellPositions = new ArrayList<>();
	private XY startPos;
	private XY endPos;
	private static final XY BLOCKEDCELL = XY.ZERO_ZERO;

	private static final Logger logger = Logger
			.getLogger(FlattenedBoard.class.getName());
	private static final Handler handler = new StreamHandler(System.out,
			new SimpleFormatter());

	public UtilVisualizer(Parent parent, Canvas boardCanvas, Label msgLabel) {
		super(parent);
		this.boardCanvas = boardCanvas;
		// set up handler
		handler.setLevel(Level.ALL);
		logger.addHandler(handler);
	}

	public static UtilVisualizer createInstance() {
		Canvas boardCanvas = new Canvas(GRIDSIZE * CELL_SIZE,
				GRIDSIZE * CELL_SIZE);
		Label statusLabel = new Label();
		VBox top = new VBox();
		top.getChildren().add(boardCanvas);

		top.getChildren().add(statusLabel);
		statusLabel.setText("Ready");

		final UtilVisualizer UtilVisualizer = new UtilVisualizer(top,
				boardCanvas, statusLabel);

		return UtilVisualizer;
	}

	private void initGrid() {
		startPos = new XY(rnd.nextInt(GRIDSIZE - 1), rnd.nextInt(GRIDSIZE - 1));
		endPos = new XY(rnd.nextInt(GRIDSIZE - 1), rnd.nextInt(GRIDSIZE - 1));
		boolean locked = true;
		XY pos = null;
		boolean v = false;
		int j = 0;
		int maxLocking = 20;
		for (int i = 0; i < BLOCKEDCELLNUM; i++) {
			v = rnd.nextInt(2) == 1;

			pos = new XY(rnd.nextInt(GRIDSIZE - 1), rnd.nextInt(GRIDSIZE - 1));
			locked = true;
			while (locked) {
				if (v) {
					pos = new XY(pos.x + 1, pos.y);
				} else {
					pos = new XY(pos.x, pos.y + 1);
				}
				if (pos.x > GRIDSIZE - 1 || pos.y > GRIDSIZE - 1
						|| pos.equals(startPos) || pos.equals(endPos)) {
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

					grid[pos.x][pos.y] = BLOCKEDCELL;

					blockedCellPositions.add(new XY(pos.x, pos.y));
					i++;
				}

			}

		}

	}

	public void start() {
		final boolean visualizeMidpoint = true;
		if (visualizeMidpoint) {
			List<XY> positions = FlattenedBoard
					.calculateCellsInCircle(new XY(24, 6), 5);
			for (XY pos : positions) {
				if (pos != null) {
					if (grid[pos.x][pos.y] == BLOCKEDCELL) {
						logger.log(Level.WARNING, "Adding Position: "
								+ pos.toString() + ", already there.");
					} else {
						grid[pos.x][pos.y] = BLOCKEDCELL;
						logger.log(Level.INFO,
								"Adding Position: " + pos.toString());
					}

				} else {
					throw new NullPointerException();
				}
			}
			paintSingleCells(positions);
		} else {
			initGrid();
			paintBoard();

			paintPositions();

			List<XY> pathPos = AStar.calculatePath(new XY(GRIDSIZE, GRIDSIZE),
					startPos, endPos, (XY[]) blockedCellPositions
							.toArray(new XY[blockedCellPositions.size()]));

			GridPath path = null;
			if (pathPos == null) {

			} else {
				path = new GridPath(pathPos);
			}

			paintPath(path);

		}

	}

	private void paintPositions() {
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();
		gc.setFill(Color.RED);
		gc.fillRoundRect(startPos.x * CELL_SIZE, startPos.y * CELL_SIZE,
				CELL_SIZE, CELL_SIZE, ARCH_SIZE, ARCH_SIZE);
		// end pos is GREEN
		gc.setFill(Color.GREEN);
		gc.fillRoundRect(endPos.x * CELL_SIZE, endPos.y * CELL_SIZE, CELL_SIZE,
				CELL_SIZE, ARCH_SIZE, ARCH_SIZE);
	}

	private void paintBoard() {
		final boolean paintSlow = true;
		final int FPS = 30;

		new Thread(() -> {
			GraphicsContext gc = boardCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());

			for (int x = 0; x < GRIDSIZE; x++) {
				for (int y = 0; y < GRIDSIZE; y++) {

					XY currCell = grid[x][y];

					Color c = Color.WHITE;

					if (currCell == BLOCKEDCELL) {
						c = Color.BLACK;
					} else {
						c = Color.ALICEBLUE;
					}

					gc.setFill(c);
					gc.fillRoundRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE,
							CELL_SIZE, ARCH_SIZE, ARCH_SIZE);

				}

				if (paintSlow) {
					try {
						Thread.sleep((long) (1 / (double) FPS * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void paintSingleCells(List<XY> positions) {
		final boolean paintSlow = true;
		final int FPS = 30;

		new Thread(() -> {
			GraphicsContext gc = boardCanvas.getGraphicsContext2D();
			gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());
			Color c = new Color((float) 0xFB * 1 / 255.0,
					(float) 0xB8 * 1 / 255.0, (float) 0x21 * 1 / 255.0, 1.0);
			gc.setFill(c);
			for (XY pos : positions) {

				gc.fillRoundRect(pos.x * CELL_SIZE, pos.y * CELL_SIZE,
						CELL_SIZE, CELL_SIZE, ARCH_SIZE, ARCH_SIZE);

				if (paintSlow) {
					try {
						Thread.sleep((long) (1 / (double) FPS * 1000));
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}).start();
	}

	private void paintPath(GridPath p) {
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();
		gc.setFill(Color.AQUAMARINE);
		if (p != null && p.getLength() > 0) {
			XY next = p.getNextPosition();
			while (next != null) {

				gc.fillRoundRect(
						(double) (next.x * CELL_SIZE)
								+ CELL_SIZE * PATHMARK_FACTOR / 2,
						(double) (next.y * CELL_SIZE)
								+ CELL_SIZE * PATHMARK_FACTOR / 2,
						CELL_SIZE * PATHMARK_FACTOR,
						CELL_SIZE * PATHMARK_FACTOR,
						CELL_SIZE * PATHMARK_FACTOR,
						CELL_SIZE * PATHMARK_FACTOR);

				next = p.getNextPosition();
			}
		}
	}
}
