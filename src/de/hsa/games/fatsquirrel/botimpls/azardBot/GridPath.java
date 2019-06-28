package de.hsa.games.fatsquirrel.botimpls.azardBot;

import java.util.List;

import com.ivj.squirrelgame.board.XY;

/**
 * This class implements a helping representation of a list of positions on a
 * board to be able to follow a path and get next direction
 * 
 * @author
 * @see AStar
 * @see AzardBot
 *
 */
public class GridPath {
	private List<XY> links;
	private int currentLink;

	public GridPath(List<XY> links) {
		this.links = links;
		currentLink = 0;
	}

	public XY getNextDirection() {
		if (currentLink + 1 < getLength()) {
			XY direction = XYSupport.vec(links.get(currentLink),
					links.get(currentLink + 1));
			currentLink++;
			return direction;
		} else {
			// end of path reached
			return null;
		}

	}

	public XY getNextPosition() {
		if (currentLink < getLength()) {
			XY position = links.get(currentLink);
			currentLink++;
			return position;
		} else {
			// end of path reached
			return null;
		}

	}

	public int getLength() {
		return links.size();
	}

	public int getCurrLink() {
		return currentLink;
	}

	public XY getEnd() {
		return links.get(getLength() - 1);
	}

	public XY getVecToEnd() {

		XY direction = XYSupport.vec(links.get(currentLink), getEnd());

		return direction;
	}

	public boolean isEndReached() {
		return getLength() == (getCurrLink() + 1);
	}
}