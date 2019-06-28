package com.ivj.squirrelgame.ui;

import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;

import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.security.SecureRandom;
import java.util.HashMap;

import java.util.Map;
import java.util.UUID;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.board.FlattenedBoard;
import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.commandParser.Command;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.core.GameCommandType;

import javafx.application.*;
import javafx.scene.image.*;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.canvas.*;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.scene.paint.*;

/**
 * An implementation of UI interface, needed to render the game board with the
 * entities. Based on JavaFX
 * 
 * @author
 * @see UI
 *
 */
@SuppressWarnings("restriction")
public class FxUI extends Scene implements UI {

	private boolean drawTestures = true;
	private static final int CELL_SIZE = 32;
	private static final int ARCH_SIZE = 10;
	private Canvas boardCanvas;
	private Label msgLabel;
	public Command lastCommand = new Command(null, (Object[]) null);

	private Image masterSquirrelImage;
	private Image miniSquirrelImage;
	private Image badBeastImage;
	private Image goodBeastImage;
	private Image badPlantImage;
	private Image goodPlantImage;
	private Image grassImage;
	private Image wallImage;

	// colors for multiplayer game
	private Map<UUID, Color> playerColors = new HashMap<>();
	// for saving given colors
	private Map<Color, Boolean> givenColors = new HashMap<>();

	private boolean multiplayerGame = false;
	private boolean initState = true;

	private SecureRandom rnd = new SecureRandom();

	private Logger logger = Logger.getLogger(FlattenedBoard.class.getName());
	private Color[] colors = { Color.BLUE, Color.BISQUE, Color.BLACK, Color.RED,
			Color.YELLOW, Color.PINK, Color.CORNFLOWERBLUE, Color.BROWN,
			Color.CYAN, Color.DARKORANGE, Color.SEAGREEN, Color.AQUA,
			Color.DARKMAGENTA };

