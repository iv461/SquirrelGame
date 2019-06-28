package com.ivj.squirrelgame.tests;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.Test;

import com.ivj.squirrelgame.board.Board;
import com.ivj.squirrelgame.board.BoardConfig;
import com.ivj.squirrelgame.board.FlattenedBoard;
import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.Entity;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.MasterSquirrel;

import org.junit.BeforeClass;

import static org.mockito.Mockito.*;

public class FlattenedBoardTest {
	private Board board1;

	@Before
	public void setUp() {
		board1 = new Board(new BoardConfig());

	}

	@After
	public void tearDown() {

	}

	@BeforeClass
	public static void setUpClass() {

	}

	@AfterClass
	public static void tearDownClass() {

	}

	@Test
	public void killTest() {
		FlattenedBoard fb = board1.flatten();
		Entity added = new GoodBeast(board1.getRandomPosition());
		board1.addEntity(added);

		fb.kill(added);

		// Test killing
		assert (board1.getEntities().getEntity(added.getId()) == null);
		assert (fb.getEntity(added.getPos()) == null);

	}

	@Test
	public void killAndReplaceTest() {
		BadBeast bb = spy(new BadBeast(new XY(5, 5)));
		board1.addEntity(bb);
		FlattenedBoard fb = spy(board1.flatten());
		for (int i = 0; i < BadBeast.BITES; i++) {
			bb.bite();
		}

		bb.nextStep(fb);

		verify(fb, times(1)).killAndReplace(bb);

	}

	@Test
	public void damageToMasterTest() {
		MasterSquirrel ms = spy(new MasterSquirrel(new XY(6, 5)));
		int msOldEnergy = ms.getEnergy();
		BadBeast bb = spy(new BadBeast(new XY(5, 5)));
		board1.addEntity(bb);
		board1.addEntity(ms);
		FlattenedBoard fb = spy(board1.flatten());

		// should attack mastersquirrel
		bb.nextStep(fb);

		assert (ms.getEnergy() == (msOldEnergy + bb.getEnergy()));
	}

}
