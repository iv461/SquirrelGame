package com.ivj.squirrelgame.ui;

import com.ivj.squirrelgame.board.BoardView;
import com.ivj.squirrelgame.commandParser.Command;

public interface UI {
	Command getCommand();

	void render(final BoardView view);

	void message(String msg);

}
