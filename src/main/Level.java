package main;

import objects.*;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static constants.ObjectConstants.ObjectType.*;
import static main.Game.TILES_SIZE;

/**
 * This class constructs a level which contains levelData, a 2D array
 */
public class Level {

    // List of all the transparent tiles from the tileset
    public static final ArrayList<Integer> transparentTiles = new ArrayList<>(Arrays.asList(6,7,76,85,85,86,90,92,93,94,95,96,97,98,99));

    // ====== Red Values ======
    public static final int DOWN_SLOPE = 6;
    public static final int UP_SLOPE = 7;

    // ====== Green Values ======
    private static final int SHARK = 0;
    private static final int PLAYER	= 1;
    private static final int FINAL_POINT = 2;
    private static final int PLANT = 3;

    // ====== Blue Values ======
    private static final int COIN = 0;
    private static final int QUESTION = 1;
    private static final int PLATFORM = 2;
    private static final int LAVA = 3;
    private static final int PIPE = 4;

    // ====== Objects ======
    private final List<Coin> coins = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();
    private final List<Platform> platforms = new ArrayList<>();
    private final List<Lava> lava = new ArrayList<>();
    private final List<Pipe> pipes = new ArrayList<>();

    // ====== Enemies ======
    private final List<Shark> sharks = new ArrayList<>();
    private final List<Plant> plants = new ArrayList<>();

    // ====== Other ======
    private int[][] levelData;
    private Point spawnPoint = new Point();
    private Point finalPoint = new Point();
    private final int maxLevelOffset;

    // ====== Constructor ======
    public Level(BufferedImage image) {
        // Init all level data from RGB values
        initLevelData(image);

        // Initialize max level offset for the level; the maximum distance in pixels for the level
        maxLevelOffset = TILES_SIZE * (image.getWidth() - Game.TILES_IN_WIDTH);
    }

    private void initLevelData(BufferedImage image) {
        // Create empty arrays for level data and enemy data
        levelData = new int[image.getHeight()][image.getWidth()];

        // Go through all pixels in the level and add all level data
        for (int y = 0; y < image.getHeight(); y++) {
            for (int x = 0; x < image.getWidth(); x++) {

                // Get color values from each pixel
                Color color = new Color(image.getRGB(x, y));
                int red = color.getRed();
                int green = color.getGreen();
                int blue = color.getBlue();

                // Change red data to transparent tile if drawing with a red value beyond the tileset
                if (red > 100)
                    red = 90;

                // Add red data
                levelData[y][x] = red;

                // Add green data
                if (green == PLAYER)
                    spawnPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == FINAL_POINT)
                    finalPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == SHARK)
                    sharks.add(new Shark(x * TILES_SIZE, y * TILES_SIZE));
                if (green == PLANT)
                    plants.add(new Plant(x * TILES_SIZE, y * TILES_SIZE));

                // Add blue data
                if (blue == COIN)
                    coins.add(new Coin(x * TILES_SIZE, y * TILES_SIZE, COIN_TYPE));
                if (blue == QUESTION)
                    questions.add(new Question(x * TILES_SIZE, y * TILES_SIZE, QUESTION_TYPE));
                if (blue == PLATFORM)
                    platforms.add(new Platform(x * TILES_SIZE, y * TILES_SIZE, PLATFORM_TYPE));
                if (blue == LAVA)
                    lava.add(new Lava(x * TILES_SIZE, y * TILES_SIZE, LAVA_TYPE));
                if (blue == PIPE)
                    pipes.add(new Pipe(x * TILES_SIZE, y * TILES_SIZE, PIPE_TYPE));

                // Print level data
//                System.out.printf("%02d ", levelData[y][x]);
            }
//    	    System.out.println(" ");
        }
    }

    // ====== Getters ======

    public List<Pipe> getPipes() {
        return pipes;
    }

    public List<Plant> getPlants() {
        return plants;
    }

    public List<Lava> getLava() {
        return lava;
    }

    public List<Platform> getPlatforms() {
        return platforms;
    }

    public List<Question> getQuestions() {
        return questions;
    }

    public List<Coin> getCoins() {
        return coins;
    }

    public List<Shark> getSharks() {
        return sharks;
    }

    public int[][] getLevelData() {
	    return levelData;
    }

    public int getMaxLevelOffset() {
	    return maxLevelOffset;
    }

    public Point getFinalPoint() {
	    return finalPoint;
    }

    public Point getSpawnPoint() {
	    return spawnPoint;
    }
}
