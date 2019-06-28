package com.ivj.squirrelgame.ui;

/**
 * An implementation of UI interface, needed to render the game board with the
 * entities. Prints on Console
 * 
 * @author
 * @see UI
 *
 */
import java.io.BufferedReader;
import java.io.InputStreamReader;

import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.commandParser.Command;
import com.ivj.squirrelgame.commandParser.CommandScanner;
import com.ivj.squirrelgame.commandParser.ScanException;
import com.ivj.squirrelgame.core.EntityType;
import com.ivj.squirrelgame.core.GameCommandType;

public class ConsoleUI implements UI {
	CommandScanner cmdScanner = new CommandScanner(GameCommandType.values(),
			new BufferedReader(new InputStreamReader(System.in)));

	Command lastCommand = new Command(null, (Object[]) null);

	public ConsoleUI() {
		process();
	}

	@Override
	public Command getCommand() {
		return lastCommand;
	}

	@Override
	public void render(BoardView view) {
		StringBuffer out = new StringBuffer();
		for (int height = 0; height < view.getSize().y; height++) {
			for (int width = 0; width < view.getSize().x; width++) {
				EntityType entityType = view
						.getEntityType(new XY(width, height));
				switch (entityType) {
				case WALL:
					out.append("W\t");
					break;
				case BAD_BEAST:
					out.append("K\t");
					break;
				case GOOD_BEAST:
					out.append("B\t");
					break;
				case BAD_PLANT:
					out.append("V\t");
					break;
				case GOOD_PLANT:
					out.append("P\t");
					break;
				case MASTER_SQUIRREL:
					out.append("S\t");
					break;
				case MINI_SQUIRREL:
					out.append("M\t");
					break;
				default:
					out.append(".\t");
					break;
				}

			}
			out.append("\n");
		}
		System.out.println(out.toString());
	}

	@Override
	public void message(String msg) {
		System.out.println(msg);

	}

	private void process() {
		Thread loopThread = new Thread(new Runnable() {

			@Override
			public void run() {
				while (true) {

					try {
						lastCommand = cmdScanner.next();
					} catch (ScanException e) {
						e.printStackTrace();
					}
				}
			}

		});
		loopThread.start();

	}
}
