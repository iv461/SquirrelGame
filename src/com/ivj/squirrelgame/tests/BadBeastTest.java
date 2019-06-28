package com.ivj.squirrelgame.tests;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;

import com.ivj.squirrelgame.board.XY;
import com.ivj.squirrelgame.core.EntityContext;
import com.ivj.squirrelgame.entity.BadBeast;
import com.ivj.squirrelgame.entity.MasterSquirrel;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

import static org.mockito.Mockito.*;

public class BadBeastTest {
	private BadBeast badBeastWithSpy;
	private EntityContext ctxMockWithSpy;

	@Before
	public void setUp() {
		// real object with spy
		badBeastWithSpy = spy(new BadBeast(new XY(0, 0)));
		// only a mock
		ctxMockWithSpy = mock(EntityContext.class);
	}

	@After
	public void tearDown() {

	}

	@Test
	public void testDying() {

		for (int i = 0; i < BadBeast.BITES; i++) {
			badBeastWithSpy.bite();
			badBeastWithSpy.nextStep(ctxMockWithSpy);
			// verify correct call on kill entity
			if (i < BadBeast.BITES) {
				verify(ctxMockWithSpy, times(0)).kill(badBeastWithSpy);
			} else {
				verify(ctxMockWithSpy, times(1)).kill(badBeastWithSpy);
			}
		}

	}

	@Test
	public void testStandingStillNoSquirrel() {
		// set nearestSquirrelEntity to return null
		when(ctxMockWithSpy.nearestSquirrelEntity(
				/* Necessary to be this position? */ badBeastWithSpy.getPos()))
						.thenReturn(null);
		// now call nextStep
		badBeastWithSpy.nextStep(ctxMockWithSpy);
		// ensure there was no call to tryMove
		verify(ctxMockWithSpy, times(0)).tryMove(badBeastWithSpy, new XY(0, 0));

	}

	@Test
	public void testStandingStillNoSquirrelinRange() {
		MasterSquirrel master = new MasterSquirrel(
				badBeastWithSpy.getPos().plus(new XY(BadBeast.VIEWDISTANCE + 1,
						BadBeast.VIEWDISTANCE + 1)));
		// set nearestSquirrelEntity to return a Squirrel not in range
		when(ctxMockWithSpy.nearestSquirrelEntity(
				/* Necessary to be this position? */ badBeastWithSpy.getPos()))
						.thenReturn(master);
		// now call nextStep
		badBeastWithSpy.nextStep(ctxMockWithSpy);
		// ensure there was no call to tryMove
		verify(ctxMockWithSpy, times(0)).tryMove(badBeastWithSpy, new XY(0, 0));

	}

	@Test
	public void testAtackingSquirrel() {
		// we need this position later
		XY masterSquirrelPosition = badBeastWithSpy.getPos().plus(
				new XY(BadBeast.VIEWDISTANCE - 2, BadBeast.VIEWDISTANCE - 2));
		MasterSquirrel master = new MasterSquirrel(masterSquirrelPosition);
		// set nearestSquirrelEntity to return a Squirrel in range
		when(ctxMockWithSpy.nearestSquirrelEntity(
				/* Necessary to be this position? */ badBeastWithSpy.getPos()))
						.thenReturn(master);
		// now call nextStep
		badBeastWithSpy.nextStep(ctxMockWithSpy);

		// the BadBeast now should have called tryMove with a direction pointing
		// to the
		// MasterSquirrel, let's calculate it
		XY desiredDirection;
		desiredDirection = XYSupport.neg(XYSupport.vec(badBeastWithSpy.getPos(),
				masterSquirrelPosition));
		desiredDirection = XYSupport.truncateDirection(desiredDirection);

		// capture the parameter to tryMove to be able to verify it
		ArgumentCaptor<XY> argumentCaptor = ArgumentCaptor.forClass(XY.class);
		ArgumentCaptor<BadBeast> BBmatcher = ArgumentCaptor
				.forClass(BadBeast.class);
		verify(ctxMockWithSpy).tryMove(BBmatcher.capture(),
				argumentCaptor.capture());
		XY capturedDirection = argumentCaptor.getValue();
		BadBeast capturedBB = BBmatcher.getValue();
		// check if direction is correct
		assert (capturedDirection.equals(desiredDirection));
		assert (capturedBB.equals(badBeastWithSpy));

	}

}
