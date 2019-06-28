package com.ivj.squirrelgame.tests;

import java.io.IOException;

import org.junit.Test;

import com.ivj.squirrelgame.core.BotGame;
import com.ivj.squirrelgame.core.State;

public class StateTest {
	@Test
	public void generateFile() throws IOException {
		State st = new State();
		st.saveFile(BotGame.getDefaultPath().toString());
	}
}
