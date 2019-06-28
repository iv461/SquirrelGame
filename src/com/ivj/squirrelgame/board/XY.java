package com.ivj.squirrelgame.board;

import de.hsa.games.fatsquirrel.botimpls.azardBot.XYSupport;

/**
 * Class representing a 2d vector with integers as coordinates. Contains methods
 * for basic vector operations.
 * 
 * @author
 *
 */
public class XY {

	public final int x;
	public final int y;

	public static final XY ZERO_ZERO = new XY(0, 0);
	public static final XY UP = new XY(0, -1);
	public static final XY DOWN = new XY(0, 1);
	public static final XY RIGHT = new XY(1, 0);
	public static final XY LEFT = new XY(-1, 0);
	public static final XY RIGHT_UP = new XY(1, -1);
	public static final XY LEFT_UP = new XY(-1, -1);
	public static final XY RIGHT_DOWN = new XY(1, 1);
	public static final XY LEFT_DOWN = new XY(-1, 1);

	public XY(int x, int y) {
		this.x = x;
		this.y = y;
	}

	/**
	 * Vector addition
	 * 
	 * @param vec
	 *            to add
	 * @return resulting vector
	 */
	public XY plus(XY vec) {
		return new XY(this.x + vec.x, this.y + vec.y);
	}

	/**
	 * Vector subtraction
	 * 
	 * @param vec
	 *            to subtract
	 * @return resulting vector
	 */
	public XY minus(XY vec) {
		return new XY(this.x - vec.x, this.y - vec.y);
	}

	/**
	 * Calculates the length of the vector from the passed position to this
	 * position
	 * 
	 * @param pos
	 *            origin of vector which length is calculated
	 * @return length of vector
	 */
	public double distanceFrom(XY pos) {
		XY v = XYSupport.vec(pos, this);
		return v.length();
	}

	/**
	 * Calculates length of this vector
	 * 
	 * @return length of vector
	 */
	public double length() {
		return Math.sqrt(this.x * this.x + this.y * this.y);
	}

	@Override
	public String toString() {
		return "x: " + x + " y: " + y;
	}

	@Override
	public boolean equals(Object o) {
		if (o instanceof XY) {
			return ((XY) o).x == this.x && ((XY) o).y == this.y;
		}
		return false;
	}

	@Override
	public int hashCode() {
		return Integer.hashCode(x) + Integer.hashCode(y) % Integer.MAX_VALUE;
	}

}
