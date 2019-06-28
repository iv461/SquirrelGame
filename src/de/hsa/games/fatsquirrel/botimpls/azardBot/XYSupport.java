package de.hsa.games.fatsquirrel.botimpls.azardBot;

import com.ivj.squirrelgame.board.XY;
import static com.ivj.squirrelgame.board.XY.*;

import java.security.SecureRandom;

/**
 * This class provides additional methods for operating on 2d vectors.Unit
 * vector are called here direction as they are used by the game.
 * 
 * @author
 * @see XY
 *
 */
public class XYSupport {

	/**
	 * Truncates vector to one move
	 */
	// TODO how about normalizing it properly ...
	public static XY truncateDirection(XY direction) {
		int x = direction.x, y = direction.y;
		if (x == 0 && y < 0) {
			return UP;
		} else if (x == 0 && y > 0) {
			return DOWN;
		} else if (x > 0 && y == 0) {
			return RIGHT;
		} else if (x > 0 && y > 0) {
			return RIGHT_DOWN;
		} else if (x > 0 && y < 0) {
			return RIGHT_UP;
		} else if (x < 0 && y == 0) {
			return LEFT;
		} else if (x < 0 && y > 0) {
			return LEFT_DOWN;
		} else if (x < 0 && y < 0) {
			return LEFT_UP;
		}
		return ZERO_ZERO;
	}

	/**
	 * Gets direction constants
	 * 
	 * @param selector
	 * @return
	 */
	public static XY inputToDirection(int selector) {
		switch (selector) {
		case 8:
			return UP;
		case 2:
			return DOWN;
		case 6:
			return RIGHT;
		case 4:
			return LEFT;
		case 9:
			return RIGHT_UP;
		case 7:
			return LEFT_UP;
		case 3:
			return RIGHT_DOWN;
		case 1:
			return LEFT_DOWN;
		default:
			return ZERO_ZERO;
		}

	}

	/**
	 * Negate a vector
	 * 
	 * @param vector
	 *            to negate
	 */
	public static XY neg(XY vector) {
		return new XY(-vector.x, -vector.y);
	}

	/**
	 * Returns a random direction
	 * 
	 * @return random direction
	 */
	public static XY getRandomDirection() {
		SecureRandom rnd = new SecureRandom();
		int num = rnd.nextInt(9);
		return inputToDirection(num);
	}

	/**
	 * Calculates vector from 2 points.
	 * 
	 * @param origin
	 *            point
	 * @param destination
	 *            point
	 * @return vector from origin to destination
	 */
	public static XY vec(XY origin, XY destination) {
		return new XY(origin.x - destination.x, origin.y - destination.y);
	}
}
