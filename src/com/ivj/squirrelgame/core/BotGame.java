package com.ivj.squirrelgame.core;

import java.io.IOException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import com.ivj.squirrelgame.ui.UI;

public abstract class BotGame extends Game {

	Logger logger = Logger.getLogger(BotGame.class.getName());

	public BotGame(UI ui) {
		super(ui);

		try {
			FileHandler fh;
			// This block configure the logger with handler and formatter
			Path logPath = FileSystems.getDefault()
					.getPath(System.getProperty("user.dir"), "BotGameLog.txt");
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

	protected abstract void initializePlayer();

	@Override
	protected void processInput() {
		// empty in bot game
	}

	protected void saveHighscore() {
		state.saveFile(getDefaultPath().toString());
	}

	protected void printAndPrintState() {
		state.sortEntries();
		System.out.print(state.toString());
		logger.log(Level.INFO, state.toString());
	}

	public static Path getDefaultPath() {
		FileSystem defaultF = FileSystems.getDefault();
		return defaultF.getPath(System.getProperty("user.dir"),
				"SquirrelGame_Highscores.txt");
	}

	public void reset() {
		turns = 0;
		// clears the whole board
		board.reset();
		state.reset();
		// initialize players again
		initializePlayer();

	}
}
