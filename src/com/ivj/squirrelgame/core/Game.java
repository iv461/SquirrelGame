package com.ivj.squirrelgame.core;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardConfig;
import com.ivj.squirrelgame.board.BoardView;

import com.ivj.squirrelgame.ui.*;

/**
 * Abstract class implementing a game loop
 * 
 * @author
 *
 */
public abstract class Game {
	protected static final double FPS = 10;
	// played turns
	protected int turns;
	// played games
	protected int games;
	protected Board board;
	protected State state;
	protected UI ui;

	protected boolean isGameRunning;

	public Game(UI ui) {

		this.turns = 0;
		// nothing to config, all is default
		board = new Board(new BoardConfig());
		state = new State();
		this.ui = ui;
		this.isGameRunning = false;

	}

	protected abstract void render(final BoardView view);

	protected abstract void processInput();

	protected abstract void update();

	public void start() {
		isGameRunning = true;
	}

	public void pause() {
		isGameRunning = false;
	}

	/**
	 * Start game loop, which runs in own thread
	 */
	public void gameLoopRun() {

		new Thread(() -> {
			while (true) {

				BoardView bv = board.getBoardView();

				processInput();
				update();
				render(bv);

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

	public UI getUI() {
		return ui;
	}

}