package main;

import entities.Shark;
import objects.Coin;
import objects.Question;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static constants.ObjectConstants.ObjectType.COIN_TYPE;
import static constants.ObjectConstants.ObjectType.QUESTION_TYPE;
import static main.Game.TILES_SIZE;

/**
 * This class constructs a level which contains levelData, a 2D array
 */
public class Level {

    // ====== Red Values ======
    public static final int DOWN_SLOPE = 6;
    public static final int UP_SLOPE = 7;
    public static final int TRANSPARENT_TILE = 90;
    public static final ArrayList<Integer> transparentTiles = new ArrayList<>(Arrays.asList(TRANSPARENT_TILE, DOWN_SLOPE, UP_SLOPE));

    // ====== Green Values ======
    private static final int SHARK = 0;
    private static final int PLAYER	= 1;
    private static final int FINAL_POINT = 2;

    // ====== Blue Values ======
    public static final int COIN = 0;
    public static final int QUESTION = 1;

    // ====== Enemies and objects ======
    private final List<Shark> sharks = new ArrayList<>();
    private final List<Coin> coins = new ArrayList<>();
    private final List<Question> questions = new ArrayList<>();

    // ====== Level data ======
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

                // Set red data
                levelData[y][x] = red;

                // Set green data
                if (green == PLAYER)
                    spawnPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == FINAL_POINT)
                    finalPoint = new Point(x * TILES_SIZE, y * TILES_SIZE);
                if (green == SHARK)
                    sharks.add(new Shark(x * TILES_SIZE, y * TILES_SIZE));

                // Set blue data
                if (blue == COIN)
                    coins.add(new Coin(x * TILES_SIZE, y * TILES_SIZE, COIN_TYPE));
                if (blue == QUESTION)
                    questions.add(new Question(x * TILES_SIZE, y * TILES_SIZE, QUESTION_TYPE));

                // Print level data
//                System.out.printf("%02d ", levelData[y][x]);
            }
//    	    System.out.println(" ");
        }
    }

    // ====== Getters ======

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
