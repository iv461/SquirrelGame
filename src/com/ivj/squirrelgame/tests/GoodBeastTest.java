package com.ivj.squirrelgame.tests;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.GoodBeast;
import com.ivj.squirrelgame.entity.MasterSquirrel;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

public class GoodBeastTest {
	private GoodBeast goodBeastWithSpy;
	private EntityContext ctxMockWithSpy;

	@Before
	public void setUp() {
		// real object with spy
		goodBeastWithSpy = spy(new GoodBeast(new XY(0, 0)));
		// only a mock
		ctxMockWithSpy = mock(EntityContext.class);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testStandingStillNoSquirrel() {
		// set nearestSquirrelEntity to return null
		when(ctxMockWithSpy.nearestSquirrelEntity(
				/* Necessary to be this position? */ goodBeastWithSpy.getPos()))
						.thenReturn(null);
		// now call nextStep
		goodBeastWithSpy.nextStep(ctxMockWithSpy);
		// ensure there was no call to tryMove
		verify(ctxMockWithSpy, times(0)).tryMove(goodBeastWithSpy,
				new XY(0, 0));

	}

	@Test
	public void testStandingStillNoSquirrelinRange() {
		MasterSquirrel masterToReturn = new MasterSquirrel(
				goodBeastWithSpy.getPos().plus(new XY(BadBeast.VIEWDISTANCE + 1,
						BadBeast.VIEWDISTANCE + 1)));
		// set nearestSquirrelEntity to return a Squirrel not in range
		when(ctxMockWithSpy.nearestSquirrelEntity(goodBeastWithSpy.getPos()))
				.thenReturn(masterToReturn);
		// now call nextStep
		goodBeastWithSpy.nextStep(ctxMockWithSpy);
		// ensure there was no call to tryMove
		verify(ctxMockWithSpy, times(0)).tryMove(goodBeastWithSpy,
				new XY(0, 0));

	}

	@Test
	public void testRunningAwaySquirrel() {
		// we need this position later
		XY masterSquirrelPosition = goodBeastWithSpy.getPos().plus(
				new XY(GoodBeast.VIEW_DISTANCE - 3, GoodBeast.VIEW_DISTANCE - 3));

		MasterSquirrel master = new MasterSquirrel(masterSquirrelPosition);
		// set nearestSquirrelEntity to return a Squirrel in Range
		when(ctxMockWithSpy.nearestSquirrelEntity(
				/* Necessary to be this position? */ goodBeastWithSpy.getPos()))
						.thenReturn(master);
		// now call nextStep
		goodBeastWithSpy.nextStep(ctxMockWithSpy);

		// the BadBeast now should have called tryMove with a direction pointing
		// to the
		// MasterSquirrel, let's calculate it
		XY desiredDirection;
		desiredDirection = XYSupport.vec(goodBeastWithSpy.getPos(),
				masterSquirrelPosition);
		desiredDirection = XYSupport.truncateDirection(desiredDirection);

		// capture the parameter to tryMove to be able to verify it
		ArgumentCaptor<XY> XYmatcher = ArgumentCaptor.forClass(XY.class);
		ArgumentCaptor<GoodBeast> GBmatcher = ArgumentCaptor
				.forClass(GoodBeast.class);

		verify(ctxMockWithSpy).tryMove(GBmatcher.capture(),
				XYmatcher.capture());

		XY capturedDirection = XYmatcher.getValue();
		GoodBeast capturedGB = GBmatcher.getValue();

		// check if direction is correct
		assert (capturedDirection.equals(desiredDirection));
		// check if its out mastersquirrel
		assert (capturedGB.equals(goodBeastWithSpy));

	}
}
