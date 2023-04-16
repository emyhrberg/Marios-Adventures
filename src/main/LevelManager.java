package main;

import helpers.ImageLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static main.Level.LEVEL_MAX_TILES;

/**
 * This class manages all the levels for the game
 * Imports the neccessary sprites for the levels
 * Draws all the tiles from the image of a level
 */
public class LevelManager {

    // ====== Sprite atlas ======
    private static final BufferedImage TILESET = ImageLoader.loadImage("tileset.png");
    private static final int PIXELS_SIZE 	= 16;
    private static final int ROWS			= 10;
    private static final int IMAGES_IN_ROW 	= 10;
    private final BufferedImage[] tileset 		= new BufferedImage[LEVEL_MAX_TILES];

    // ====== List of levels ======
    private List<Level> levels;
    private int levelIndex = 0;

    // ====== Constructor ======
    public LevelManager() {
		initSpriteAtlas();
		initLevels();
    }

    private void initSpriteAtlas() {
		// Loop through all the pixels of the level image
		for (int y = 0; y < ROWS; y++)
			for (int x = 0; x < IMAGES_IN_ROW; x++) {

				// Get current sub-image from the sprite-atlas
				int xIndex = y * IMAGES_IN_ROW + x;

				// Populate the sprite atlas!
				tileset[xIndex] = TILESET.getSubimage(x * PIXELS_SIZE, y * PIXELS_SIZE, PIXELS_SIZE, PIXELS_SIZE);
			}
    }

    private void initLevels() {
		// Create an empty array to store all levels in
		levels = new ArrayList<>();

		// Load the levels!
		for (int i = 1; i < 3; i++) {
			BufferedImage image = loadLevel(i + ".png"); // loads 1.png, 2.png, etc
			levels.add(new Level(image));
		}
    }

    private BufferedImage loadLevel(String fileName) {
		// Try and load the image
		try (InputStream is = LevelManager.class.getResourceAsStream("/levels/" + fileName)) {
			if (is == null) {
				System.err.println("Error: Failed to load the level file. The file or folder path may be incorrect.\n" + fileName);
				System.exit(1);
			} else {
				return ImageIO.read(is);
			}
		} catch (IOException e) {
			System.err.println("\nFailed to load level!\n" + e.getMessage());
		}
		return null;
    }

    // ====== Draw ======

    public void draw(Graphics g, int levelOffset) {
		// Get current level data
		final int[][] levelData = levels.get(levelIndex).getLevelData();

		// Get width and height of the level
		final int levelWidth = levelData[0].length;

		// Loop through all pixels of the level
		for (int j = 0; j < Game.TILES_IN_HEIGHT; j++)
			for (int i = 0; i < levelWidth; i++) {
				// Position of a tile
				int x = Game.TILES_SIZE * i - levelOffset; // add level offset to x
				int y = Game.TILES_SIZE * j;

				// Get index
				int index = levelData[j][i];

				// Draw tiles
				g.drawImage(tileset[index], x, y, Game.TILES_SIZE, Game.TILES_SIZE, null);
			}
    }

    // ====== Getters ======

    public Level getLevel() {
		return levels.get(levelIndex);
    }

    public int getAmountOfLevels() {
		return levels.size();
    }

    public int getLevelIndex() {
		return levelIndex;
    }

    // ====== Setters ======

    public void setLevelIndex(final int levelIndex) {
		this.levelIndex = levelIndex;
    }
}
