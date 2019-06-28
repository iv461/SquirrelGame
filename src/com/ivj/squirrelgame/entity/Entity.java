package com.ivj.squirrelgame.entity;

import java.util.UUID;

import com.ivj.squirrelgame.board.*;
import com.ivj.squirrelgame.core.EntityContext;

/**
 * Abstract class defining a entity for the game with a ID, a energy and a
 * position
 * 
 * @author
 *
 */
public abstract class Entity {

	protected final UUID id;
	protected int energy;
	protected XY pos;

	// moves to skip
	protected int remainingSkippingMoves;

	/**
	 * Default constructor
	 * 
	 * @param id
	 * @param energy
	 * @param pos
	 */
	Entity(UUID id, int energy, XY pos) {

		this.id = id;
		this.energy = energy;
		this.pos = pos;

	}

	/**
	 * Performs next step
	 * 
	 * @param ec
	 */
	public abstract void nextStep(EntityContext ec);

	/*
	 * Updates energy on collision with other entity e, e.g on collision with
	 * bad plant the energy decreases
	 * 
	 * @param e other Entity to collide with
	 * 
	 * @return energy left
	 */
	public int updateEnergyOnCollide(Entity e) {
		if (e != null) {
			updateEnergy(e.getEnergy());

		}
		return getEnergy();
	}

	public void updateEnergy(int delta) {
		energy += delta;

	}

	public UUID getId() {
		return id;
	}

	public int getEnergy() {
		return energy;

	}

	public XY getPos() {
		return pos;

	}

	/**
	 * Moves a entity, truncates the movement of max 1
	 * 
	 * @param direction
	 * @return true if moved, false if not
	 */
	public boolean move(XY direction) {
		if (direction == null) {
			throw new NullPointerException();
		}
		if (remainingSkippingMoves > 0) {
			remainingSkippingMoves--;
			return false;
		}

		pos = getPos().plus(direction);

		return true;
	}

	/**
	 * Wrapper for some entities who die after some operations like losing
	 * energy on walking, should be overwritten if needed
	 * 
	 * @param moves
	 */
	public boolean shouldDie() {
		return false;
	}

	/**
	 * Wrapper for some entities respawn after some operations like the
	 * BadBeast, which bites only 7 times, should be overwritten if needed
	 * 
	 * @param moves
	 */
	public boolean shouldRespawn() {
		return false;
	}

	public void setRemainingSkippingMoves(int moves) {
		remainingSkippingMoves = moves;
	}

	@Override
	public boolean equals(Object e) {
		if (e instanceof Entity) {
			Entity et = ((Entity) e);
			if (this.getId().equals(et.getId())) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int hashCode() {
		return getId().hashCode();
	}

	@Override
	public String toString() {
		return this.getClass().getName() + "ID: " + id + "Energy: " + energy
				+ "Position: " + pos.toString() + "\n";
	}

}
