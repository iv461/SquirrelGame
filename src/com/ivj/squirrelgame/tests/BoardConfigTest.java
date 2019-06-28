package com.ivj.squirrelgame.tests;

import org.junit.Test;

import com.ivj.squirrelgame.board.BoardConfig;

public class BoardConfigTest {

	@Test
	public void createFile() {
		BoardConfig cf = new BoardConfig();
		cf.saveToFile(BoardConfig.getDefaultPath().toFile());
	}
}
