package com.ivj.squirrelgame.core;

import java.lang.reflect.InvocationTargetException;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.commandParser.Command;
import com.ivj.squirrelgame.commandParser.CommandExecutor;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.HandOperatedMasterSquirrel;
import com.ivj.squirrelgame.entity.MasterSquirrel;
import com.ivj.squirrelgame.entity.NotEnoughEnergyException;
import com.ivj.squirrelgame.ui.UI;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * Game implementation for single player game with a hand operated player
 * 
 * @see Game
 */
public class SinglePlayerGame extends Game {

	private Command lastCommand = new Command(null, (Object[]) null);
	private CommandExecutor cmdExecutor = new CommandExecutor(this);
	private Entity player;
	private Command lastPlayerCommand = new Command(null, (Object[]) null);

	public static final String PLAYERNAME = "Player1";
	boolean writeEnityConfigToFile = false;
	boolean loadEntityConfigFromFile = true;

	public SinglePlayerGame(UI ui) {
		super(ui);

		if (loadEntityConfigFromFile) {
			board.loadEntityConfig(Board.getDefaultPath());

		} else if (writeEnityConfigToFile) {
			initializePlayer();
			board.populate();
			board.writeEntityConfig(Board.getDefaultPath(),
					board.createEntityConfig());
		}
	}

	protected void update() {
		if (super.isGameRunning) {
			board.nextStepOnEntities();
		}
	}

	protected void render(final BoardView view) {
		ui.render(view);
		ui.message("Highscore: " + player.getEnergy());
	}

	/**
	 * Gets command from UI and updates our command which was passed in
	 * Constructor to our player squirrel, so it can access the command
	 */
	protected void processInput() {
		lastCommand = ui.getCommand();

		if (lastCommand.getCommandType() != null) {
			try {
				cmdExecutor.execute(lastCommand);
			} catch (InvocationTargetException | IllegalAccessException
					| NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

	}

	public void showHelp() {

	}

	public void exit() {
		System.exit(0);
	}

	public void all() {

	}

	public void movLeft() {
		lastPlayerCommand.setCommandType(GameCommandType.LEFT);
		lastPlayerCommand.setParams(lastCommand.getParams());
	}

	public void movUp() {
		lastPlayerCommand.setCommandType(GameCommandType.UP);
		lastPlayerCommand.setParams(lastCommand.getParams());
	}

	public void movDown() {
		lastPlayerCommand.setCommandType(GameCommandType.DOWN);
		lastPlayerCommand.setParams(lastCommand.getParams());
	}

	public void movRight() {
		lastPlayerCommand.setCommandType(GameCommandType.RIGHT);
		lastPlayerCommand.setParams(lastCommand.getParams());
	}

	public void spawnMiniSquirrel(int energy) {
		try {
			board.getEntityContext().spawnMiniSquirrel((MasterSquirrel) player,
					XYSupport.getRandomDirection(), energy);
		} catch (NotEnoughEnergyException e) {
			e.printStackTrace();
		}
	}

	public void initializePlayer() {
		// adds a player squirrel, passing reference to command
		player = new HandOperatedMasterSquirrel(board.getRandomPosition(),
				lastPlayerCommand);
		board.addEntity(player);
		state.addPlayer(PLAYERNAME);
	}

}
