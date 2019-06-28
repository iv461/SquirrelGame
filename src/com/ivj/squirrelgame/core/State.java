package com.ivj.squirrelgame.core;

import java.io.BufferedReader;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;

/**
 * A state of the Game saving the Highscore of multiple players
 * 
 * @author
 * @see Game
 *
 */
public class State {
	private Map<String, List<Integer>> highscores = new HashMap<>();

	protected final static Logger logger = Logger
			.getLogger(State.class.getName());

	public State() {

	}

	public void saveFile(String path) {
		try (OutputStreamWriter writer = new OutputStreamWriter(
				new FileOutputStream(path))) {
			for (Map.Entry<String, List<Integer>> entry : highscores
					.entrySet()) {
				writer.append(";").append(entry.getKey()).append("\n");

				for (Integer points : entry.getValue()) {
					writer.append(points.toString()).append("\n");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public void reset() {
		highscores.clear();
	}

	public void openFile(String path) {
		/// TODO fixx
		try (BufferedReader reader = new BufferedReader(new FileReader(path))) {
			String currentTeam = null;
			String line;

			while ((line = reader.readLine()) != null) {
				if (line.startsWith(";")) {
					currentTeam = line.substring(1);
				} else if (!line.isEmpty()) {
					setHighscore(currentTeam, Integer.valueOf(line));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Adds a new player
	 * 
	 * @param name
	 * @return true if added, false if already exists
	 */
	public boolean addPlayer(String name) {
		if (name == null) {
			throw new NullPointerException();
		}
		if (!highscores.containsKey(name)) {
			highscores.put(name, new ArrayList<>());
			return true;
		}
		return false;
	}

	/**
	 * Adds new highscore, should be called after a new round
	 * 
	 * @param playerName
	 * @param highscore
	 * @return true if added, false if such player does not exist
	 */
	public boolean setHighscore(String playerName, Integer highscore) {
		if (highscores.containsKey(playerName)) {
			highscores.get(playerName).add(highscore);
			return true;
		}
		return false;
	}

	/*
	 * Sorts entries
	 */
	public void sortEntries() {
		for (Map.Entry<String, List<Integer>> entry : highscores.entrySet()) {
			Collections.sort(entry.getValue());
		}
	}

	@Override
	public String toString() {
		StringBuffer out = new StringBuffer();

		for (Map.Entry<String, List<Integer>> entry : highscores.entrySet()) {
			out.append("BotName: " + entry.getKey() + ":\n");

			for (Integer points : entry.getValue()) {
				out.append("        " + points + "\n");
			}
		}
		return out.toString();
	}
}
