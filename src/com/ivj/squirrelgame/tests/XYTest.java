package com.ivj.squirrelgame.tests;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.ivj.squirrelgame.board.XY;

public class XYTest {

	private XY xy1;

	@Before
	public void setUp() {
		xy1 = new XY(0, 0);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testPlus() {
		// XY plusXY = new XY(5,5);
		//assert(xy1.plus(plusXY).equals(new XY(5, 5)));

		assertEquals("0+5=5", new XY(5, 5), xy1.plus(new XY(5, 5)));
	}

	@Test
	public void testMinus() {
		assertEquals("0-7=-7", new XY(-7, -6), xy1.minus(new XY(7, 6)));
	}

}
