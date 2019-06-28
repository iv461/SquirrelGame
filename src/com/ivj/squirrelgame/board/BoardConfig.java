package com.ivj.squirrelgame.board;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.List;
import java.util.Properties;
import static java.lang.Integer.*;

/**
 * This class saves a configuration for a Board class with constants like width,
 * height.
 */
public class BoardConfig {
	private static final int DEFAULT_WIDTH = 80;
	private static final int DEFAULT_HEIGHT = 60;
	// number of entity of this type
	private static final int DEFAULT_GOODPLANTN_NUM = 50;
	private static final int DEFAULT_BADPLANTN_NUM = 10;
	private static final int DEFAULT_GOODPBEAST_NUM = 20;
	private static final int DEFAULT_BADBEAST_NUM = 10;
	private static final int DEFAULT_WALL_NUM = 90;

	public int height;
	public int width;

	public int goodPlantNum;
	public int badPlantNum;
	public int goodBeastNum;
	public int badBeastNum;
	public int wallNum;

	public List<String> botNames;

	public BoardConfig() {
		this(getDefaultPath().toFile());
	}

	public BoardConfig(File f) {
		boolean success = readFromFile(f);
		if (!success) {
			initializeDefaultValues();
		}
	}

	public XY getSize() {
		return new XY(width, height);
	}

	private void initializeDefaultValues() {
		this.height = DEFAULT_HEIGHT;
		this.width = DEFAULT_WIDTH;

		this.goodPlantNum = DEFAULT_GOODPLANTN_NUM;
		this.badPlantNum = DEFAULT_BADPLANTN_NUM;
		this.goodBeastNum = DEFAULT_GOODPBEAST_NUM;
		this.badBeastNum = DEFAULT_BADBEAST_NUM;
		this.wallNum = DEFAULT_WALL_NUM;
	}

	public boolean readFromFile(File f) {
		Properties props = new Properties();

		try (InputStream is = new FileInputStream(f)) {
			props.load(is);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		try {
			height = parseInt(props.getProperty("height"));
			width = parseInt(props.getProperty("width"));

			goodPlantNum = parseInt(props.getProperty("goodPlantNum"));
			badPlantNum = parseInt(props.getProperty("badPlantNum"));
			goodBeastNum = parseInt(props.getProperty("goodBeastNum"));
			badBeastNum = parseInt(props.getProperty("badBeastNum"));
			wallNum = parseInt(props.getProperty("wallNum"));

		} catch (NumberFormatException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public boolean saveToFile(File f) {

		Properties props = new Properties();
		props.setProperty("height", Integer.toString(height));
		props.setProperty("width", Integer.toString(width));

		props.setProperty("goodPlantNum", Integer.toString(goodPlantNum));
		props.setProperty("badPlantNum", Integer.toString(badPlantNum));
		props.setProperty("goodBeastNum", Integer.toString(goodBeastNum));
		props.setProperty("badBeastNum", Integer.toString(badBeastNum));
		props.setProperty("wallNum", Integer.toString(wallNum));

		try (OutputStream out = new FileOutputStream(f);) {
			props.store(out, "");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}

		return true;
	}

	public static Path getDefaultPath() {
		FileSystem defaultF = FileSystems.getDefault();
		return defaultF.getPath(System.getProperty("user.dir"),
				"SquirrelGame_BoardConfig.txt");
	}

}
