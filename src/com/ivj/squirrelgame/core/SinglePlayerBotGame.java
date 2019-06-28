package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.botapi.BotControllerFactory;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.MasterSquirrelBot;
import com.ivj.squirrelgame.ui.UI;

/**
 * Game implementation for single player game with a Bot player
 * 
 * @see Game
 */
public class SinglePlayerBotGame extends BotGame {

	private Entity botPlayer;
	public static final String PLAYERNAME = "BotPlayer1";
	public static final int turnsForOneGame = 1000;
	public static final int gamesNum = 3;
	private BotControllerFactory botControllerFactory;
	boolean writeEnityConfigToFile = true;
	boolean loadEntityConfigFromFile = true;

	public SinglePlayerBotGame(UI ui,
			BotControllerFactory botControllerFactory) {
		super(ui);
		this.botControllerFactory = botControllerFactory;

		if (loadEntityConfigFromFile) {
			board.loadEntityConfig(Board.getDefaultPath());

		} else if (writeEnityConfigToFile) {
			initializePlayer();
			board.populate();
			board.writeEntityConfig(Board.getDefaultPath(),
					board.createEntityConfig());
		}
	}

	@Override
	protected void render(BoardView view) {
		// no need to render if game is not running
		if (super.isGameRunning) {
			ui.render(view);
			// TODO fetch the player
			// ui.message("Highscore: " + botPlayer.getEnergy());
		}

	}

	@Override
	protected void update() {
		if (super.isGameRunning) {
			board.nextStepOnEntities();
		}
	}

	@Override
	// Override to provide finite number of turns after which the game stops
	public void gameLoopRun() {

		new Thread(() -> {
			while (true) {

				BoardView bv = board.getBoardView();

				processInput();
				update();
				render(bv);

				if (turns >= turnsForOneGame) {
					// TODO fetch the player
					// state.setHighscore(PLAYERNAME, botPlayer.getEnergy());
					// ui.message("Finished. Highscore: " +
					// botPlayer.getEnergy());
					saveHighscore();
					printAndPrintState();
					reset();

					if (games >= gamesNum) {
						state.saveFile(getDefaultPath().toString());
						return;
					}
					games++;
				}
				// count turns
				turns++;

				try {
					Thread.sleep((long) (1 / (double) FPS * 1000));
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

		}).start();

	}

	@Override
	protected void initializePlayer() {
		botPlayer = new MasterSquirrelBot(board.getRandomPosition(),
				botControllerFactory, PLAYERNAME);
		board.addEntity(botPlayer);

	}

}