	public FxUI(Parent parent, Canvas boardCanvas, Label msgLabel) {
		super(parent);
		this.boardCanvas = boardCanvas;
		this.msgLabel = msgLabel;
		loadImages();

		try {
			FileHandler fh;
			// This block configure the logger with handler and formatter
			Path logPath = FileSystems.getDefault()
					.getPath(System.getProperty("user.dir"), "FxUI_Log.txt");
			fh = new FileHandler(logPath.toString());
			logger.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);

		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static FxUI createInstance(XY boardSize) {
		Canvas boardCanvas = new Canvas(boardSize.x * CELL_SIZE,
				boardSize.y * CELL_SIZE);
		Label statusLabel = new Label();
		VBox top = new VBox();
		top.getChildren().add(boardCanvas);

		top.getChildren().add(statusLabel);
		statusLabel.setText("Ready");

		final FxUI fxUI = new FxUI(top, boardCanvas, statusLabel);
		setKeyEventHandler(fxUI);

		return fxUI;
	}

	private void loadImages() {

		String path = "/res/";

		try {

			masterSquirrelImage = new Image(
					getClass().getResourceAsStream(path + "squirrel.png"));
			// TODO rescale !
			miniSquirrelImage = masterSquirrelImage;

			badBeastImage = new Image(
					getClass().getResourceAsStream(path + "badBeast.png"));

			goodBeastImage = new Image(
					getClass().getResourceAsStream(path + "goodBeast.png"));
			grassImage = new Image(
					getClass().getResourceAsStream(path + "grass.png"));
			wallImage = new Image(
					getClass().getResourceAsStream(path + "wall.png"));

			goodPlantImage = new Image(
					getClass().getResourceAsStream(path + "goodPlant.png"));

			badPlantImage = new Image(
					getClass().getResourceAsStream(path + "badPlant.png"));
		} catch (NullPointerException ne) {
			ne.printStackTrace();
			drawTestures = false;
		}

	}

	private static void setKeyEventHandler(final FxUI fxUI) {

		fxUI.setOnKeyPressed(new EventHandler<KeyEvent>() {

			@Override
			public void handle(KeyEvent event) {
				KeyCode code = event.getCode();
				switch (code) {
				case UP:
					fxUI.lastCommand.setCommandType(GameCommandType.UP);
					break;
				case DOWN:
					fxUI.lastCommand.setCommandType(GameCommandType.DOWN);
					break;
				case LEFT:
					fxUI.lastCommand.setCommandType(GameCommandType.LEFT);
					break;
				case RIGHT:
					fxUI.lastCommand.setCommandType(GameCommandType.RIGHT);
					break;

				case ESCAPE:
					fxUI.lastCommand.setCommandType(GameCommandType.EXIT);
					break;

				default:
					break;

				}

			}
		});
	}

	public void render(final BoardView view) {

		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				repaintBoardCanvas(view);
			}
		});
	}

	/**
	 * Flips image in one direction
	 * 
	 * @param toFlip
	 * @param horizontal
	 * @return flipped image
	 */
	private Image flipImage(Image toFlip, boolean horizontal) {
		PixelReader pixelReader = toFlip.getPixelReader();

		int width = (int) toFlip.getWidth();
		int height = (int) toFlip.getHeight();

		WritableImage writableImage = new WritableImage(width, height);
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				Color color = pixelReader.getColor(x, y);
				if (horizontal) {
					pixelWriter.setColor(width - x - 1, y, color);
				} else {
					pixelWriter.setColor(x, height - y - 1, color);
				}

			}
		}
		return writableImage;
	}

	/**
	 * Gives random color from prebuild colors
	 * 
	 * @return
	 */
	private Color getRandomColor() {
		if (givenColors.size() == 0) {
			for (Color c : colors) {
				givenColors.put(c, false);
			}
		}
		Color color = colors[rnd.nextInt() * (givenColors.size() - 1)];
		while (givenColors.get(color) == true) {
			color = colors[rnd.nextInt() * (givenColors.size() - 1)];
		}

		givenColors.put(color, true);
		return color;
	}

	/**
	 * Paints a image with color, so it is only in one color c, brightness and
	 * opacity is preserved from original image
	 * 
	 * @param c
	 * @return painted Image
	 */
	private Image paintImage(Image toPaint, Color c) {
		PixelReader pixelReader = toPaint.getPixelReader();

		int width = (int) toPaint.getWidth();
		int height = (int) toPaint.getHeight();

		WritableImage writableImage = new WritableImage(width, height);
		PixelWriter pixelWriter = writableImage.getPixelWriter();

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {

				int pixel = pixelReader.getArgb(x, y);
				int px_op = (pixel & 0xff000000) >>> 24;
				int px_r = (pixel & 0xff0000) >>> 16;
				int px_g = (pixel & 0xff00) >>> 8;
				int px_b = pixel & 0xff;
				double avg = (((px_r + px_g + px_b) / 3.0) / (double) 255.0);
				double op = px_op / (double) 255;
				Color resulting = new Color(c.getRed() * avg,
						c.getGreen() * avg, c.getBlue() * avg, op);
				pixelWriter.setColor(x, y, resulting);

			}
		}
		return writableImage;
	}

	private void repaintBoardCanvas(BoardView view) {
		GraphicsContext gc = boardCanvas.getGraphicsContext2D();
		gc.clearRect(0, 0, boardCanvas.getWidth(), boardCanvas.getHeight());
		XY viewSize = view.getSize();

		if (drawTestures) {
			// to save the squirrels
			Map<XY, UUID> squirrels = new HashMap<>();
			Map<XY, UUID> miniSquirrels = new HashMap<>();

			for (int x = 0; x < viewSize.x; x++) {
				for (int y = 0; y < viewSize.y; y++) {
					XY currPos = new XY(x, y);
					EntityType et = view.getEntityType(currPos);
					Image img = grassImage;

					if (et == EntityType.BAD_BEAST) {
						img = badBeastImage;
					} else if (et == EntityType.GOOD_BEAST) {
						img = goodBeastImage;
					} else if (et == EntityType.BAD_PLANT) {
						img = badPlantImage;
					} else if (et == EntityType.GOOD_PLANT) {
						img = goodPlantImage;
					} else if (et == EntityType.MASTER_SQUIRREL) {
						// TODO handle different colors for multiplayer
						img = masterSquirrelImage;
						UUID id = view.getID(currPos);
						if (initState) {
							squirrels.put(currPos, id);
						} else {
							if (multiplayerGame) {
								Color c = playerColors.get(id);
								img = paintImage(img, c);
							}
						}

					} else if (et == EntityType.MINI_SQUIRREL) {
						img = miniSquirrelImage;
						UUID id = view.getID(currPos);
						if (initState) {

							miniSquirrels.put(currPos, id);
						} else {
							if (multiplayerGame) {
								Color c = playerColors.get(id);
								img = paintImage(img, c);
							}
						}
					} else if (et == EntityType.WALL) {
						img = wallImage;
					}
					XY dir = view.getLastMovingDirection(currPos);
					if (dir != null) {
						if (dir.equals(XY.LEFT)) {
							img = flipImage(img, true);
						}
					}

					if (initState) {
						// TODO FIXXX THIS STATE GARBAGE
						if (squirrels.size() == 1) {
							multiplayerGame = false;
						} else if (squirrels.size() > 1) {
							multiplayerGame = true;
						}

						if (multiplayerGame) {
							if (squirrels.size() > colors.length) {
								logger.log(Level.SEVERE,
										"there can't be more than 12 squirrels currently, we don't have so much colors ..");

							}

							for (UUID id : squirrels.values()) {
								playerColors.put(id, getRandomColor());
							}

						}
						initState = false;
					}

					// draw first the grass
					gc.drawImage(grassImage, x * CELL_SIZE, y * CELL_SIZE,
							CELL_SIZE, CELL_SIZE);
					gc.drawImage(img, x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE,
							CELL_SIZE);

				}
			}
		} else {
			for (int x = 0; x < viewSize.x; x++) {
				for (int y = 0; y < viewSize.y; y++) {
					XY currPos = new XY(x, y);
					EntityType et = view.getEntityType(currPos);
					Color c = Color.WHITE;
					if (et == EntityType.BAD_BEAST) {
						c = Color.RED;
					} else if (et == EntityType.GOOD_BEAST) {
						c = Color.GREENYELLOW;
					} else if (et == EntityType.BAD_PLANT) {
						c = Color.BLUEVIOLET;
					} else if (et == EntityType.GOOD_PLANT) {
						c = Color.CORNFLOWERBLUE;
					} else if (et == EntityType.MASTER_SQUIRREL) {
						c = Color.BLACK;
					} else if (et == EntityType.MINI_SQUIRREL) {
						c = Color.GRAY;
					} else if (et == EntityType.WALL) {
						c = Color.ANTIQUEWHITE;
					}
					gc.setFill(c);
					gc.fillRoundRect(x * CELL_SIZE, y * CELL_SIZE, CELL_SIZE,
							CELL_SIZE, ARCH_SIZE, ARCH_SIZE);
				}
			}
		}

	}

	@Override
	public void message(final String msg) {
		Platform.runLater(new Runnable() {
			@Override
			public void run() {
				msgLabel.setText(msg);
			}
		});
	}

	@Override
	public Command getCommand() {
		return lastCommand;
	}

}