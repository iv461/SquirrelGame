package com.ivj.squirrelgame.tests;

import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doReturn;

import org.junit.After;
import org.junit.Before;

import org.junit.Test;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardConfig;
import com.ivj.squirrelgame.board.FlattenedBoard;
import com.ivj.squirrelgame.board.XY;

import com.ivj.squirrelgame.entity.GoodPlant;

import com.ivj.squirrelgame.entity.MasterSquirrel;

public class GoodPlantTest {
	private FlattenedBoard fbWithSpy;
	private Board boardWithSpy;

	@Before
	public void setUp() {
		boardWithSpy = new Board(new BoardConfig());
		// real object with spy
		fbWithSpy = spy(new FlattenedBoard(boardWithSpy));

	}

	@After
	public void tearDown() {

	}

	@Test
	public void testRespawn() {
		GoodPlant plant = new GoodPlant(new XY(1, 1));
		doReturn(plant).when(fbWithSpy).getEntity(new XY(1, 1));
		// create a squirrel which collides with the plant
		MasterSquirrel squirrel = new MasterSquirrel(new XY(0, 0));
		int oldEnergy = squirrel.getEnergy();
		fbWithSpy.tryMove(squirrel, new XY(1, 1));

		verify(fbWithSpy, times(1)).killAndReplace(plant);
		assert (squirrel.getEnergy() == oldEnergy + plant.getEnergy());
	}
}
