package com.ivj.squirrelgame.main;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.ivj.squirrelgame.board.BoardConfig;

import com.ivj.squirrelgame.botapi.BotControllerFactory;
import com.ivj.squirrelgame.core.Game;
import com.ivj.squirrelgame.core.MultiPlayerBotGame;
import com.ivj.squirrelgame.core.SinglePlayerBotGame;
import com.ivj.squirrelgame.core.SinglePlayerGame;
import com.ivj.squirrelgame.entity.EntitySetException;
import com.ivj.squirrelgame.pathfinding.UtilVisualizer;
import com.ivj.squirrelgame.ui.ConsoleUI;
import com.ivj.squirrelgame.ui.FxUI;
import com.ivj.squirrelgame.ui.UI;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

/**
 * Launches the application and creates the game, handles different
 * configurations. Can start a JavaFX application
 * 
 * @author
 * @see FxUI
 *
 */
@SuppressWarnings("restriction")
public class Launcher extends Application {
	private static final Logger logger = Logger
			.getLogger(Launcher.class.getName());

	public static void main(String[] args) throws EntitySetException {
		boolean isJFX = true;

		if (isJFX) {
			Application.launch(args);
		} else {
			createGame(new ConsoleUI());
		}

	}

	@Override
	public void start(Stage primaryStage) {

		final boolean isAStarTest = false;

		UtilVisualizer asV = null;
		Scene scene = null;
		if (isAStarTest) {
			scene = UtilVisualizer.createInstance();
			asV = (UtilVisualizer) scene;
		} else {
			BoardConfig bc = new BoardConfig();
			scene = FxUI.createInstance(bc.getSize());
		}

		primaryStage.setScene(scene);

		if (isAStarTest) {
			primaryStage.setTitle("AStarVisualizer");

		} else {
			primaryStage.setTitle("SquirrelGame");
		}

		scene.getWindow().setOnCloseRequest(new EventHandler<WindowEvent>() {

			@Override
			public void handle(WindowEvent evt) {
				System.exit(-1);
			}
		});

		primaryStage.show();

		if (!isAStarTest) {
			createGame((FxUI) scene);
		} else {
			asV.start();
		}

	}

	private static BotControllerFactory getFactory(String botName) {
		String className = "de.hsa.games.fatsquirrel.botimpls." + botName
				+ ".BotControllerFactoryImpl";
		Class<?> factoryClass;

		try {
			factoryClass = Launcher.class.getClassLoader().loadClass(className);

		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}

		Object factoryInstance;
		try {
			factoryInstance = factoryClass.getConstructor().newInstance();
		} catch (NoSuchMethodException | InstantiationException
				| IllegalAccessException | IllegalArgumentException
				| InvocationTargetException | SecurityException e) {
			e.printStackTrace();
			return null;
		}

		if (!(factoryInstance instanceof BotControllerFactory)) {
			logger.log(Level.SEVERE,
					"could instatiate object of type BotControllerFactory");
			return null;
		}

		return (BotControllerFactory) factoryInstance;

	}

	private static void createGame(UI ui) {

		boolean singleplayerGame = true;
		boolean botGame = true;
		Game game = null;
		String botName = "azardBot";
		BotControllerFactory factory = getFactory(botName);

		if (singleplayerGame) {

			if (botGame) {
				game = new SinglePlayerBotGame(ui, factory);
			} else {
				game = new SinglePlayerGame(ui);
			}
		} else {
			int players = 5;
			// TODO fix,just testing code, all bots are created from same
			// mastercontroller, so they have same names

			List<String> names = new ArrayList<>();
			for (int i = 0; i < players; i++) {
				names.add(botName + i);
			}
			if (botGame) {
				game = new MultiPlayerBotGame(ui, getFactory(botName),
						(String[]) (names.toArray(new String[names.size()])));
			}
		}

		startGame(game);
	}

	private static void startGame(Game game) {
		if (game != null) {
			game.gameLoopRun();
			game.start();
		}
	}

}
