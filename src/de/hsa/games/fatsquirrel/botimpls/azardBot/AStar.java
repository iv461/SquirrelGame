package de.hsa.games.fatsquirrel.botimpls.azardBot;

/*
MIT License

Copyright (c) 2017 Sebastian Lague

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
*/

/*
 * 30.05.2018
 * Changed some things, added additional sanity check for not allowing starting 
 * and ending positions to be marked as blocked
 */
import java.util.*;

import com.ivj.squirrelgame.board.XY;

public class AStar {

	public static final int DIAGONAL_COST = 14;
	public static final int V_H_COST = 10;
	// constant to mark blocked cells
	private static final Cell BLOCKED_CELL = null;
	// Blocked cells are just null Cell values in grid
	private static Cell[][] grid;
	private static PriorityQueue<Cell> open;
	private static boolean closed[][];

	static class Cell {
		int heuristicCost = 0; // Heuristic cost
		int finalCost = 0; // G+H
		int x, y;
		Cell parent;

		Cell(int i, int j) {
			this.x = i;
			this.y = j;
		}

		@Override
		public String toString() {
			return "[" + this.x + ", " + this.y + "]";
		}
	}

	public static void setBlocked(int i, int j) {
		grid[i][j] = BLOCKED_CELL;
	}

	public static void checkAndUpdateCost(Cell current, Cell t, int cost) {

		if (t == BLOCKED_CELL || closed[t.x][t.y]) {
			return;
		}

		int t_final_cost = t.heuristicCost + cost;

		boolean inOpen = open.contains(t);
		if (!inOpen || t_final_cost < t.finalCost) {
			t.finalCost = t_final_cost;
			t.parent = current;
			if (!inOpen) {
				open.add(t);
			}

		}
	}

	public static void runAStar(XY startPos, XY endPos) {

		// add the start location to open list.
		Cell startCell = grid[startPos.x][startPos.y];
		open.add(startCell);

		Cell current;

		while (true) {

			current = open.poll();

			if (current == null) {
				break;
			}

			closed[current.x][current.y] = true;

			if (current.equals(grid[endPos.x][endPos.y])) {
				return;
			}

			Cell t;
			if (current.x - 1 >= 0) {
				t = grid[current.x - 1][current.y];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);

				if (current.y - 1 >= 0) {
					t = grid[current.x - 1][current.y - 1];
					checkAndUpdateCost(current, t,
							current.finalCost + DIAGONAL_COST);
				}

				if (current.y + 1 < grid[0].length) {
					t = grid[current.x - 1][current.y + 1];
					checkAndUpdateCost(current, t,
							current.finalCost + DIAGONAL_COST);
				}
			}

			if (current.y - 1 >= 0) {
				t = grid[current.x][current.y - 1];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
			}

			if (current.y + 1 < grid[0].length) {
				t = grid[current.x][current.y + 1];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);
			}

			if (current.x + 1 < grid.length) {
				t = grid[current.x + 1][current.y];
				checkAndUpdateCost(current, t, current.finalCost + V_H_COST);

				if (current.y - 1 >= 0) {
					t = grid[current.x + 1][current.y - 1];
					checkAndUpdateCost(current, t,
							current.finalCost + DIAGONAL_COST);
				}

				if (current.y + 1 < grid[0].length) {
					t = grid[current.x + 1][current.y + 1];
					checkAndUpdateCost(current, t,
							current.finalCost + DIAGONAL_COST);
				}
			}
		}
	}

	/**
	 * Calculates the shortest path
	 * 
	 * @param gridSize
	 *            Size of the Grid
	 * @param startPos
	 *            Starting Position of the path
	 * @param endPos
	 *            End Position of the path
	 * @param blocked
	 *            Array of blocked Positions Array of positions which are
	 *            blocked
	 * @return Ordered Positions, which represent the shortest path or null if
	 *         there is no possible path, beginning with including the startPos,
	 *         ending with including the endPos
	 */
	public static List<XY> calculatePath(XY gridSize, XY startPos, XY endPos,
			XY[] blocked) {

		if (gridSize == null || startPos == null || endPos == null
				|| blocked == null) {
			throw new NullPointerException();
		}
		// Init
		grid = new Cell[gridSize.x][gridSize.y];
		closed = new boolean[gridSize.x][gridSize.y];

		open = new PriorityQueue<>((Object o1, Object o2) -> {
			Cell c1 = (Cell) o1;
			Cell c2 = (Cell) o2;

			return c1.finalCost < c2.finalCost ? -1
					: c1.finalCost > c2.finalCost ? 1 : 0;
		});

		for (int i = 0; i < gridSize.x; ++i) {
			for (int j = 0; j < gridSize.y; ++j) {
				grid[i][j] = new Cell(i, j);
				grid[i][j].heuristicCost = Math.abs(i - endPos.x)
						+ Math.abs(j - endPos.y);
			}
		}
		grid[startPos.x][startPos.y].finalCost = 0;

		/*
		 * Set blocked cells. Simply set the cell values to null for blocked
		 * cells.
		 */
		for (int i = 0; i < blocked.length; ++i) {
			XY pos = blocked[i];
			if (pos.equals(startPos) || pos.equals(endPos)) {
				throw new IllegalArgumentException(
						"starting and ending Position should not be blocked");
			}
			setBlocked(blocked[i].x, blocked[i].y);
		}

		runAStar(startPos, endPos);

		// Trace back the path
		List<XY> path = new ArrayList<>();
		if (closed[endPos.x][endPos.y]) {

			Cell current = grid[endPos.x][endPos.y];
			path.add(new XY(current.x, current.y));
			while (current.parent != null) {
				current = current.parent;
				path.add(new XY(current.x, current.y));
			}

			// reverse order to start from first cell
			List<XY> newlist = path.subList(0, path.size());
			Collections.reverse(newlist);
			path = newlist;

		} else {
			// No possible path
			return null;
		}

		return path;
	}

	public void displayMap(XY startPos, XY endPos, Cell[][] grid, XY gridSize) {
		// Display initial map
		System.out.println("Grid: ");
		for (int x = 0; x < gridSize.x; ++x) {
			for (int y = 0; y < gridSize.y; ++y) {
				if (x == startPos.x && y == startPos.y)
					System.out.print("SO  "); // Source
				else if (x == endPos.x && y == endPos.y)
					System.out.print("DE  "); // Destination
				else if (grid[x][y] != BLOCKED_CELL)
					System.out.printf("%-3d ", 0);
				else
					System.out.print("BL  ");
			}
			System.out.println();
		}
		System.out.println();
	}

	public void displayScores(Cell[][] grid, XY gridSize) {
		System.out.println("\nScores for cells: ");
		for (int i = 0; i < gridSize.x; ++i) {
			for (int j = 0; j < gridSize.y; ++j) {
				if (grid[i][j] != BLOCKED_CELL)
					System.out.printf("%-3d ", grid[i][j].finalCost);
				else
					System.out.print("BL  ");
			}
			System.out.println();
		}
		System.out.println();
	}

}