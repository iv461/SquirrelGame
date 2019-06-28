package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.botapi.BotControllerFactory;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.EntitySet;
import com.ivj.squirrelgame.entity.MasterSquirrelBot;
import com.ivj.squirrelgame.ui.UI;

public class MultiPlayerBotGame extends BotGame {

	// container with copies of the botPlayers
	private EntitySet botPlayers;
	public final int turnsForOneGame = 1000;
	public final int gamesNum = 3;
	private String[] names;
	private BotControllerFactory botControllerFactory;

	boolean writeEnityConfigToFile = false;
	boolean loadEntityConfigFromFile = true;

	public MultiPlayerBotGame(UI ui, BotControllerFactory botControllerFactory,
			String[] names) {
		super(ui);
		this.botControllerFactory = botControllerFactory;
		this.names = names;
		botPlayers = new EntitySet();

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
		ui.render(view);

		int i = 0;
		StringBuffer buf = new StringBuffer();
		buf.append("Highscore: ");
		for (Entity botPlayer : botPlayers.getEntites()) {
			buf.append(names[i] + ": " + botPlayer.getEnergy() + "; ");

		}
		ui.message(buf.toString());
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

					int i = 0;
					for (Entity e : botPlayers.getEntites()) {
						state.setHighscore(names[i], e.getEnergy());
						i++;
					}
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
		for (int i = 0; i < this.names.length; i++) {
			Entity botPlayer = new MasterSquirrelBot(board.getRandomPosition(),
					botControllerFactory, names[i]);

			board.addEntity(botPlayer);
			// save the players
			botPlayers.addEntity(botPlayer);
			state.addPlayer(names[i]);

		}

	}
}
