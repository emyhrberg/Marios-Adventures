package main;

import helpers.ImageLoader;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import static ui.Menu.TILES_IN_HEIGHT;
import static ui.Menu.TILES_SIZE;

/**
 * This class manages all the levels for the game
 * Imports the necessary sprites for the levels
 * Draws all the tiles from the image of a level
 */
public class LevelManager {

    // ====== Tileset ======
    private static final BufferedImage TILES 	= ImageLoader.loadImage("/sprites/tiles.png");
    private static final BufferedImage TILES_NUM = ImageLoader.loadImage("/sprites/tiles-num.png");
    private static final int PIXELS_SIZE 		= 16;
    private static final int ROWS				= 10;
    private static final int IMAGES_IN_ROW 		= 10;
    private final BufferedImage[] tilesImages 	= new BufferedImage[100];

    // ====== List of levels ======
    private List<Level> levels;
    private int levelIndex = 0;

    // ====== Constructor ======
    public LevelManager() {
		initSpriteAtlas();
		initLevels();
    }

    private void initLevels() {
		// Create an empty array to store all levels in
		levels = new ArrayList<>();

		for (int i = 0; i < 2; i++) {
			try (InputStream is = LevelManager.class.getResourceAsStream("/levels/" + (i + 1) + ".png")) {

				// handle levels not found
				if (is == null) {
					System.err.println("Error: failed to load level image from: \n" + "/levels/" + (i + 1) + ".png");
					System.exit(1);
				}

				// read the image files and add the levels
				levels.add(new Level(ImageIO.read(is)));

			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void initSpriteAtlas() {
		// Loop through all the pixels of the level image
		for (int y = 0; y < ROWS; y++)
			for (int x = 0; x < IMAGES_IN_ROW; x++) {

				// Get current sub-image from the sprite-atlas
				int xIndex = y * IMAGES_IN_ROW + x;

				// Populate the sprite atlas!
				if (Game.DEBUG) {
					tilesImages[xIndex] = TILES_NUM.getSubimage(x * PIXELS_SIZE, y * PIXELS_SIZE, PIXELS_SIZE, PIXELS_SIZE);
				} else {
					tilesImages[xIndex] = TILES.getSubimage(x * PIXELS_SIZE, y * PIXELS_SIZE, PIXELS_SIZE, PIXELS_SIZE);
				}
			}
	}

    // ====== Draw ======

    public void draw(Graphics g, int levelOffset) {
		// Get current level data
		final int[][] levelData = levels.get(levelIndex).getLevelData();

		// Get width and height of the level
		final int levelWidth = levelData[0].length;

		// Loop through all pixels of the level
		for (int j = 0; j < TILES_IN_HEIGHT; j++)
			for (int i = 0; i < levelWidth; i++) {
				// Position of a tile
				int x = TILES_SIZE * i - levelOffset; // add level offset to x
				int y = TILES_SIZE * j;

				// Get index
				int index = levelData[j][i];

				// Draw tiles
				g.drawImage(tilesImages[index], x, y, TILES_SIZE, TILES_SIZE, null);
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
